package com.mygdx.GameWorld;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import com.mygdx.Entities.GameObjects.*;
import com.mygdx.Entities.Joints.*;
import com.mygdx.Entities.Modifiers.*;
import com.mygdx.Renderer.GameRenderer;
import com.mygdx.managers.ClientInterface;
import com.mygdx.managers.Encoder;

public class GameManager {
	
	private float runTime = 0;
	
	// Game controllers
	private GameRenderer renderer;
	private World world;
	private Pinner pinner = new Pinner();
	private Encoder encoder;
	
	// Actors
	private ArrayList<IJoint> joints;
	private ArrayList<Vector2> canvasBounds;
	private ArrayList<IGameObject> points;
	private ArrayList<IModifier> modifiers;
	private ArrayList<Field> fields;
	
	//Server
	public ClientInterface connection;
	
	// Game State
	public GameState currentState;	
	
	// helper for pinning objects to mouse
	private static class Pinner {
		public IGameObject objectToPin;
		public float offset [];
		public float pos [];
		
		public void init(IGameObject object, float [] pos){
			this.pos = pos;
			this.objectToPin = object;
			this.offset = new float [] { pos[0] - objectToPin.getBody().getPosition().x,
										 pos[1] - objectToPin.getBody().getPosition().y};
		}
		
		public void pinObject(){
			objectToPin.getBody().setTransform( pos[0] - offset[0], 
												pos[1] - offset[1], 
												objectToPin.getBody().getAngle());
			objectToPin.getBody().setAwake(true);
		}
		
		public void dispose(){
			objectToPin = null;
		}
	} 
	
	public enum GameState{
		MENU, CREATE, RUNNING, LEVELCHOOSER
	}
	
	public GameManager(World world) throws FileNotFoundException{
		
		this.world = world;
		currentState = GameState.MENU;
		
		try { encoder = new Encoder(); } 
		catch(FileNotFoundException fe) 
		{ 
			Gdx.app.log("FileNotFound", fe.getMessage());
			throw fe; 
		}
		
		//server connect
		//connection = new ClientInterface();
		//connection.connectSocket();
		
		initGameObjects();
		initWall();
	}
	
	
	/*
	 * Init Methods
	 */
	
	private void initWall() {
		
		canvasBounds = new ArrayList<Vector2>();
		
		canvasBounds.add(new Vector2(0,0));
		canvasBounds.add(new Vector2(100,0));
		addChain(canvasBounds);
		canvasBounds.clear();
		
		canvasBounds.add(new Vector2(GameConstants.W_WIDTH,0));
		canvasBounds.add(new Vector2(GameConstants.W_WIDTH,GameConstants.W_HEIGHT));
		addChain(canvasBounds);
		canvasBounds.clear();
		
		canvasBounds.add(new Vector2(GameConstants.W_WIDTH,GameConstants.W_HEIGHT));
		canvasBounds.add(new Vector2(0,GameConstants.W_HEIGHT));
		addChain(canvasBounds);
		canvasBounds.clear();
		
		canvasBounds.add(new Vector2(0,GameConstants.W_HEIGHT));
		canvasBounds.add(new Vector2(0,0));
		addChain(canvasBounds);
		canvasBounds.clear();		
		
	}
	
	private void initGameObjects() {
		points = new ArrayList<IGameObject>();
		joints = new ArrayList<IJoint>();
		modifiers = new ArrayList<IModifier>();
		fields = new ArrayList<Field>();
	}
	
	/*
	 * Update
	 */
	
	public void update(float delta){
		runTime += delta;
		
		switch(currentState){
		case MENU:
			updateMenu(delta);
			break;
		case CREATE:
			updateCreate(delta);
			break;
		case RUNNING:
			updateRunning(delta);
			break;
		default:
			break;
		}	    
	}
	
	// updating running game state
	public void updateRunning(float delta) {
		
		for (IJoint s : joints)
			s.update(points);
		
		for (IModifier m : modifiers)
			m.update();
		
		for (Field f : fields)
			f.update(points);
		
		if (isPinning())
			pinner.pinObject();
		
		world.step(1f/45f, 6, 2); // update GameObjects
	}
	
	// updating creating game state
	public void updateCreate(float delta) {
		if( isPinning() ){
			pinner.pinObject();
		}
	}
	
	// updating menu state
	public void updateMenu(float delta){
		//
	}
	

	/*
	 * Adders
	 */
	
	public void addGameObject(IGameObject gameObject){
		gameObject.initialize(world);
		points.add(gameObject);
	}
	
	/**
	 * Adds a circle GameObject to the world. Can be a static or dynamic body.
	 * @param pos the center of the circle
	 * @param radius radius of the circle
	 * @param pinned whether or not to make static/dynamic
	 */
	
	public void addField(Field field){
		fields.add(field);
	}

	
	/**
	 * Creates a static GameObject chain. Can collide with other game objects, but is pinned.
	 * @param vertices
	 */
	public void addChain(ArrayList<Vector2> vertices){

		Vector2 vert[] = new Vector2[vertices.size()];
		
		for(int i = 0; i < vertices.size(); i++){
			vert[i] = vertices.get(i);
		}
		
		Chain chainBody = new Chain.Constructor(vert).Construct();
		chainBody.initialize(world);
		points.add(chainBody);
	}
	
	public void addVelocityToObject(IGameObject point, float [] endPos){
		Vector2 beginPos = point.getBody().getPosition();
		Velocity impulse = new Velocity(point, beginPos, new Vector2(endPos[0], endPos[1]));
		impulse.initialize();
		modifiers.add(impulse);		
	}
	
	
	public void addVelocityToField(Field field, float [] endPos ){
		
	}
	
	public void addForceToObject(IGameObject point, float [] endPos){
		Vector2 beginPos = point.getBody().getPosition();
		Force force = new Force(point, beginPos, new Vector2( endPos[0], endPos[1]));
		force.initialize();
		modifiers.add(force);
	}
	
	public void addForceToField(Field field, float [] endPos){
		Vector2 beginPos = field.getCenter();
		field.setModifier(new Force(null,beginPos,new Vector2(endPos[0],endPos[1])));
	}
	
	public void addStick(IGameObject p1, IGameObject p2){
		Stick stick = new Stick(p1,p2);
		stick.initialize(world);
		joints.add(stick);
	}
	
	public void addSpring(IGameObject p1, IGameObject p2){
		Spring spring = new Spring(p1,p2,GameConstants.SPRING_CONSTANT);
		joints.add(spring);
	}
	
	// TODO: implement 
	public void rotateObject(int index, float degrees){
		points.get(index).getBody().setTransform(points.get(index).getBody().getPosition().x, 
				points.get(index).getBody().getPosition().y, points.get(index).getBody().getAngle() + degrees);		
	}
	/*
	
	// incoming circles from server are stored in a queue in connection.
	public void processIncomingObjects(){
		while(true){
			Circle newCircle = connection.getCircles();
			if(newCircle == null){
				break;
			}
			addCircle(newCircle.getPos(),newCircle.getRadius());
		}
	}
	*/

	
	/*
	 * Setters
	 */
	
	public void pinObject(IGameObject object, float [] pos){
		pinner.init(object, pos);
	}
	
	public void updatePinner( float [] mousePos){
		pinner.pos = mousePos;
	}
	
	public void releaseObject(){
		pinner.dispose();
	}
	
	public void setRenderer(GameRenderer renderer){
		this.renderer = renderer;
	}
	
	public void setToMenu(){
		currentState = GameState.MENU;
	}
	
	public void start(){
		currentState = GameState.RUNNING;
	}
	
	public void create(){
		currentState = GameState.CREATE;
	}
	
	public void save(){
		encoder.load(this);
		encoder.Encode();
		encoder.dispose();
	}
	

	public void toggleCreative(){
		if ( currentState == GameState.RUNNING){
			currentState = GameState.CREATE;
			for ( IModifier m : modifiers){
				if (m instanceof Velocity)
					((Velocity)m).update();
			}
		} else
			currentState = GameState.RUNNING;
	}
	
	public void restart(){
		for(IGameObject point : points){
			world.destroyBody(point.getBody());
		}
		points = null;
		joints = null;
		modifiers = null;
		fields = null;
		initGameObjects();
		initWall();
		renderer.restart();
		currentState = GameState.CREATE;
	}
	
	/*
	 * Getters
	 */
	
	public World getWorld(){
		return world;
	}
	
	public ArrayList<IJoint> getJoints(){
		return joints;
	}
	
	public ArrayList<IGameObject> getPoints(){
		return points;
	}
	
	public ArrayList<IModifier> getModifiers(){
		return modifiers;
	}
	
	public ArrayList<Field> getFields(){
		return fields;
	}
	
	public boolean isMenu(){
		return currentState == GameState.MENU;
	}
	
	public boolean isCreate(){
		return currentState == GameState.CREATE;
	}
	
	public boolean isRunning(){
		return currentState == GameState.RUNNING;
	}


	public boolean isPinning() {
		return pinner.objectToPin != null;
	}
}

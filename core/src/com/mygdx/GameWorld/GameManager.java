package com.mygdx.GameWorld;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import com.mygdx.Entities.GameObjects.*;
import com.mygdx.Entities.Joints.*;
import com.mygdx.Entities.Modifiers.*;
import com.mygdx.InputProcessing.WebSocketManager;
import com.mygdx.Renderer.GameRenderer;
import com.mygdx.XMLService.XMLDeserializer;
import com.mygdx.XMLService.XMLSerializer;

public class GameManager {
	
	private float runTime = 0;
	
	// Game controllers
	private GameRenderer renderer;
	private World world;
	private Pinner pinner = new Pinner();
	private XMLSerializer encoder;
	private XMLDeserializer decoder;
	
	// Actors
	private ArrayList<IJoint> joints;
	private ArrayList<Chain> bounds;
	private ArrayList<IGameObject> points;
	private ArrayList<IModifier> modifiers;
	private ArrayList<Field> fields;
	
	//Server
	public WebSocketManager socketManager;
	
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
			this.offset = new float [] { pos[0] - objectToPin.getPosition().x,
										 pos[1] - objectToPin.getPosition().y};
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
	
	public GameManager(World world) {
		
		this.world = world;
		currentState = GameState.MENU;
		
		//server connect
		socketManager = new WebSocketManager();
		socketManager.connectSocket();
		
		initGameObjects();
		initWall();
	}
	
	
	/*
	 * Init Methods
	 */
	
	private void initWall() {
		
		float [] boundaryCoordinates = new float [] {0,0,GameConstants.W_WIDTH,0,GameConstants.W_WIDTH,GameConstants.W_HEIGHT,0,GameConstants.W_HEIGHT,0,0};
		Chain boundaryObject = new Chain.Constructor(boundaryCoordinates).Construct();
		boundaryObject.initialize(world);
		bounds.add(boundaryObject);
		
	}
	
	private void initGameObjects() {
		points = new ArrayList<IGameObject>();
		joints = new ArrayList<IJoint>();
		modifiers = new ArrayList<IModifier>();
		fields = new ArrayList<Field>();
		bounds = new ArrayList<Chain>();
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
		
		for (IGameObject gameObject : points)
			gameObject.update();
		
		for (IJoint s : joints)
			s.update();
		
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
	
	public void addField(Field field){
		fields.add(field);
	}
	
	public void addVelocity(IGraphObject clickable,float [] endPos){
		Velocity velocity = new Velocity(clickable,new Vector2(endPos[0],endPos[1]));
		velocity.initialize();
		modifiers.add(velocity);
	}
	
	public void addModifier(IModifier modifier){
		modifier.initialize();
		modifiers.add(modifier);
	}
	
	public void addForce(IGraphObject clickable, float [] endPos){
		Force force = new Force(clickable, new Vector2(endPos[0],endPos[1]));
		force.initialize();
		modifiers.add(force);
	}
	
	public void addStick(IGameObject p1, IGameObject p2){
		Stick stick = new Stick(p1,p2);
		stick.initialize(world);
		joints.add(stick);
	}
	
	public void addJoint(IJoint joint){
		joint.initialize(world);
		joints.add(joint);
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
		try { encoder = new XMLSerializer(); } 
		catch(FileNotFoundException fe) 
		{ 
			Gdx.app.log("FileNotFound", fe.getMessage());
		}
		encoder.loadGameEntities(this);
		encoder.serialize();
		encoder.dispose();
	}
	
	public void load(){
		try { decoder = new XMLDeserializer(); }
		catch(FileNotFoundException fe)
		{
			Gdx.app.log("FileNotFound", fe.getMessage());
		}
		decoder.deserialize(this);
		decoder.dispose();
	}
	

	public void toggleCreative(){
		if ( currentState == GameState.RUNNING){
			currentState = GameState.CREATE;
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
	
	public ArrayList<Chain> getBounds(){
		return bounds;
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
	
	public void dispose(){
		socketManager.dispose();
	}
}

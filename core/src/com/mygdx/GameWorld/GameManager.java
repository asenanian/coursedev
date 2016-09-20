package com.mygdx.GameWorld;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;


import com.mygdx.Entities.Chain;
import com.mygdx.Entities.Circle;
import com.mygdx.Entities.Force;
import com.mygdx.Entities.IJoint;
import com.mygdx.Entities.IModifier;
import com.mygdx.Entities.PolyBody;
import com.mygdx.Entities.IGameObject;
import com.mygdx.Entities.Impulse;
import com.mygdx.Entities.Rect;
import com.mygdx.Entities.Rectangle;
import com.mygdx.Entities.Spring;
import com.mygdx.Entities.Stick;
import com.mygdx.Renderer.GameRenderer;
import com.mygdx.managers.ClientInterface;

public class GameManager {
	
	private float runTime = 0;
	
	// Game controllers
	private GameRenderer renderer;
	private World world;
	private Pinner pinner = new Pinner();
	
	// Actors
	private ArrayList<IJoint> joints;
	private Rect BOUNDS;
	private ArrayList<Vector2> canvasBounds;
	private ArrayList<IGameObject> points;
	private ArrayList<IModifier> modifiers;
	
	//Server
	public ClientInterface connection;
	
	// Game State
	public GameState currentState;	
	
	// helper for pinning objects to mouse
	private static class Pinner {
		public int indexPinned = -1;
		public IGameObject objectToPin;
		public float offset [];
		public float pos [];
		
		public void init(int index, float [] pos, ArrayList<IGameObject> points){
			this.pos = pos;
			this.indexPinned = index;
			this.objectToPin = points.get(index);
			this.offset = new float [] { pos[0] - objectToPin.getBody().getPosition().x,
										 pos[1] - objectToPin.getBody().getPosition().y};
		}
		
		public void pinObject(){
			objectToPin.getBody().setTransform( pos[0] - offset[0], pos[1] - offset[1], 
					objectToPin.getBody().getAngle());
			objectToPin.getBody().setAwake(true);
		}
		
		public void dispose(){
			objectToPin = null;
			indexPinned = -1;
		}
	} 
	
	public enum GameState{
		MENU, CREATE, RUNNING, LEVELCHOOSER
	}
	
	public GameManager(World world){
		
		this.world = world;
		currentState = GameState.MENU;
		
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
		BOUNDS = new Rect(
				GameConstants.LEFTWALL,
				GameConstants.FLOOR,
				GameConstants.RIGHTWALL-GameConstants.LEFTWALL,
				GameConstants.CEILING-GameConstants.FLOOR,
									Color.WHITE,true);
		BOUNDS.setOutlineColor(Color.WHITE);
		
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
		world.step(1f/45f, 6, 2);
		updateSprings();
		
		for( IModifier m : modifiers){
			m.update(points);
		}
		
		if ( pinner.indexPinned != -1 ){
			pinner.pinObject();
		}
	}
	
	// updating creating game state
	public void updateCreate(float delta) {
		if( pinner.indexPinned != -1 ){
			pinner.pinObject();
		}
	}
	
	// updating menu state
	public void updateMenu(float delta){
		//
	}
	
	public void updateSprings(){
		for(IJoint s : joints){
			if(s instanceof Spring)
				((Spring)s).update(points);
		}
	}

	/*
	 * Adders
	 */
	
	/**
	 * Adds a circle GameObject to the world. Can be a static or dynamic body.
	 * @param pos the center of the circle
	 * @param radius radius of the circle
	 * @param pinned whether or not to make static/dynamic
	 */
	public void addCircle(float [] pos, float radius, boolean pinned) {
		Circle circle = new Circle.Constructor(pos, radius, pinned).Construct();
		circle.initialize(world);
		points.add(circle);
		//connection.sendMessage("Circle sent");
	}
	
	/**
	 * Adds a rectangle GameObject to the world. Recieves two vertices as input. These serve
	 * as opposite corners to the rectangle. 
	 * @param pos1 corner of the rectangle.
	 * @param pos2 opposite corner of the rectangle
	 * @param pinned make it static/dynamic.
	 */
	public void addRectangle(float [] pos1, float [] pos2, boolean pinned){
		
		float x = pos1[0] > pos2[0] ? pos2[0] : pos1[0];
		float y = pos1[1] > pos2[1] ? pos2[1] : pos1[1];
		float width = pos1[0] > pos2[0] ? pos1[0] - x : pos2[0] - x;
		float height = pos1[1] > pos2[1] ? pos1[1] - y : pos2[1] - y;
		
		Rectangle rectBody = new Rectangle.Constructor(x, y, width, height, pinned).Construct();
		rectBody.initialize(world);
		points.add(rectBody);
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
	
	/**
	 * creates a GameObject polygon from an ArrayList of Vector2. Can be pinned (static body).
	 * @param vertices list of coordinates for each corner of the polygon.
	 * @param pinned will make the body static or dynamic
	 */
	public void addPolygon(ArrayList<Vector2> vertices, boolean pinned){
		
		Vector2 vert[] = new Vector2[vertices.size()];
		
		for(int i = 0; i < vertices.size(); i++){
			vert[i] = vertices.get(i);
		}
		
		PolyBody polygonBody = new PolyBody.Constructor(vert, pinned).Construct();
		polygonBody.initialize(world);
		points.add(polygonBody);
	}
	
	/**
	 * adds a drawable velocity to a given GameObject.
	 * @param index the index of the GameObject.
	 * @param endPos the coordinates of the end of the velocity vector.
	 */
	public void addImpulse(int index, float [] endPos){

		Vector2 beginPos = points.get(index).getBody().getPosition();
		Impulse impulse = new Impulse(index, beginPos, new Vector2(endPos[0], endPos[1]));
		impulse.initialize(points);
		modifiers.add(impulse);		
	}
	
	/**
	 * adds a drawable force to a given GameObject.
	 * @param index the index of the GameObject.
	 * @param endPos the coordinates of the end of the force vector.
	 */
	public void addForce(int index, float [] endPos){
		Vector2 beginPos = points.get(index).getBody().getPosition();
		Force force = new Force(index, beginPos, new Vector2( endPos[0], endPos[1]));
		force.initialize(points);
		modifiers.add(force);		
	}
	
	/**
	 * creates a drawable joint between two GameObjects that restricts the distance between them. 
	 * @param ind1 index of first GameObject
	 * @param ind2 index of second GameObject
	 */
	public void addStick(int ind1, int ind2){
		Stick stick = new Stick(points,ind1,ind2);
		stick.initialize(this);
		joints.add(stick);
	}
	
	/**
	 * creates a drawable linear spring between two GameObjects. 
	 * @param ind1 index of first GameObject
	 * @param ind2 index of second GameObject
	 */
	public void addSpring(int ind1, int ind2) {
		joints.add(new Spring(points, ind1, ind2, GameConstants.SPRING_CONSTANT));
		Gdx.app.log("Manager", "addSpring");
	}
	
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
	
	public void pinObject(int index, float [] pos){
		pinner.init(index, pos, points);
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
	

	public void toggleCreative(){
		if ( currentState == GameState.RUNNING){
			currentState = GameState.CREATE;
			for ( IModifier m : modifiers){
				if (m instanceof Impulse)
					((Impulse)m).update(points);
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
		initGameObjects();
		renderer.restart();
		currentState = GameState.CREATE;
	}
	
	/*
	 * Getters
	 */
	
	public World getWorld(){
		return world;
	}
	
	public Rect getBounds(){
		return BOUNDS;
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
	
	public boolean isMenu(){
		return currentState == GameState.MENU;
	}
	
	public boolean isCreate(){
		return currentState == GameState.CREATE;
	}
	
	public boolean isRunning(){
		return currentState == GameState.RUNNING;
	}
}

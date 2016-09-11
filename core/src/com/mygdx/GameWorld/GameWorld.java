package com.mygdx.GameWorld;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.Entities.Circle;
import com.mygdx.Entities.Point;
import com.mygdx.Entities.Rect;
import com.mygdx.Entities.Spring;
import com.mygdx.managers.ClientInterface;
import com.mygdx.managers.GameInputProcessor;
import com.mygdx.managers.MenuInputProcessor;
import com.mygdx.managers.Mouse;
import com.mygdx.ui.SimpleButton;

public class GameWorld {
	
	private float runTime = 0;
	private float mousePos[] = new float[2];
	private float prevMousePos[] = new float[2];
	
	// Game controllers
	private GameRenderer renderer;
	
	// Actors
	private ArrayList<Point> points;
	private ArrayList<Spring> springs;
	private Rect BOUNDS;

	// Buttons
	private HashMap<String,SimpleButton> toolbar;
	private HashMap<String,SimpleButton> menuButtons;
	
	//Server
	public ClientInterface connection;
	
	// Game State
	public GameState currentState;	
	
	public enum GameState{
		MENU, CREATE, RUNNING, LEVELCHOOSER
	}
	
	public GameWorld(){
		
		currentState = GameState.MENU;
		
		//server connect
		//connection = new ClientInterface();
		//connection.connectSocket();
		
		initBoundaries();
		initGameObjects();
	}
	
	
	/*
	 * Init Methods
	 */
	
	private void initBoundaries() {
		BOUNDS = new Rect(
				GameConstants.LEFTWALL,
				GameConstants.FLOOR,
				GameConstants.RIGHTWALL-GameConstants.LEFTWALL,
				GameConstants.CEILING-GameConstants.FLOOR,
									Color.WHITE,true);
		BOUNDS.setOutlineColor(Color.WHITE);
	}
	
	private void initGameObjects() {
		// Actors
		points = new ArrayList<Point>();
		springs = new ArrayList<Spring>();
	}
	
	public void loadUI(){
		// buttons
		this.toolbar = ((GameInputProcessor)((InputMultiplexer) Gdx.input.getInputProcessor()).getProcessors().get(1)).getToolbar();
		this.menuButtons = ((MenuInputProcessor)((InputMultiplexer) Gdx.input.getInputProcessor()).getProcessors().get(0)).getMenuButtons();
	}
	
	/*
	 * Update
	 */
	
	public void update(float delta){
		runTime += delta;
		mousePos = getMousePos();
		
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
		
	    //reset prevMousePos
	    prevMousePos[0] = mousePos[0];
	    prevMousePos[1] = mousePos[1];
	}
	
	public void updateRunning(float delta) {
		if(Mouse.isDragged()){
			pinMouseToPoint(Mouse.getIndexPinned());
		}
        //update points (move them)
	    updatePoints();
	    //update spring forces/rigid frames
	    for(int i = 0; i < GameConstants.EXPENSIVENESS; i++) {
	    	updateFrames(delta);
	    }
	}
	
	public void updateCreate(float delta) {
		if(Mouse.isDragged() && toolbar.get("MOVE").getClicked()){
			pinMouseToPoint(Mouse.getIndexBegin());
		}
	}
	
	public void updateMenu(float delta){
		if(menuButtons.get("PLAY").getClicked()){
			
			// switch to PLAY state
			currentState = GameState.CREATE;
			
			Gdx.app.log("GameWorld", "Switching to Play State");
			
			// send message to server registering username.
			//connection.userConnect(connection.getUsername());
			
		} else if (menuButtons.get("QUIT").getClicked()) {
			Gdx.app.exit(); //quit game
		}
		
	}
	
	public void updateFrames(float dt) {		
		for (Spring s : springs) {
			s.update(points,dt);
		}
	}
	
	public void updatePoints() {
		//processIncomingObjects();
		ArrayList<Circle> tmpCircles = new ArrayList<Circle>();
		for (int i = 0; i < points.size(); i++) {
			Point tmp;
			if (points.get(i) instanceof Circle) {
				//print("FOUND CIRCLE!!!!!!!!!!!!!");
				tmp = new Circle((Circle)points.get(i));
				tmpCircles.add((Circle)tmp);
			} else {
				//just a point,no collisions
				tmp = new Point(points.get(i));
			}
			tmp.update(GameConstants.GRAVITY);
			points.set(i, tmp);
			//print("pos:"+tmp.getPos()[0]+","+tmp.getPos()[1]+","+tmp.getPos()[2]);
		}
		//check for circle collisions
		//print(tmpCircles.size()+"CIRCLES SIZE");
		for (int i = 0; i < tmpCircles.size()-1; i++) {
			for (int j = i+1; j < tmpCircles.size(); j++) {
				//Circle tmp = new Circle(tmpCircles.get(i));
				tmpCircles.get(i).processCollision(tmpCircles.get(j),GameConstants.GRAVITY);
			}
		}
	}
	
	private float[] getMousePos() {
		//invert y value to make bottom y = 0, not height
		return new float[] {Gdx.input.getX(), GameConstants.HEIGHT-Gdx.input.getY()};
	}
	
	public void togglePointVisibility() {
		for (Point p : points) {
			p.toggleAbstract();
		}
	}
	
	/*
	 * Adders
	 */
	
	// add a circle to the arena. will notify the server.
	public void addCircle(float[] pos, float radius) {
		points.add(new Circle(pos,pos,radius,toolbar.get("PINNED").getClicked()));
		//connection.sendMessage("Circle sent");
	}
	
	public void addSpring(int ind1, int ind2, boolean stick, boolean hidden) {
		float springConstant = stick ? 9999f : GameConstants.SPRING_CONSTANT;
		Spring newSpring = new Spring(points, ind1, ind2,springConstant,hidden);
		springs.add(newSpring);
	}

	public void addPoint(float[] pos, boolean isAbstract) {
		//print("adding point at pos = "+pos[0]+","+pos[1]);
		points.add(new Point(pos, pos, isAbstract,
							toolbar.get("PINNED").getClicked()));
	}
	public void addPoint(float[] pos) {
		points.add(new Point(pos, pos, false, 
				toolbar.get("PINNED").getClicked()));
	}
	
	public void addCurve(float x, float y){

		if(Mouse.getIndexBegin() != -1){
			float[] beginPoint = points.get(Mouse.getIndexBegin()).getPos();
			float[] endPoint = new float [] {x,y};
			float dist = distance(beginPoint,endPoint);
			float angle = getAngle(beginPoint,endPoint);
			
			for( float nDist = GameConstants.STRING_SEGMENT_LENGTH; nDist < dist; nDist += GameConstants.STRING_SEGMENT_LENGTH){
				endPoint[0] = beginPoint[0] + nDist*(float)Math.cos(angle);
				endPoint[1] = beginPoint[1] + nDist*(float)Math.sin(angle);
				if (BOUNDS.containsPos(endPoint)) {
					int i = inPoint(endPoint);
					if (i == -1) {
						//pNew not in point
						Mouse.setIndexEnd(points.size());
	    				addPoint(endPoint,true); //add abstract point
					} else {
						//pNew is in point
						Mouse.setIndexEnd(i);
					}
					addSpring(Mouse.getIndexBegin(),Mouse.getIndexEnd(),
									toolbar.get("STICK").getClicked(),false);
					Mouse.setIndexBegin(Mouse.getIndexEnd());
					Mouse.setIndexEnd(-1);
				}
			}
		}
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
	 * Helpers
	 */
	
	/** Checks if a pos is in a point.
	 * 
	 * @param pos position to check
	 * @return index of point that contains pos. -1 if no point contains pos.
	 */
	private int inPoint(float[] pos) {
		for (Point p : points) {
	    	if (p.containsPos(pos)) {
	    		return points.indexOf(p);
	    	}
		}
		return -1;
	}

	public float getAngle(float[] p1, float[] p2) {
		//radians
		return (float)Math.atan2(p2[1]-p1[1],p2[0]-p1[0]);
	}
	
	public float distance(float[] pos1, float[] pos2) {
		return (float)Math.sqrt(Math.pow(pos1[0]-pos2[0],2) + 
								Math.pow(pos1[1]-pos2[1],2));
	}
	
	public void pinMouseToPoint(int index) {
		if(index == -1) return; //escape
		Point p = points.get(index);
    	p.setPos(mousePos);
    	p.setPrevPos(prevMousePos);
    	points.set(index, p);
	}
	
	/*
	 * Setters
	 */
	public void setToMenu(){
		currentState = GameState.MENU;
	}
	
	public void start(){
		currentState = GameState.RUNNING;
	}
	
	public void create(){
		currentState = GameState.CREATE;
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
	public void toggleCreative(){
		currentState = (currentState == GameState.RUNNING ? GameState.CREATE : GameState.RUNNING);
	}
	
	public void setRenderer(GameRenderer renderer){
		this.renderer = renderer;
	}
	
	/*
	 * Getters
	 */
	public Rect getBounds(){
		return BOUNDS;
	}
	
	public ArrayList<Point> getPoints(){
		return points;
	}
	
	public ArrayList<Spring> getSprings(){
		return springs;
	}
	
	public void restart(){
		Mouse.setPointHovered(-1);
		initGameObjects();
		renderer.restart();
		currentState = GameState.CREATE;
	}
}

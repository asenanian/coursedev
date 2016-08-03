package com.mygdx.game;






/*
 * To do list:
 * -find FileChooser thing to upload images in game
 * -get Text to work
 * -make Levels: aim projectiles, cannon
 * 
 * Later:
 * -figure out how to : deploy HTML project, make server
 */

















import java.util.ArrayList;
import java.util.Arrays;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.entities.Button;
import com.mygdx.entities.MultiDraw;
import com.mygdx.entities.Point;
import com.mygdx.entities.Rect;
import com.mygdx.entities.Spring;
//import com.mygdx.entities.Stick;
import com.mygdx.managers.GameInputProcessor;
import com.mygdx.managers.GameKeys;



public class TestVerlet extends ApplicationAdapter implements MultiDraw {
	public static int WIDTH, HEIGHT;
	public static int FLOOR, CEILING, LEFTWALL, RIGHTWALL;
	public static Rect BOUNDS;
	public static OrthographicCamera cam;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private Button[] buttons;
	private ArrayList<Point> origPoints;
	private ArrayList<Point> points;
	private ArrayList<Spring> origSprings;
	private ArrayList<Spring> springs;
	private float timeConstant = 0.001f;
	private float gravity = 0;//-timeConstant*100f;
	private float k = timeConstant*1000f;
	private float w = 1f; //width of springs
	private int expensiveness = 100; //aka realisticness (1 to 1000 please)
	private Music danubeMusic;
	private Sound scratchSound;
	private boolean audioOn;
	
	public static int MAXPOINTS = 50;
	public static int MAXSPRINGS = 60;
	
	public int numToolButtons = 8;
	public static int PINNEDBUTTON = 0;
	public static int HIDDENBUTTON = 1;
	public static int MOVEBUTTON = 2;
	public static int SPRINGBUTTON = 3;
	public static int STICKBUTTON = 4;
	public static int CURVEBUTTON = 5;
	public static int CIRCLEBUTTON = 6;
	public static int ABSTRACTBUTTON = 7;
	
	public int numGamePlayButtons = 2;
	public static int PLAYPAUSEBUTTON = 8;
	public static int CLEARBUTTON = 9;
	
	public static int CREATING = 0;
	public static int PLAYING = 1;
	
	public int gameState;
	public int indexOfClick = -1;
	public int tmp1stPointIndex = -1;
	public int tmp2ndPointIndex = -1;
	public boolean hovering = false;
	public boolean placedCircle = true;
	//public boolean pinned = false;
	//public boolean moving = false;
	//public boolean makeStick = false;
	public boolean isTouched = true;
	public float[] mousePos = new float[2];
	public float[] prevMousePos = new float[2];
	@Override
	public void create () {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		initBoundaries();
		
		cam = new OrthographicCamera(WIDTH,HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2);
		cam.update();
		Gdx.input.setInputProcessor(new GameInputProcessor());
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		
		scratchSound = loadSound("scratch.mp3");
		danubeMusic = loadMusic("danube.mp3");
		audioOn = true; //toggle audio
		init();
	}
	
	public void initBoundaries() {
		FLOOR = HEIGHT/4;
		CEILING = 3*HEIGHT/4;
		LEFTWALL = WIDTH/4;
		RIGHTWALL = 3*WIDTH/4;
		BOUNDS = new Rect(LEFTWALL,FLOOR,WIDTH,HEIGHT,Color.LIGHT_GRAY,false);
	}
	public void init() {
		initPoints();
	    initSprings();
	    initButtons();
	    gameState = CREATING;
	}
	
	public void initPoints() {
		points = new ArrayList<Point>();
		origPoints = new ArrayList<Point>();
	}
	
	public void initSprings() {
		springs = new ArrayList<Spring>();
		origSprings = new ArrayList<Spring>();
	}
	public void initButtons() {
		//print(indexOfClick);
		buttons = new Button[numToolButtons+numGamePlayButtons];
		int n1 = numToolButtons;
		//pinned tool button
		buttons[PINNEDBUTTON] = new Button("Verlet/pinnedPoint.png",new float[] {50,PINNEDBUTTON*HEIGHT/n1+50},
									60f,60f);
		//normal point tool
		buttons[SPRINGBUTTON] = new Button("Verlet/spring.png", new float[] {50,SPRINGBUTTON*HEIGHT/n1+50},
									60f,60f);
		//stick tool button
		buttons[STICKBUTTON] = new Button("Verlet/stick.png",new float[] {50,STICKBUTTON*HEIGHT/n1+50},
									60f,60f);
		//move tool button
		buttons[MOVEBUTTON] = new Button("Verlet/moveTool.png",new float[] {50,MOVEBUTTON*HEIGHT/n1+50},
									60f,60f);
		//hidden support
		buttons[HIDDENBUTTON] = new Button("Verlet/hiddenStick.png",new float[] {50,HIDDENBUTTON*HEIGHT/n1+50},
									100f,60f);
		//draw line by dragging
		buttons[CURVEBUTTON] = new Button("Verlet/curveTool.png",new float[] {50,CURVEBUTTON*HEIGHT/n1+50},
									50f,50f);
		//draw bicycle-like wheel
		buttons[CIRCLEBUTTON] = new Button("Verlet/circleTool.png",new float[] {50,CIRCLEBUTTON*HEIGHT/n1+50},
									50f,50f);
		//make all points abstract (invisible)
		buttons[ABSTRACTBUTTON] = new Button("Verlet/abstractButton.png",new float[] {50,ABSTRACTBUTTON*HEIGHT/n1+50},
									50f,50f);
		
		//NOW ADD ALL GAMEPLAY BUTTONS
		int n2 = numGamePlayButtons;
		//play/pause button (start it off showing play button)
		buttons[PLAYPAUSEBUTTON] = new Button("Verlet/play.png",new float[] {300,HEIGHT/n1},
									50f,50f);
		//clear screen button
		buttons[CLEARBUTTON] = new Button("Verlet/bomb.png",new float[] {500,HEIGHT/n1},
									50f,50f);
	}
	
	public void restart() {
		//print("RESTARTING\n\n\n\n");
		gameState = CREATING;
		//print("orig pos:"+origPoints.get(0).getPos()[1]);
		//print(origPoints.get(0));
		points = (ArrayList<Point>) origPoints.clone();
		springs = (ArrayList<Spring>) origSprings.clone();
		updateSpringPoints();
	}
	
	public void resize(int width, int height) {
		//TO DO: make fullscreen mode work
		//WIDTH = width;
		//HEIGHT = height;
		//initBoundaries();
	}
	
	public void updatePoints() {
		for (int i = 0; i < points.size(); i++) {
			Point tmp = new Point(points.get(i));
			tmp.update(gravity);
			points.set(i, tmp);
			//points.get(i).update(gravity); //this makes reset button not work
			//print("pos:"+tmp.getPos()[0]+","+tmp.getPos()[1]+","+tmp.getPos()[2]);
		}
	}

	public void updateSpringPoints() {
		for (int i = 0; i < springs.size(); i++) {
			Spring s = springs.get(i);
			if (s.getP1ind() >= points.size() || s.getP2ind() >= points.size()) {
				springs.remove(i);
				i--;
			} else {	
				s.update(points);
			}
		}
	}
	
	public float getAngle(float[] p1, float[] p2) {
		return (float)Math.atan2(p2[1]-p1[1],p2[0]-p1[0]);
	}

	public void updateFrames(float dt) {
		//update spring forces if dealing with Springs
		//update rigid frame if dealing with Sticks
		
		for (Spring s : springs) {
			//update points because of spring tension or rigidity
			points = (ArrayList<Point>) s.updatePoints(points,dt).clone();
		}
		
		updateSpringPoints(); //give springs up-to-date versions of points
	}
	public void print(Object obj) {
		System.out.println(obj);
	}
	public float[] getMousePos() {
		//invert y value to make bottom y = 0, not height
		return new float[] {Gdx.input.getX(), HEIGHT-Gdx.input.getY()};
	}
	
	public void drawButtonSprites(SpriteBatch batch) {
		//draw sprite over the button rectangle
		for (Button b : buttons) {
			b.drawSprites(batch);
		}
	}
	public void drawButtonShapes(ShapeRenderer shapeRenderer) {
		//draw transparent rectangle as button
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.set(ShapeType.Filled);
		for (Button b : buttons) {
			b.drawShapes(shapeRenderer);
		}
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	public void drawBounds(ShapeRenderer shapeRenderer) {
		BOUNDS.draw(shapeRenderer);
	}
	
	public void drawSprings(ShapeRenderer shapeRenderer) {
		shapeRenderer.set(ShapeType.Filled); //for filled rectline
	    for (Spring s : springs) {
    		s.draw(shapeRenderer);
	    }
	}
	public void drawPoints(ShapeRenderer shapeRenderer) {
		//Color color = Color.WHITE;
		shapeRenderer.set(ShapeType.Filled);
		for (Point p : points) {
			p.draw(shapeRenderer);
		}
	}
	public void drawSpringBuilder(ShapeRenderer shapeRenderer) {
		//precondition: tmp1stPointIndex != -1
		float[] pointPos = points.get(tmp1stPointIndex).getPos();
		shapeRenderer.set(ShapeType.Line);
		Color color;
		if (buttons[HIDDENBUTTON].getClicked()) {
			color = Color.WHITE;
		} else if (buttons[STICKBUTTON].getClicked()) {
			color = Color.BLUE;
		} else {
			//normal spring
			color = Color.RED;
		}
		shapeRenderer.setColor(color);
		shapeRenderer.line(mousePos[0],mousePos[1],pointPos[0],pointPos[1]);
	}
	
	public void drawCurve(ShapeRenderer shapeRenderer) {
		float lineWidth = 1f;
		if (tmp1stPointIndex != -1) {
			drawSpringBuilder(shapeRenderer);
			if (distance(points.get(tmp1stPointIndex).getPos(),mousePos) > lineWidth) {
				if (BOUNDS.containsPos(mousePos)) {
					int i = inPoint(mousePos);
					if (i == -1) {
						//mousePos not in point
						tmp2ndPointIndex = points.size();
						addPoint(mousePos,true); //add abstract point
					} else {
						//mousePos is in point
						tmp2ndPointIndex = i;
					}
					print("adding spring to points.size() = "+points.size());
					addSpring(tmp1stPointIndex,tmp2ndPointIndex,
									buttons[STICKBUTTON].getClicked(),false);
					tmp1stPointIndex = tmp2ndPointIndex;
					tmp2ndPointIndex = -1;
					print(springs.get(springs.size()-1).getLength());
					//print(springs.size()+"springs size,  points size:"+points.size());
				}
			}
		}
	}
	public int inPoint(float[] pos) {
		//in any point...return index of point mouse is in, else -1
		for (int i = 0; i < points.size(); i++) {
    	    Point p = points.get(i);
	    	if (p.containsPos(pos)) {
	    		return i;
	    	}
		}
		return -1;
	}

	public float distance(float[] pos1, float[] pos2) {
		return (float)Math.sqrt(Math.pow(pos1[0]-pos2[0],2) + 
								Math.pow(pos1[1]-pos2[1],2));
	}
	public boolean inButton(float[] pos, int b) {
		return buttons[b].containsPos(pos);
	}

	public void addPoint(float[] pos, boolean isAbstract) {
		//print("adding point at pos = "+pos[0]+","+pos[1]);
		points.add(new Point(pos, pos, false, isAbstract,
							buttons[PINNEDBUTTON].getClicked()));
	}
	public void addPoint(float[] pos) {
		points.add(new Point(pos, pos, false, 
				buttons[PINNEDBUTTON].getClicked()));
	}
	
	public void addSpring(int ind1, int ind2, boolean stick, boolean hidden) {
		float localK = (stick ? 9999f : k);
		springs.add(new Spring(points,ind1,ind2,localK,hidden));
	}
	
	public void checkValidSprings() {
		//check if spring still has two points. 
		// If not, get rid of it.
		for (int i = 0; i < springs.size(); i++) {
			Spring s = springs.get(i);
			if (s.getP1ind() >= points.size() || s.getP2ind() >= points.size()) {
				springs.remove(i);
			}
		}
	}
	
	public void processHovering() {
		//default: hovering = false
		//makes points big or small
    	for (int i = 0; i < points.size(); i++) {
    		//Point tmp = points.get(i);
			if (points.get(i).containsPos(mousePos)) {
    			if (tmp1stPointIndex == -1 && tmp2ndPointIndex == -1 && 
    					points.size() > 1) {
    				
    				points.get(i).makeBig();
    				//tmp.makeBig();
    				//points.set(i, tmp);
    				hovering = true;
    				break;
    			}
			}
    	}
    	if (!hovering) {
    		for (int i = 0; i < points.size(); i++) {
    			//Point tmp = points.get(i);
    			if (tmp1stPointIndex == -1 && tmp2ndPointIndex == -1) {
    				
    				points.get(i).makeSmall();
    				//tmp.makeSmall();
    				//points.set(i, tmp);
    			}
    		}
    	}
	}

	public boolean processButtonClicks() {
		boolean buttonClicked = false;
    	//get button clicked
    	for (int i = 0; i < numToolButtons+numGamePlayButtons; i++) {
    		if (buttons[i].containsPos(mousePos)) {
    			buttonClicked = true;
    			buttons[i].setClicked(!buttons[i].getClicked());
    			print("set button "+i+" to clicked");
	    	}
    	}
    	
    	if (buttonClicked) {
    		handleSpecialButtons();
    	}
    	
    	return buttonClicked;
	}
	public void handleSpecialButtons() {
		//special buttons...these need immediate attention
		//print(buttons[PAUSEBUTTON].getClicked());
		if (buttons[CLEARBUTTON].getClicked()) {
			//clear the screen
			if (audioOn) stopMusic();
			buttons[CLEARBUTTON].setClicked(false);
	    	init();
		} else if (buttons[PLAYPAUSEBUTTON].getClicked()) {
			if (gameState == PLAYING) {
				buttons[PLAYPAUSEBUTTON].setImage("Verlet/play.png");
				gameState = CREATING;
				if (audioOn) danubeMusic.pause();
			} else {
				buttons[PLAYPAUSEBUTTON].setImage("Verlet/pause.png");
				gameState = PLAYING;
				if (audioOn) danubeMusic.play();
			}
			buttons[PLAYPAUSEBUTTON].setClicked(false);
		} else if (buttons[ABSTRACTBUTTON].getClicked()) {
			togglePointVisibility();
			buttons[ABSTRACTBUTTON].setClicked(false);
		}
	}
	public boolean checkAt2ndPoint() {
		//used for making chains (with curve tool or normal spring tool)
		//check if finished drawing spring to 2nd point--no need for mouseUp event
		if (tmp1stPointIndex != -1) {
			//clicked on first point to start building a chain...
			int i = inPoint(mousePos);
			if (i != -1) {
				//mousePos is in a point
				if (tmp1stPointIndex == i) return false;//2ndPoint can't be 1stPoint
	    		tmp2ndPointIndex = i; //end point of spring
	    		//add spring connecting two points
	    		//print(tmp2ndPointIndex+" ,"+tmp1stPointIndex);
	    		addSpring(tmp1stPointIndex,tmp2ndPointIndex,
	    				buttons[STICKBUTTON].getClicked(),false);
	    		print("stick = "+buttons[STICKBUTTON].getClicked());
	    		//now start drawing new spring from 2nd point
	    		tmp1stPointIndex = tmp2ndPointIndex;
				tmp2ndPointIndex = -1;
				//make all points small again
	    		for (Point p : points) {
	    			p.makeSmall();
	    		}
		    	return true;
			}
	    }
		return false;
	}
	public void processMouseDown() {
		boolean rc = processButtonClicks();
		if (rc) return; //if clicked button, get out of this function
		
		//start dealing with points and springs
		if (gameState == CREATING) {
			//check if just clicked on a point, make that point big
			int i = inPoint(mousePos);
			if (i != -1) { 
				//mousePos was in a point at index 'index'
	    	    if (points.size() > 1 || buttons[CURVEBUTTON].getClicked()) {
					//draw normal spring
		    		//print("got here");
		    		tmp1stPointIndex = i; //start making spring
		    		//make all points bigger/easier to click
		    		for (Point pp : points) {
		    			pp.makeBig();
		    		}
				}
			}
			//otherwise (didn't already click on a point), just add a point at cursor
			if (tmp1stPointIndex == -1) {
				if (BOUNDS.containsPos(mousePos)) {
	    			//print("Adding a point!");
	    			if (buttons[CURVEBUTTON].getClicked()) {
	    				tmp1stPointIndex = points.size();//last index in points array
	    				addPoint(mousePos,false);
	    			} else if (buttons[CIRCLEBUTTON].getClicked()) {
	    				//draw circle shape centered at mousePos
	    				updateCircle(mousePos,
	    						buttons[STICKBUTTON].getClicked(),true);
	    				placedCircle = true;
	    				
	    				
	    			} else {
	    				//just draw normal point
	    				addPoint(mousePos,false); 
	    				//print("added abstract point!");
	    			}
				}
			}
			//print("tmp1stPointIndex ="+tmp1stPointIndex);
			
		} else if (gameState == PLAYING) {
			//check if clicked on Point...pin Point to cursor
			for (int i = 0; i < points.size(); i++) {
		    	Point p = points.get(i);
		    	float[] pos = p.getPos();
		    	//print(pos[0]+", "+pos[1]);
		    	//print(mousePos[0]+", "+mousePos[1]);
		    	if (p.containsPos(mousePos)) {
		    		//print("contains pos!!!");
		    		indexOfClick = i;
		    	}
		    }
		}
	}

	public void processMouseUp() {
		if (gameState == CREATING) {
			checkAt2ndPoint();
			print(springs.size());
			//make sure to stop drawing spring builder...
    		tmp1stPointIndex = -1;
    		
    	} else if (gameState == PLAYING) {
    		//stop pinning mouse to Point specified by indexOfClick
    		indexOfClick = -1;
    	}
	}
	
	@SuppressWarnings("unchecked")
	public void processKeyPresses() {
		if (GameKeys.occurs(GameKeys.MOUSEDOWN)) {
	    	isTouched = true;
	    	processMouseDown();
	    	
	    } else if (GameKeys.occurs(GameKeys.MOUSEUP)) {
	    	//print("Mouse up");
	    	isTouched = false;
	    	processMouseUp();
	    	
	    } else if (GameKeys.occurs(GameKeys.P)) {
	    	buttons[PINNEDBUTTON].setClicked(!buttons[PINNEDBUTTON].getClicked());
	    } else if (GameKeys.occurs(GameKeys.M)) {
	    	buttons[MOVEBUTTON].setClicked(!buttons[MOVEBUTTON].getClicked());
	    } else if (GameKeys.occurs(GameKeys.Z)) {
	    	//this used to be key to stop music...
	    } else if (GameKeys.occurs(GameKeys.R)) {
	    	//this is supposed to restart the object falling.
	    	//print(points[0].getPos()[1]);
	    	if (audioOn) stopMusic();
	    	restart();
	    	//print(points[0].getPos()[1]);
	    	
	    } else if (GameKeys.occurs(GameKeys.SHIFT)) {
	    	//make sturdier spring, "stick"
	    	buttons[STICKBUTTON].setClicked(!buttons[STICKBUTTON].getClicked());
	    	
	    } else if (GameKeys.occurs(GameKeys.DEL)) {
	    	
	    	
	    	//MAKE THIS UNDO LAST ACTION:
	    	// undo addSpring, addPoint, addCircle, etc.
	    	
	    	
	    	//delete last Point made
	    	if (points.size() > 0) {
	    		points.remove(points.size()-1);
	    	}
//	    	for (int i = 0; i < points.size(); i++) {
//	    		if (points.get(i) == null && i > 0) {
//	    			points.set(i-1, null);
//	    			break;
//	    		}
//	    	}
	    	tmp1stPointIndex = -1;
	    	checkValidSprings();
	    	updateSpringPoints();
	    	
	    } else if (GameKeys.occurs(GameKeys.ENTER)) {
	    	//play music
    		if (audioOn) {
    			danubeMusic.play();
    			danubeMusic.setVolume(0f);
    		}
	    	initButtons(); //set to unclicked
	    	//set gamestate to play
	    	gameState = PLAYING;
	    	buttons[PLAYPAUSEBUTTON].setImage("Verlet/pause.png");
	    	//copy points to origPoints in order to 
	    	// save original values of points (pos,vel,etc..)
	    	origPoints = (ArrayList<Point>) points.clone();
			origSprings = (ArrayList<Spring>) springs.clone();	
			
	    	//make all points small
    		for (Point p : points) {
    			p.makeSmall();
    		}
    		
	    } else if (GameKeys.occurs(GameKeys.ESCAPE)) {
	    	if (audioOn) stopMusic();
	    	init();
	    } 
	}
	
	public void togglePointVisibility() {
		for (Point p : points) {
			//print(p.getAbstract()+"before");
			p.toggleAbstract();
			//print(p.getAbstract()+"after");
		}
	}
	public void updatePlayState(float dt) {
		//fade in music
		if (audioOn && danubeMusic.getVolume() < 1.0f) {
			danubeMusic.setVolume(danubeMusic.getVolume()+0.01f);
		}
		//pin points to mouse position so you can move points
		if (indexOfClick != -1) {
			pinMouseToPoint(indexOfClick);
		}
        //update points (move them)
	    updatePoints();
	    //update spring forces/rigid frames
	    for(int i = 0; i < expensiveness; i++) {
	    	updateFrames(dt);
	    }
	}
	
	public void updateCreatingState() {
		if (isTouched && !buttons[MOVEBUTTON].getClicked()) {
			//make points easier to click
			for (Point p : points) {
				p.makeBig();
			}
			
			checkAt2ndPoint();
    	}
		if (buttons[MOVEBUTTON].getClicked()) {
			if (tmp1stPointIndex != -1) {
				pinMouseToPoint(tmp1stPointIndex);
			}
		}
//		if (!placedCircle && buttons[CIRCLEBUTTON].getClicked()) {
//			print("prev:"+prevMousePos[1]+", new:"+mousePos[1]);
//			updateCircle(prevMousePos,false); //delete
//			updateCircle(mousePos,true); //create
//		}
    	//make point bigger if hovering above it, else smaller (normal size)
    	processHovering();
	}
	public void updateCircle(float[] pos, boolean stick, boolean create) {
		float radius = 100;
		int n = 10;
		boolean hideSpokes = false;
		int firstPind = points.size(); //index of center point
		int firstSind = springs.size()-1;
		int lastSind = firstSind + (n-2)*4 + 1;
		if (create) {
			addPoint(pos); //add center point
			//add circum points
			float angle;
			//inner circle points
			for (int i = 0; i < n; i++) {
				angle = i*2*(float)Math.PI/n;
				float x1 = pos[0]+(radius-10)*(float)Math.cos(angle);
				float y1 = pos[1]+(radius-10)*(float)Math.sin(angle);
				addPoint(new float[] {x1,y1});
			}
			//outer circle points
			for (int i = 0; i < n; i++) {
				angle = i*2*(float)Math.PI/n;
				float x2 = pos[0]+radius*(float)Math.cos(angle);
				float y2 = pos[1]+radius*(float)Math.sin(angle);
				addPoint(new float[] {x2,y2});
			}
			//print(firstPind+" "+lastPind);
			//add edges and spokes
			for (int i = firstPind+1; i < firstPind+n; i++) {
				addSpring(i,i+1,stick,false); //inner edges
				addSpring(firstPind,i, stick, hideSpokes); //inner spokes
				addSpring(i+n,i+n+1,stick,false); //outer edge
				addSpring(i,i+n,stick, hideSpokes); //outer spokes
			}
			//add last edges + last spokes
			addSpring(firstPind+n,firstPind+1,stick,false);
			addSpring(firstPind+2*n,firstPind+n+1,stick,false);
			addSpring(firstPind,firstPind+n,stick,hideSpokes);
			addSpring(firstPind+n,firstPind+2*n,stick,hideSpokes);
		} else {
			//delete circle
			for (int i = firstPind; i < firstPind+2*n; i++) {
				points.remove(i);
			}
			for (int i = firstSind; i < lastSind; i++) {
				springs.remove(i);
			}
		}
	}
	
	public Sound loadSound(String soundFile) {
		return Gdx.audio.newSound(Gdx.files.internal("Audio/"+soundFile));
	}
	public Music loadMusic(String musicFile) {
		return Gdx.audio.newMusic(Gdx.files.internal("Audio/"+musicFile));
	}

	public void stopMusic() {
		scratchSound.play(1.0f);
		danubeMusic.stop();
		danubeMusic.dispose();
	}

	public void pinMouseToPoint(int index) {
		Point p = points.get(index);
    	p.setPos(mousePos);
    	p.setPrevPos(prevMousePos);
    	points.set(index, p);
	}
	
	@Override
	public void drawSprites(SpriteBatch batch) {
		drawButtonSprites(batch);
		
	}

	@Override
	public void drawShapes(ShapeRenderer shapeRenderer) {
		drawButtonShapes(shapeRenderer);
		drawSprings(shapeRenderer);
		//draw curve or chain
		if (gameState == CREATING && isTouched) {
			if (buttons[CURVEBUTTON].getClicked()) 
				drawCurve(shapeRenderer);
			else {
				//print(tmp1stPointIndex);
				if (tmp1stPointIndex != -1) 
					drawSpringBuilder(shapeRenderer);
			}
		}
		drawPoints(shapeRenderer);
		drawBounds(shapeRenderer);
		
	}
	
	
	@Override
	public void render () {
		//MAIN LOOP
		
		//clear screen to black
		Gdx.gl.glClearColor(0,0,0, 1);
		Gdx.gl.glViewport(0, 0, WIDTH, HEIGHT);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	    //print(pinned);
        
        
        //print("gameState = "+gameState);
        
        
        hovering = false; //not hovering over any points
        mousePos = getMousePos();
        if (gameState == CREATING) {
        	updateCreatingState();
        } else if (gameState == PLAYING) {
        	updatePlayState(Gdx.graphics.getDeltaTime()); 
        }
        
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin();
        drawShapes(shapeRenderer);
        shapeRenderer.end();
        
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        drawSprites(batch);
        batch.end();
	    
	    //check mouse+key activity
	    processKeyPresses();
	    
	    GameKeys.update();
	    
	    //reset prevMousePos
	    prevMousePos[0] = mousePos[0];
	    prevMousePos[1] = mousePos[1];
		
	}
	@Override
	public void dispose() {
		//close stream
		danubeMusic.dispose();
	}
}

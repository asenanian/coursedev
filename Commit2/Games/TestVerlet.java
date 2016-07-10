package com.mygdx.game;


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
import com.mygdx.entities.Point;
import com.mygdx.entities.Spring;
import com.mygdx.entities.Stick;
import com.mygdx.managers.GameInputProcessor;
import com.mygdx.managers.GameKeys;


public class TestVerlet extends ApplicationAdapter {
	public static int WIDTH, HEIGHT;
	public static int FLOOR, CEILING, LEFTWALL, RIGHTWALL;
	public static OrthographicCamera cam;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private Button[] buttons;
	private ArrayList<Point> origPoints;
	private ArrayList<Point> points;
	private ArrayList<Spring> origSprings;
	private ArrayList<Spring> springs;
	private float timeConstant = 0.001f;
	private float gravity = 0f;//-timeConstant*100f;
	private float k = timeConstant*1000f;
	private float w = 1f; //width of springs
	private int expensiveness = 100; //aka realisticness (1 to 1000 please)
	private Music danubeMusic;
	private Sound scratchSound;
	
	public static int MAXPOINTS = 50;
	public static int MAXSPRINGS = 60;
	
	public int numButtons = 9;
	public static int PINNEDBUTTON = 0;
	public static int HIDDENBUTTON = 1;
	public static int MOVEBUTTON = 2;
	public static int SPRINGBUTTON = 3;
	public static int STICKBUTTON = 4;
	public static int CLEARBUTTON = 5;
	public static int CURVEBUTTON = 6;
	public static int CIRCLEBUTTON = 7;
	public static int ABSTRACTBUTTON = 8;
	
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
		FLOOR = HEIGHT/4;
		CEILING = 3*HEIGHT/4;
		LEFTWALL = WIDTH/4;
		RIGHTWALL = 3*WIDTH/4;
		
		cam = new OrthographicCamera(WIDTH,HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2);
		cam.update();
		Gdx.input.setInputProcessor(new GameInputProcessor());
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		scratchSound = loadSound("scratch.mp3");
		danubeMusic = loadMusic("danube.mp3");
		init();
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
		buttons = new Button[numButtons];
		int n = numButtons;
		//pinned tool button
		buttons[PINNEDBUTTON] = new Button(new float[] {50,PINNEDBUTTON*HEIGHT/n},
									60f,60f,"Verlet/pinnedPoint.png");
		//normal point tool
		buttons[SPRINGBUTTON] = new Button(new float[] {50,SPRINGBUTTON*HEIGHT/n},
									60f,60f,"Verlet/spring.png");
		//stick tool button
		buttons[STICKBUTTON] = new Button(new float[] {50,STICKBUTTON*HEIGHT/n},
									60f,60f,"Verlet/stick.png");
		//move tool button
		buttons[MOVEBUTTON] = new Button(new float[] {50,MOVEBUTTON*HEIGHT/n},
									60f,60f,"Verlet/moveTool.png");
		//clear screen button
		buttons[CLEARBUTTON] = new Button(new float[] {50,CLEARBUTTON*HEIGHT/n},
									50f,50f,"Verlet/bomb.png");
		//hidden support
		buttons[HIDDENBUTTON] = new Button(new float[] {50,HIDDENBUTTON*HEIGHT/n},
									100f,60f,"Verlet/hiddenStick.png");
		//draw line by dragging
		buttons[CURVEBUTTON] = new Button(new float[] {50,CURVEBUTTON*HEIGHT/n},
									50f,50f,"Verlet/lineTool.png");
		//draw bicycle-like wheel
		buttons[CIRCLEBUTTON] = new Button(new float[] {50,CIRCLEBUTTON*HEIGHT/n},
									50f,50f,"Verlet/circleTool.png");
		//make all points abstract (invisible)
		buttons[ABSTRACTBUTTON] = new Button(new float[] {50,ABSTRACTBUTTON*HEIGHT/n},
									50f,50f,"Verlet/abstractButton.png");
	}
	
	public void restart() {
		//print("RESTARTING\n\n\n\n");
		gameState = CREATING;
		//print("orig pos:"+origPoints.get(0).getPos()[1]);
		//print(origPoints.get(0));
		points = (ArrayList<Point>) origPoints.clone();
		springs = (ArrayList<Spring>) origSprings.clone();
		updateSprings();
	}
	
	public void updatePoints() {
		for (int i = 0; i < points.size(); i++) {
			Point tmp = new Point(points.get(i));
			tmp.update(gravity);
			points.set(i, tmp);
			print("pos:"+tmp.getPos()[0]+","+tmp.getPos()[1]+","+tmp.getPos()[2]);
		}
	}

	public void updateSprings() {
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
			float[] point1pos = s.getPoint1().getPos();
			float[] point2pos = s.getPoint2().getPos();
			float dx = point2pos[0] - point1pos[0];
			float dy = point2pos[1] - point1pos[1];
			float dist = (float)Math.sqrt(dx * dx + dy * dy); //dist bt points
			float diff = dist - s.getLength();
			Point tmp1 = points.get(s.getP1ind());
			Point tmp2 = points.get(s.getP2ind());
			if (s instanceof Stick) {
				//for rigid body
				float percent = diff / dist / 2;
				float offsetX = dx * percent;
				float offsetY = dy * percent;
				tmp1.addXY(offsetX, offsetY);
				tmp2.addXY(-offsetX, -offsetY);
			} else {
				//for spring restoring force
				float[] restoringAccels = s.getRestoringAccels(diff,dt);
				float p1accel = restoringAccels[0];
				float p2accel = restoringAccels[1];
				tmp1.addVect2D(p1accel,tmp1,tmp2);
				tmp2.addVect2D(p2accel,tmp1,tmp2);
			}
	
		}
		
		updateSprings(); //give springs up-to-date versions of points
	}
	public void print(Object obj) {
		System.out.println(obj);
	}
	public float[] getMousePos() {
		//invert y value to make bottom y = 0, not height
		return new float[] {Gdx.input.getX(), HEIGHT-Gdx.input.getY()};
	}
	
	public void drawButtons() {
		//first draw transparent rectangle as button
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeType.Filled);
		for (Button b : buttons) {
			b.drawRect(shapeRenderer);
		}
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		//now draw sprite over the rectangle
		batch.begin();
		for (Button b : buttons) {
			b.draw(batch);
		}
		batch.end();
	}

	public void drawBounds() {
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.line(LEFTWALL, CEILING, RIGHTWALL, CEILING);
		shapeRenderer.line(RIGHTWALL, CEILING, RIGHTWALL, FLOOR);
		shapeRenderer.line(RIGHTWALL, FLOOR, LEFTWALL, FLOOR);
		shapeRenderer.line(LEFTWALL, FLOOR, LEFTWALL, CEILING);
		shapeRenderer.end();
	}
	
	public void drawSprings() {
		shapeRenderer.begin(ShapeType.Filled); //for filled rectline
	    for (Spring s : springs) {
    		s.draw(shapeRenderer);
	    }
	    shapeRenderer.end();
	}
	public void drawPoints() {
		Color color = Color.WHITE;
		shapeRenderer.begin(ShapeType.Filled);
		for (Point p : points) {
			p.draw(shapeRenderer);
		}
		shapeRenderer.end();
	}
	public void drawSpringBuilder() {
		//precondition: tmp1stPointIndex != -1
		float[] pointPos = points.get(tmp1stPointIndex).getPos();
		shapeRenderer.begin(ShapeType.Line);
		Color color = Color.RED;
		if (buttons[STICKBUTTON].getClicked()) {
			color = Color.BLUE;
		} else {
			if (buttons[HIDDENBUTTON].getClicked()) {
				color = Color.WHITE;
			} else {
				color = Color.RED;
			}
		}
		shapeRenderer.setColor(color);
		shapeRenderer.line(mousePos[0],mousePos[1],pointPos[0],pointPos[1]);
		shapeRenderer.end();
	}
	
	public void drawCurve() {
		float lineWidth = 1f;
		if (tmp1stPointIndex != -1) {
			drawSpringBuilder();
			if (distance(points.get(tmp1stPointIndex).getPos(),mousePos) > lineWidth) {
				if (inBounds(mousePos)) {
					tmp1stPointIndex = points.size();
					addPoint(mousePos,true); //add abstract point
					addSpring(points.size()-2,points.size()-1,
							buttons[STICKBUTTON].getClicked(),false);
					//print(springs.size()+"springs size,  points size:"+points.size());
				}
			}
		}
	}
	public float distance(float[] pos1, float[] pos2) {
		return (float)Math.sqrt(Math.pow(pos1[0]-pos2[0],2) + 
								Math.pow(pos1[1]-pos2[1],2));
	}
	public boolean inButton(float[] pos, int b) {
		Button button = buttons[b];
		if (pos[0] > button.getPos()[0] && pos[0] < button.getPos()[0]+button.getWidth() &&
				pos[1] > button.getPos()[1] && pos[1] < button.getPos()[1]+button.getHeight()) {
			return true;
		}
		return false;
	}

	public boolean inBounds(float[] pos) {
		if (pos[0] > TestVerlet.LEFTWALL && pos[0] < TestVerlet.RIGHTWALL &&
				pos[1] > TestVerlet.FLOOR && pos[1] < TestVerlet.CEILING) {
			return true;
		}
		return false;
	}
	public void addPoint(float[] pos, boolean isAbstract) {
		print("mousePos;"+pos[0]+","+pos[1]);
		points.add(new Point(pos.clone(), pos.clone(), false, isAbstract,
							buttons[PINNEDBUTTON].getClicked()));
	}
	public void addPoint(float[] pos) {
		points.add(new Point(pos.clone(), pos.clone(), 
				buttons[PINNEDBUTTON].getClicked()));
	}
	
	public void addSpring(int ind1, int ind2, boolean stick, boolean hidden) {
		if (stick) {
			springs.add(new Stick(points,ind1,ind2,w,hidden));
		} else {
			springs.add(new Spring(points,ind1,ind2,k,w,hidden));
		}
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
		int buttonClicked = -1;
    	//get button clicked
    	for (int i = 0; i < numButtons; i++) {
	    	if (inButton(mousePos,i)) {
	    		buttonClicked = i;
	    	}
    	}
    	if (buttonClicked != -1) {
    		//toggle button clicked
    		for (int i = 0; i < numButtons; i++) {
    			if (i == buttonClicked) {
    				buttons[buttonClicked].setClicked(!buttons[buttonClicked].getClicked());
    			}
    		}
    		//exception: clear screen
    		if (buttons[CLEARBUTTON].getClicked()) {
    			//clear the screen
    			stopMusic();
    	    	init();
    		}
    		if (buttons[ABSTRACTBUTTON].getClicked()) {
    			togglePointVisibility();
    			buttons[ABSTRACTBUTTON].setClicked(false);
    		}
    		return true;
    	}
    	return false;
	}
	public void checkAt2ndPoint() {
		//used for drawing curve
		//check if finished drawing spring to 2nd point--no need for mouseUp event
		if (tmp1stPointIndex != -1) {
			//clicked on first point to start building a spring...
    		for (int i = 0; i < points.size(); i++) {
    			if (tmp1stPointIndex == i) continue;
    	    	Point p = points.get(i);
    	    	if (p.containsPos(mousePos)) {
    	    		tmp2ndPointIndex = i; //end point of spring
    	    		//add spring connecting two points
    	    		//print(tmp2ndPointIndex+" ,"+tmp1stPointIndex);
    	    		addSpring(tmp1stPointIndex,tmp2ndPointIndex,
    	    				buttons[STICKBUTTON].getClicked(),false);
    	   
    	    		//now start drawing new spring from 2nd point
    	    		tmp1stPointIndex = tmp2ndPointIndex;
	    			tmp2ndPointIndex = -1;
	    			//make all points small again
    	    		for (Point pp : points) {
    	    			pp.makeSmall();
    	    		}
    	    	}
    		}
		}
	}
	public void processMouseDown() {
		boolean rc = processButtonClicks();
		if (rc) return; //if clicked button, return
		
		//start dealing with points and springs
		if (gameState == CREATING) {
			//check if just clicked on a point, make that point big
			for (int i = 0; i < points.size(); i++) {
	    	    Point p = points.get(i);
	    	    //print("p = "+p);
	    
		    	if (p.containsPos(mousePos) && 
		    			(points.size() > 1 ||
		    				buttons[CURVEBUTTON].getClicked())) {
					//draw normal spring
		    		//print("got here");
		    		tmp1stPointIndex = i; //start making spring
		    		//make all points bigger/easier to click
		    		for (Point pp : points) {
		    			pp.makeBig();
		    		}
		    		break; //already found what Point cursor is inside
				
				}
			}
			//otherwise (didn't already click on a point), just add a point at cursor
			if (tmp1stPointIndex == -1) {
				if (inBounds(mousePos)) {
	    			//print("Adding a point!");
	    			boolean isAbstract = false;
	    			if (buttons[CURVEBUTTON].getClicked()) {
	    				isAbstract = true;
	    				tmp1stPointIndex = points.size();//last index in points array
	    				addPoint(mousePos,isAbstract);
	    			} else if (buttons[CIRCLEBUTTON].getClicked()) {
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
			if (!buttons[CURVEBUTTON].getClicked()) {
				checkAt2ndPoint();
			}
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
	    	stopMusic();
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
	    	updateSprings();
	    	
	    } else if (GameKeys.occurs(GameKeys.ENTER)) {
	    	//play music
    		danubeMusic.play();
    		danubeMusic.setPosition(88f);
    		danubeMusic.setVolume(0f);
	    	
	    	//set gamestate to play
	    	gameState = PLAYING;
	    	//copy points to origPoints in order to 
	    	// save original values of points (pos,vel,etc..)
	    	origPoints = (ArrayList<Point>) points.clone();
			origSprings = (ArrayList<Spring>) springs.clone();	
			
	    	//make all points small
    		for (Point p : points) {
    			p.makeSmall();
    		}
    		
	    } else if (GameKeys.occurs(GameKeys.ESCAPE)) {
	    	stopMusic();
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
		if (danubeMusic.getVolume() < 1.0f) {
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
			if (buttons[CURVEBUTTON].getClicked()) {
				drawCurve();
			} else {
				//make points easier to click
				for (Point p : points) {
					p.makeBig();
				}
				//print(tmp1stPointIndex);
				if (tmp1stPointIndex != -1) {
					//print("hello");
					drawSpringBuilder();
				}
	    		checkAt2ndPoint();
			}
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
				float x1 = pos[0]+radius/3*(float)Math.cos(angle);
				float y1 = pos[1]+radius/3*(float)Math.sin(angle);
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
	public void render () {
		//MAIN LOOP
		
		//clear screen to black
		Gdx.gl.glClearColor(0,0,0, 1);
		Gdx.gl.glViewport(0, 0, WIDTH, HEIGHT);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	    //print(pinned);
        
        
        
        shapeRenderer.setProjectionMatrix(cam.combined);
        hovering = false; //not hovering over any points
        mousePos = getMousePos();
        if (gameState == CREATING) {
        	updateCreatingState();
        	//draw red/blue lines
            drawSprings();
            //draw points
        	drawPoints();
        } else if (gameState == PLAYING) {
        	updatePlayState(Gdx.graphics.getDeltaTime()); 
        	//draw red/blue lines
            drawSprings();
            //draw points
        	drawPoints();
        }
        
	    //draw buttons
	    drawButtons();
	    
	    drawBounds();
	    
	    //check mouse+key activity
	    processKeyPresses();
	    
	    GameKeys.update();
	    
	    
	    
	    //reset prevMousePos
	    prevMousePos = (float[]) mousePos.clone();
		
	}
	@Override
	public void dispose() {
		//close stream
		danubeMusic.dispose();
	}
	
}

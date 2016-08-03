package com.mygdx.gamestates;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.entities.Button;
import com.mygdx.entities.Circle;
import com.mygdx.entities.Image;
import com.mygdx.entities.Point;
import com.mygdx.entities.Rect;
import com.mygdx.entities.Spring;
//import com.mygdx.entities.Stick;
import com.mygdx.entities.Text;
import com.mygdx.entities.ToggleButton;
import com.mygdx.game.MyGdxGame;
import com.mygdx.managers.FileChooserManager;
import com.mygdx.managers.GameInputProcessor;
import com.mygdx.managers.GameKeys;
import com.mygdx.managers.GameStateManager;

public class PlayState extends GameState {
	public static int WIDTH, HEIGHT;
	public static int FLOOR, CEILING, LEFTWALL, RIGHTWALL;
	public static Rect BOUNDS;
	public static String TOOLSDIR = "Verlet/Tools/";
	public static String GAMEPLAYDIR = "Verlet/GamePlayButtons/";
	public static String MISCDIR = "Verlet/Misc/";
	public static String AUDIODIR = "Audio/";
	public static OrthographicCamera cam;
	//public ShapeRenderer shapeRenderer;
	//public SpriteBatch batch;
	public ArrayList<Button> buttons;
	public ArrayList<Point> origPoints;
	public ArrayList<Point> points;
	public ArrayList<Spring> origSprings;
	public ArrayList<Spring> springs;
	public float timeConstant;
	public float gravity;
	public float k;
	public float w;
	public float circleRadius;
	public int expensiveness;
	public Music danubeMusic;
	public Sound scratchSound;
	public boolean audioOn;
	
	public static int MAXPOINTS = 1000; //NOT USED
	public static int MAXSPRINGS = 1000; //NOT USED
	
	public static int numToolButtons = 9;
	public static int PINNEDBUTTON = 0;
	public static int HIDDENBUTTON = 1;
	public static int MOVEBUTTON = 2;
	public static int SPRINGBUTTON = 3;
	public static int STICKBUTTON = 4;
	public static int CURVEBUTTON = 5;
	public static int WHEELBUTTON = 6;
	public static int ABSTRACTBUTTON = 7;
	public static int CIRCLEBUTTON = 8;
	
	public static int numGamePlayButtons = 2;
	public static int PLAYBUTTON = 9;
	public static int CLEARBUTTON = 10;
	
	public static int CREATING = 0;
	public static int PLAYING = 1;
	
	public int gameSubState;
	public int indexOfClick;
	public int tmp1stPointIndex;
	public int tmp2ndPointIndex;
	public boolean hovering;
	public boolean placedWheel;
	//public boolean pinned;
	//public boolean moving;
	//public boolean makeStick;
	public boolean isTouched;
	public float[] mousePos;
	public float[] prevMousePos;
	public BitmapFont fileNameFont;
	public FileChooserManager fileChooserManager;
	
	public PlayState(GameStateManager gsm) {
		super(gsm);
	}
	
	
	@Override
	public void init() {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		initBoundaries();
		
		cam = new OrthographicCamera(WIDTH,HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2);
		cam.update();
		
		Gdx.input.setInputProcessor(new GameInputProcessor());
		
		fileChooserManager = new FileChooserManager();
		
		initVals();
		
		initPoints();
	    initSprings();
	    initButtons();
	    initFonts();
	    initFileChooserManager();
	    
	    scratchSound = loadSound("scratch.mp3");
		danubeMusic = loadMusic("danube.mp3");
		audioOn = (gravity == 0); //toggle audio
		
	    gameSubState = CREATING;
	}

	public void initVals() {
		timeConstant = 0.001f;
		gravity = -timeConstant*100f;
		k = timeConstant * 50f;
		w = 1f; //width of springs
		expensiveness = 100; //aka realisticness (1 to 1000 please)
		indexOfClick = -1;
		tmp1stPointIndex = -1;
		tmp2ndPointIndex = -1;
		hovering = false;
		placedWheel = true;
		circleRadius = 10f;
		//pinned = false;
		//moving = false;
		//makeStick = false;
		isTouched = true;
		mousePos = new float[2];
		prevMousePos = new float[2];
	}
	
	public void initBoundaries() {
		FLOOR = HEIGHT/4;
		CEILING = 3*HEIGHT/4;
		LEFTWALL = WIDTH/4;
		RIGHTWALL = 3*WIDTH/4;
		BOUNDS = new Rect(LEFTWALL,FLOOR,RIGHTWALL-LEFTWALL,CEILING-FLOOR,
									Color.LIGHT_GRAY,true);
		BOUNDS.setOutlineColor(Color.WHITE);
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
		buttons = new ArrayList<Button>(numToolButtons+numGamePlayButtons);
		int n1 = numToolButtons;
		//ORDER OF BUTTONS MATTERS!!
//		PINNEDBUTTON = 0;
//		HIDDENBUTTON = 1;
//		MOVEBUTTON = 2;
//		SPRINGBUTTON = 3;
//		STICKBUTTON = 4;
//		CURVEBUTTON = 5;
//		WHEELBUTTON = 6;
//		ABSTRACTBUTTON = 7;
//		CIRCLEBUTTON = 8;
		//pinned tool button
		addButton(TOOLSDIR+"pinnedTool.png",50,PINNEDBUTTON*HEIGHT/n1+50,
									60f,60f);
		//hidden support
		addButton(TOOLSDIR+"hiddenTool.png",50,HIDDENBUTTON*HEIGHT/n1+50,
									100f,60f);
		//move tool button
		addButton(TOOLSDIR+"moveTool.png",50,MOVEBUTTON*HEIGHT/n1+50,
									60f,60f);
		//spring tool
		addButton(TOOLSDIR+"springTool.png",50,SPRINGBUTTON*HEIGHT/n1+50,
									60f,60f);
		//stick tool
		addButton(TOOLSDIR+"stickTool.png",50,STICKBUTTON*HEIGHT/n1+50,
									60f,60f);
		//curve tool
		addButton(TOOLSDIR+"curveTool.png",50,CURVEBUTTON*HEIGHT/n1+50,
									50f,50f);
		//draw bicycle-like wheel
		addButton(TOOLSDIR+"wheelTool.png",50,WHEELBUTTON*HEIGHT/n1+50,
									50f,50f);
		//make all points abstract (invisible)
		addButton(TOOLSDIR+"abstractTool.png",50,ABSTRACTBUTTON*HEIGHT/n1+50,
									50f,50f);
		//circle/disc 
		addButton(TOOLSDIR+"circleTool.png",50,CIRCLEBUTTON*HEIGHT/n1+50,
									50f,50f);
				
		//NOW ADD ALL GAMEPLAY BUTTONS
		int n2 = numGamePlayButtons;
		//play/pause button (start it off showing play button)
		buttons.add(new ToggleButton(GAMEPLAYDIR+"play.png",GAMEPLAYDIR+"pause.png",
				300,HEIGHT/n1,50f,50f));
		
		//clear screen button
		addButton(TOOLSDIR+"bomb.png",500,HEIGHT/n1,50f,50f);
	}
	public void initFonts() {
		fileNameFont = new BitmapFont();
		fileNameFont.setColor(Color.BLUE);
	}
	
	public void initFileChooserManager() {
		//get list of all files in '...android/assets/Verlet' directory
		float[] windowBounds = new float[] {WIDTH/5,2*HEIGHT/3,3*WIDTH/5,HEIGHT/3};
		print("INITIALIZING FILECHOOSERMANAGER");
		fileChooserManager.init("Verlet", fileNameFont, windowBounds);
	}

	
	@Override
	public void update(float dt) {
        hovering = false; //not hovering over any points
        mousePos = getMousePos();
        if (gameSubState == CREATING) {
        	updateCreatingSubState();
        } else if (gameSubState == PLAYING) {
        	updatePlaySubState(Gdx.graphics.getDeltaTime()); 
        }
        
		fileChooserManager.updateChosenImage(mousePos,false);
		
        handleInput();
	    GameKeys.update();
	    
	    //reset prevMousePos
	    prevMousePos[0] = mousePos[0];
	    prevMousePos[1] = mousePos[1];

	}

	@Override
	public void drawSprites(SpriteBatch batch) {
		drawButtonSprites(batch);
		fileChooserManager.drawSprites(batch);
	}

	@Override
	public void drawShapes(ShapeRenderer shapeRenderer) {
		BOUNDS.draw(shapeRenderer);
		drawButtonShapes(shapeRenderer);
		drawSprings(shapeRenderer);
		drawPoints(shapeRenderer);
		BOUNDS.drawOutline(shapeRenderer);
		//draw curve or chain
		if (gameSubState == CREATING && isTouched) {
			if (buttons.get(CURVEBUTTON).getClicked()) 
				buildCurve(shapeRenderer);
			else {
				//print(tmp1stPointIndex);
				if (tmp1stPointIndex != -1) {
					drawSpringBuilder(shapeRenderer);
				}
			}
		}
		//draw file chooser window
		if (fileChooserManager.getActive()) {
			//print("FILE chooser is active");
			fileChooserManager.drawShapes(shapeRenderer);
		}
		
	}

	@Override
	public void handleInput() {
		//check mouse+key activity
	    processKeyPresses();
	}

	@Override
	public void dispose() {
		//close stream
		danubeMusic.dispose();
	}
	
	
	public void restart() {
		//print("RESTARTING\n\n\n\n");
		gameSubState = CREATING;
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
			tmp.update(gravity);
			points.set(i, tmp);
			//print("pos:"+tmp.getPos()[0]+","+tmp.getPos()[1]+","+tmp.getPos()[2]);
		}
		//check for circle collisions
		//print(tmpCircles.size()+"CIRCLES SIZE");
		for (int i = 0; i < tmpCircles.size()-1; i++) {
			for (int j = i+1; j < tmpCircles.size(); j++) {
				//Circle tmp = new Circle(tmpCircles.get(i));
				tmpCircles.get(i).processCollision(tmpCircles.get(j),gravity);
			}
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
		//radians
		return (float)Math.atan2(p2[1]-p1[1],p2[0]-p1[0]);
	}

	public void updateFrames(float dt) {
		//update spring forces if dealing with Springs
		//update rigid frame if dealing with Sticks
		
		for (Spring s : springs) {
			//update points because of spring tension or rigidity
			points = (ArrayList<Point>) s.updatePoints(points,dt).clone();
		}
		//print(springs.size());
		
		//updateSpringPoints(); //give springs up-to-date versions of points
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
		//change color of button rectangle at click
		shapeRenderer.set(ShapeType.Filled);
		for (Button b : buttons) {
			b.drawShapes(shapeRenderer);
		}
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
		if (buttons.get(HIDDENBUTTON).getClicked()) {
			color = Color.WHITE;
		} else if (buttons.get(STICKBUTTON).getClicked()) {
			color = Color.BLUE;
		} else {
			//normal spring
			color = Color.RED;
		}
		shapeRenderer.setColor(color);
		shapeRenderer.line(mousePos[0],mousePos[1],pointPos[0],pointPos[1]);
	}

	public void buildCurve(ShapeRenderer shapeRenderer) {
		int distBetweenPoints = 1;
		if (buttons.get(CIRCLEBUTTON).getClicked()) {
			distBetweenPoints = Spring.LINEWIDTH*2+1;
		} 
		if (tmp1stPointIndex != -1) {
			drawSpringBuilder(shapeRenderer);
			float[] p1 = points.get(tmp1stPointIndex).getPos();
			float[] p2 = mousePos;
			float dist = distance(p1,p2);
			float angle = getAngle(p1,p2);
			//print(getAngle(new float[] {1,1},new float[] {2,2})+" should be pi/4");
			float[] pNew; //new pos to add point to curve
			for (int n = distBetweenPoints; n < dist; n += distBetweenPoints) {
				pNew = new float[2];
				pNew[0] = p1[0] + n*(float)Math.cos(angle);
				pNew[1] = p1[1] + n*(float)Math.sin(angle);
				if (BOUNDS.containsPos(pNew)) {
					int i = inPoint(pNew);
					if (i == -1) {
						//pNew not in point
						tmp2ndPointIndex = points.size();
						if (buttons.get(CIRCLEBUTTON).getClicked()) {
	    					addCircle(pNew,Spring.LINEWIDTH);
	    				} else {
	    					addPoint(pNew,true); //add abstract point
	    				}
					} else {
						//pNew is in point
						tmp2ndPointIndex = i;
					}
					print("adding spring to points.size() = "+points.size());
					addSpring(tmp1stPointIndex,tmp2ndPointIndex,
									buttons.get(STICKBUTTON).getClicked(),false);
					tmp1stPointIndex = tmp2ndPointIndex;
					tmp2ndPointIndex = -1;
					print(springs.get(springs.size()-1).getLength());
					//print(springs.size()+"springs size,  points size:"+points.size());
				}
			}
		}
	}
	public int inPoint(float[] pos) {
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
	public void addButton(String internalPath,float x, float y,float width,float height) {
		buttons.add(new Button(internalPath,x,y,width,height));
	}
	public void addPoint(float[] pos, boolean isAbstract) {
		//print("adding point at pos = "+pos[0]+","+pos[1]);
		points.add(new Point(pos, pos, false, isAbstract,
							buttons.get(PINNEDBUTTON).getClicked()));
	}
	public void addPoint(float[] pos) {
		points.add(new Point(pos, pos, false, 
				buttons.get(PINNEDBUTTON).getClicked()));
	}
	public void addCircle(float[] pos, float radius) {
		points.add(new Circle(pos,pos,radius,
				buttons.get(PINNEDBUTTON).getClicked()));
	}
	
	public void addSpring(int ind1, int ind2, boolean stick, boolean hidden) {
		if (stick) {
			springs.add(new Spring(points,ind1,ind2,9999f,hidden));
		} else {
			//print("if k > 0.3f, you just added a stick: "+k);
			springs.add(new Spring(points,ind1,ind2,k,hidden));
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
		boolean buttonClicked = false;
    	//get button clicked
    	for (int i = 0; i < numToolButtons+numGamePlayButtons; i++) {
    		if (buttons.get(i).containsPos(mousePos)) {
    			print(buttons.get(i).getName()+" was clicked");
    			buttonClicked = true;
    			buttons.get(i).toggleClicked();
    			//print("buttons index "+i+" was clicked");
    			print(mousePos[1]+": "+buttons.get(i).getBottom()+","+buttons.get(i).getTop()+","+buttons.get(i).getCenter()[1]);
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
		if (buttons.get(CLEARBUTTON).getClicked()) {
			//clear the screen
			if (audioOn) stopMusic();
			buttons.get(CLEARBUTTON).setClicked(false);
	    	init();
		} else if (buttons.get(PLAYBUTTON).getClicked()) {
			gameSubState = (gameSubState == PLAYING ? CREATING : PLAYING);
			if (audioOn) {
				if (danubeMusic.isPlaying()) {
					danubeMusic.pause();
				} else {
					danubeMusic.play();
				}
			}
			((ToggleButton)buttons.get(PLAYBUTTON)).toggleImage();
			
		} else if (buttons.get(ABSTRACTBUTTON).getClicked()) {
			togglePointVisibility();
			buttons.get(ABSTRACTBUTTON).setClicked(false);
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
	    				buttons.get(STICKBUTTON).getClicked(),false);
	    		//print(springs.get(springs.size()-1).getColor());
	    		//print("stick = "+buttons.get(STICKBUTTON).getClicked());
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
	public void addFromFileChooser() {
		String chosenImagePath = fileChooserManager.getChosenImagePath();
		//String chosenImageName = fileChooserManager.getChosenImageName();
		if (chosenImagePath.equals("Verlet/GameObjects/cannon.png")) {
			print("ADDED CANNON!!!!!!!!!");
			//buttons.add(new Button(chosenImagePath, mousePos, 60,60));
		} else {
			//add more possible files to drag into the screen...
		}
		//stop dragging the file
		fileChooserManager.resetChosenImage();
	}
	
	public boolean processFileChooserClicks(float[] mousePos) {
		//check if just clicked in file chooser window
		//print("clicked on file name");
		boolean rc = fileChooserManager.updateClicks(mousePos);
		if (rc) return true; //let fileChooserManager.update() deal with this
		
		//check if user just dragged a file into 
		//   the screen from filechooser window
		if (fileChooserManager.getChosenImage() != null) {
			//add file to screen where user clicked
			addFromFileChooser(); //resets chosenImage
			return true;
		}
		fileChooserManager.updateChosenImage(mousePos,true);//possibly throws image in trash
		
		return false;
	}
	
	public void processMouseDown() {
		boolean rc1 = processButtonClicks();
		if (rc1) return; //if clicked a button, get out of this function
		
		//start dealing with non-button stuff
		if (gameSubState == CREATING) {
			boolean rc2 = processFileChooserClicks(mousePos);
			if (rc2) return;
			
			//check if just clicked on a point, make that point big
			int i = inPoint(mousePos);
			if (i != -1) { 
				//mousePos was in a point at index 'index'
	    	    if (points.size() > 1 || buttons.get(CURVEBUTTON).getClicked()) {
					//draw normal spring
		    		//print("got here");
		    		tmp1stPointIndex = i; //start making spring from end point of last spring
		    		//make all points bigger/easier to click
		    		for (Point p : points) {
		    			p.makeBig();
		    		}
				}
			}
			//otherwise (didn't already click on a point), just add a point at cursor
			if (tmp1stPointIndex == -1) {
				if (BOUNDS.containsPos(mousePos)) {
	    			//print("Adding a point!");
	    			if (buttons.get(CURVEBUTTON).getClicked()) {
	    				tmp1stPointIndex = points.size();//last index in points array
	    				if (buttons.get(CIRCLEBUTTON).getClicked()) {
	    					addCircle(mousePos,Spring.LINEWIDTH);
	    				} else {
	    					addPoint(mousePos,true);
	    				}
	    			} else if (buttons.get(WHEELBUTTON).getClicked()) {
	    				//draw circle shape centered at mousePos
	    				createWheel(mousePos,buttons.get(STICKBUTTON).getClicked());
	    				placedWheel = true;
	    				
	    				
	    			} else {
	    				//just draw normal point
	    				if (buttons.get(CIRCLEBUTTON).getClicked()) {
	    					addCircle(mousePos,circleRadius);
	    				} else {
	    					addPoint(mousePos,false);
	    				}
	    				//print("added abstract point!");
	    			}
				}
			}
			//print("tmp1stPointIndex ="+tmp1stPointIndex);
			
		} else if (gameSubState == PLAYING) {
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
		if (gameSubState == CREATING) {
			checkAt2ndPoint();
			print(springs.size());
			//make sure to stop drawing spring builder...
    		tmp1stPointIndex = -1;
    		
    	} else if (gameSubState == PLAYING) {
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
	    	//toggle pinned point button
	    	buttons.get(PINNEDBUTTON).toggleClicked();
	    } else if (GameKeys.occurs(GameKeys.M)) {
	    	//toggle movePoint button
	    	buttons.get(MOVEBUTTON).toggleClicked();
	    } else if (GameKeys.occurs(GameKeys.Z)) {
	    	//open/close file chooser window
	    	
	    	//pause if PLAYING
	    	if (gameSubState == PLAYING) {
	    		gameSubState = CREATING;
	    		((ToggleButton)buttons.get(PLAYBUTTON)).toggleImage();
	    	}
			if (audioOn) {
				if (danubeMusic.isPlaying()) {
					danubeMusic.pause();
				} else {
					danubeMusic.play();
				}
			}
			
			if (!fileChooserManager.getActive()) {
				fileChooserManager.show();
			} else {
				//close window at 2nd 'Z' press
				fileChooserManager.hide();
			}
	    } else if (GameKeys.occurs(GameKeys.R)) {
	    	//this is supposed to restart the object falling.
	    	//print(points[0].getPos()[1]);
	    	if (audioOn) stopMusic();
	    	restart();
	    	//print(points[0].getPos()[1]);
	    	
	    } else if (GameKeys.occurs(GameKeys.SHIFT)) {
	    	//make sturdier spring, "stick"
	    	buttons.get(STICKBUTTON).toggleClicked();
	    	
	    } else if (GameKeys.occurs(GameKeys.C)) {
	    	//draw circle
	    	buttons.get(CIRCLEBUTTON).toggleClicked();
	    }
	    else if (GameKeys.occurs(GameKeys.DEL)) {
	    	
	    	
	    	//MAKE THIS UNDO LAST ACTION:
	    	// undo addSpring, addPoint, addCircle, etc.
	    	
	    	
	    	//delete last Point made
	    	if (points.size() > 0) {
	    		points.remove(points.size()-1);
	    	}
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
	    	gameSubState = PLAYING;
	    	
	    	//print(buttons[PLAYBUTTON].getClass().getName()+" twe");
	    	((ToggleButton)buttons.get(PLAYBUTTON)).toggleImage();
	    	
	    	
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
	public void updatePlaySubState(float dt) {
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
	
	public void updateCreatingSubState() {
		if (isTouched && !buttons.get(MOVEBUTTON).getClicked()) {
			//make points easier to click
			for (Point p : points) {
				p.makeBig();
			}
			
			checkAt2ndPoint();
    	}
		if (buttons.get(MOVEBUTTON).getClicked()) {
			if (tmp1stPointIndex != -1) {
				pinMouseToPoint(tmp1stPointIndex);
			}
		}
//		if (!placedWheel && buttons[WHEELBUTTON].getClicked()) {
//			print("prev:"+prevMousePos[1]+", new:"+mousePos[1]);
//			createWheel(prevMousePos,false); //delete
//			createWheel(mousePos,true); //create
//		}
		
    	//make point bigger if hovering above it, else smaller (normal size)
    	processHovering();
	}
	public void createWheel(float[] pos, boolean stick) {
		float radius = 100;
		int n = 10;
		int nRings = 8;
		
		boolean hideSpokes = false;
		int firstPind = points.size(); //index of center point
		
		//CREATE WHEEL FRAME
		addPoint(pos); //add center point
		//add circum points
		float angle;
		//add points
		for (int ring = 0; ring < nRings; ring++) {
			for (int i = 0; i < n; i++) {
				angle = i*2*(float)Math.PI/n;
				float x = pos[0]+((ring+1)*radius/nRings)*(float)Math.cos(angle);
				float y = pos[1]+((ring+1)*radius/nRings)*(float)Math.sin(angle);
				addPoint(new float[] {x,y});
			}
		}
		//print(firstPind+" "+lastPind);
		//add edges and spokes
//		for (int i = firstPind+1; i < firstPind+n; i++) {
//			addSpring(i,i+1,stick,false); //inner edges
//			addSpring(firstPind,i, stick, hideSpokes); //inner spokes
//			addSpring(i+n,i+n+1,stick,false); //outer edge
//			addSpring(i,i+n,stick, hideSpokes); //outer spokes
//			
//			addSpring(i,i+n+1,stick,false);
//			addSpring(i,i+n-1,stick,false);
//			if (i > 1) addSpring(i+n,i-1,stick,false);
//			addSpring(i+n,i+1,stick,false);
//		}
		print(points.size()-firstPind+"size of points");
		for (int ring = 0; ring < nRings; ring++) {
			for (int i = firstPind+ring*n+1; i < firstPind+ring*n+n; i++) {
				addSpring(i,i+1,stick,false); //inner edges
				if (ring == 0) addSpring(firstPind,i,stick,hideSpokes);
				//else addSpring(firstPind+ring*n,i,stick,hideSpokes);
				//addSpring(i+n,i+n+1,stick,false); //outer edge
				//print((i+n)+","+(i+n+1));
				if (ring > 0) {
					addSpring(i-n,i,stick, hideSpokes); //outer spokes
				}
				
				if (ring > 0) {
					addSpring(i-n,i+1,stick,false);
					addSpring(i-n,i-1,stick,false);
					if (i > firstPind+ring*n+1) addSpring(i,i-n-1,stick,false);
					addSpring(i,i-n+1,stick,false);
				}
			}
			addSpring(firstPind+ring*n+1,firstPind+ring*n+n,stick,false);//edge
			addSpring(firstPind+ring*n,firstPind+ring*n+n,stick,hideSpokes);//outer spoke
			if (ring == 0) {
				//add last inner spoke
				addSpring(firstPind,firstPind+n,stick,hideSpokes);
				continue;
			}
			//add last edges
			addSpring(firstPind+ring*n-n+1,firstPind+ring*n,stick,false);
			addSpring(firstPind+ring*n+1,firstPind+ring*n+n,stick,false);
			//add crosses
			addSpring(firstPind+ring*n,firstPind+ring*n+1,stick,false); 
			addSpring(firstPind+ring*n-n+1,firstPind+ring*n+n,stick,false);
		}
	}
	
	public Sound loadSound(String soundFile) {
		return Gdx.audio.newSound(Gdx.files.internal(AUDIODIR+soundFile));
	}
	public Music loadMusic(String musicFile) {
		return Gdx.audio.newMusic(Gdx.files.internal(AUDIODIR+musicFile));
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

}

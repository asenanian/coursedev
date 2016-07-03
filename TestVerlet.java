package com.mygdx.game;


import java.util.ArrayList;
import java.util.Arrays;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
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
	private float gravity = -1f;
	private float k = 0.005f;
	
	public static int MAXPOINTS = 50;
	public static int MAXSPRINGS = 60;
	
	public int numButtons = 7;
	public static int PINNEDBUTTON = 0;
	public static int HIDDENBUTTON = 1;
	public static int MOVEBUTTON = 2;
	public static int SPRINGBUTTON = 3;
	public static int STICKBUTTON = 4;
	public static int CLEARBUTTON = 5;
	public static int CURVEBUTTON = 6;
	public int indexOfClick = -1;
	public static int CREATING = 0;
	public static int PLAYING = 1;
	public int gameState;
	public int tmp1stPointIndex = -1;
	public int tmp2ndPointIndex = -1;
	public boolean hovering = false;
	//public boolean pinned = false;
	//public boolean moving = false;
	//public boolean makeStick = false;
	public boolean isTouched = true;
	@Override
	public void create () {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		FLOOR = HEIGHT/6;
		CEILING = 5*HEIGHT/6;
		LEFTWALL = WIDTH/6;
		RIGHTWALL = 5*WIDTH/6;
		
		cam = new OrthographicCamera(WIDTH,HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2);
		cam.update();
		Gdx.input.setInputProcessor(new GameInputProcessor());
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
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
									80f,80f,"Verlet/stick.png");
		//move tool button
		buttons[MOVEBUTTON] = new Button(new float[] {50,MOVEBUTTON*HEIGHT/n},
									80f,80f,"Verlet/moveTool.png");
		//clear screen button
		buttons[CLEARBUTTON] = new Button(new float[] {50,CLEARBUTTON*HEIGHT/n},
									60f,60f,"Verlet/bomb.png");
		//hidden support
		buttons[HIDDENBUTTON] = new Button(new float[] {50,HIDDENBUTTON*HEIGHT/n},
									120f,60f,"Verlet/hiddenStick.png");
		//draw line by dragging
		buttons[CURVEBUTTON] = new Button(new float[] {50,CURVEBUTTON*HEIGHT/n},
											60f,60f,"Verlet/lineTool.png");
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
	
	public void updateFrames() {
		updateSprings();
		for (Spring s : springs) {
			float[] point1pos = s.getPoint1().getPos();
			float[] point2pos = s.getPoint2().getPos();
//				print(point1pos[0]+" p1pos, p2pos: "+point2pos[0]);
			float dx = point2pos[0] - point1pos[0];
			float dy = point2pos[1] - point1pos[1];
			float dist = (float)Math.sqrt(dx * dx + dy * dy); //dist bt points
//				print(dist+" dist, length: "+s.getLength());
			float diff = dist - s.getLength();
			Point tmp1 = points.get(s.getP1ind());
			Point tmp2 = points.get(s.getP2ind());
			if (s instanceof Stick) {
				//for rigid body
				float percent = diff / dist / 2;
				float offsetX = dx * percent;
				float offsetY = dy * percent;
//					Point tmp1 = points.get(s.getP1ind());
//					Point tmp2 = points.get(s.getP2ind());
				tmp1.addXY(offsetX, offsetY);
				tmp2.addXY(-offsetX, -offsetY);
//					points.set(s.getP1ind(), tmp1);
//					points.set(s.getP2ind(), tmp2);
			} else {
				//for spring restoring force
				float[] restoringAccels = s.getRestoringAccels(diff);
				float p1accel = restoringAccels[0];
				float p2accel = restoringAccels[1];
				
				float angle = getAngle(point1pos,point2pos);
//					Point tmp1 = points.get(s.getP1ind());
//					Point tmp2 = points.get(s.getP2ind());
				tmp1.addVect(p1accel,angle);
				tmp2.addVect(p2accel,angle);
//					points.set(s.getP1ind(), tmp1);
//					points.set(s.getP2ind(), tmp2);
				
//				print(p1accel+" , "+p2accel);
			}
			points.set(s.getP1ind(), tmp1);
			points.set(s.getP2ind(), tmp2);
		}
		
		updateSprings(); //give springs up-to-date versions of points
	}
	public void print(Object obj) {
		System.out.println(obj);
	}
	public float getAngle(float[] p1, float[] p2) {
		return (float)Math.atan2(p2[1]-p1[1],p2[0]-p1[0]);
	}

	public float[] getMousePos() {
		//invert y value to make bottom y = 0, not height
		float[] mousePos = new float[] {Gdx.input.getX(), HEIGHT-Gdx.input.getY()};
		return mousePos;
	}
	
	public boolean inButton(float[] pos, int b) {
		Button button = buttons[b];
		//print(button.getPos()[0]+","+button.getPos()[1]+'\t'+pos[0]+","+pos[1]);
		if (pos[0] > button.getPos()[0] && pos[0] < button.getPos()[0]+button.getWidth() &&
				pos[1] > button.getPos()[1] && pos[1] < button.getPos()[1]+button.getHeight()) {
			return true;
		}
		return false;
	}
	public void updatePoints() {
		for (int i = 0; i < points.size(); i++) {
    		Point tmp = new Point(points.get(i));
    		tmp.update(gravity);
    		points.set(i, tmp);
    		//print(origPoints.get(0).getPos()[1]+" after");
    		//print(points[i].getPos()[1]);
	    }
	}

	
	
	
	
	public void drawBounds() {
		//TO DO: DRAW BOUNDS
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.line(LEFTWALL, CEILING, RIGHTWALL, CEILING);
		shapeRenderer.line(RIGHTWALL, CEILING, RIGHTWALL, FLOOR);
		shapeRenderer.line(RIGHTWALL, FLOOR, LEFTWALL, FLOOR);
		shapeRenderer.line(LEFTWALL, FLOOR, LEFTWALL, CEILING);
		shapeRenderer.end();
	}
	
	
	
	
	
	public void drawSprings() {
		shapeRenderer.begin(ShapeType.Filled); //for filled rectline
	    Color color = Color.RED;
	    for (Spring s : springs) {
	    	if (s != null) {
	    		if (s instanceof Stick) {
	    			//after shift key
	    			color = Color.BLUE;
	    		} else {
	    			color = Color.RED;
	    		}
	    		shapeRenderer.setColor(color);
	    		s.draw(shapeRenderer);
	    	}
	    }
	    shapeRenderer.end();
	}
	public void drawPoints() {
		Color color = Color.WHITE;
		shapeRenderer.begin(ShapeType.Filled);
		for (Point p : points) {
			if (p.getPinned()) {
				//after 'P' key
				color = Color.BLUE;
			} else {
				color = Color.WHITE;
			}
			shapeRenderer.setColor(color);
			p.draw(shapeRenderer);
		}
		shapeRenderer.end();
	}
	public void drawSpringBuilder(float[] mousePos) {
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
	
	public void drawCurve(float[] mousePos) {
		float lineWidth = 0.001f;
		if (tmp1stPointIndex != -1) {
			drawSpringBuilder(mousePos);
			if (distance(points.get(tmp1stPointIndex).getPos(),mousePos) > lineWidth) {
				if (inBounds(mousePos)) {
					tmp1stPointIndex = points.size();
					addPoint(mousePos,true); //add abstract point
					addSpring(points.size()-2,points.size()-1);
					//print(springs.size()+"springs size,  points size:"+points.size());
				}
			}
		}
	}
	public float distance(float[] pos1, float[] pos2) {
		return (float)Math.sqrt(Math.pow(pos1[0]-pos2[0],2) + 
								Math.pow(pos1[1]-pos2[1],2));
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
	
	public boolean inBounds(float[] pos) {
		if (pos[0] > TestVerlet.LEFTWALL && pos[0] < TestVerlet.RIGHTWALL &&
				pos[1] > TestVerlet.FLOOR && pos[1] < TestVerlet.CEILING) {
			return true;
		}
		return false;
	}
	public void addPoint(float[] pos, boolean isAbstract) {
		points.add(new Point(pos.clone(), pos.clone(), isAbstract,
							buttons[PINNEDBUTTON].getClicked()));
	}
	public void addPoint(float[] pos) {
		points.add(new Point(pos.clone(), pos.clone(), 
							buttons[PINNEDBUTTON].getClicked()));
	}
	public void addSpring(int ind1, int ind2) {
		//check if p1 and p2 are valid, then add spring
		if (points.get(ind1) != null && points.get(ind2) != null) {
			if (buttons[HIDDENBUTTON].getClicked()) {
				springs.add(new Stick(points,ind1,ind2,true));
			} else {
				if (buttons[STICKBUTTON].getClicked()) {
					springs.add(new Stick(points,ind1,ind2,false));
				} else {
					float w = 4;
					springs.add(new Spring(points,ind1,ind2,k,w));
				}
			}
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
		float[] mousePos = getMousePos();
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

	public boolean processButtonClicks(float[] mousePos) {
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
        		init();
    		}
    		return true;
    	}
    	return false;
	}
	public void processMouseDown() {
    	float[] mousePos = getMousePos();
    	boolean rc = processButtonClicks(mousePos);
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
	    			}
	    			addPoint(mousePos,isAbstract); //adds point to points array
	    			//print("added abstract point!");
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
	
	public void checkAt2ndPoint() {
		//check if finished drawing spring to 2nd point--no need for mouseUp
		if (tmp1stPointIndex != -1) {
			//clicked on first point to start building a spring...
    		float[] mousePos = getMousePos();
    		for (int i = 0; i < points.size(); i++) {
    			if (tmp1stPointIndex == i) continue;
    	    	Point p = points.get(i);
    	    	if (p.containsPos(mousePos)) {
    	    		tmp2ndPointIndex = i; //end point of spring
    	    		//add spring connecting two points
    	    		//print(tmp2ndPointIndex+" ,"+tmp1stPointIndex);
    	    		addSpring(tmp1stPointIndex,tmp2ndPointIndex);
    	   
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
	    } else if (GameKeys.occurs(GameKeys.R)) {
	    	//this is supposed to restart the object falling.
	    	//print(points[0].getPos()[1]);
	    	restart();
	    	//print(points[0].getPos()[1]);
	    	
	    } else if (GameKeys.occurs(GameKeys.SHIFT)) {
	    	//make sturdier spring, "stick"
	    	buttons[STICKBUTTON].setClicked(!buttons[STICKBUTTON].getClicked());
	    	
	    } else if (GameKeys.occurs(GameKeys.DEL)) {
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
	    	gameState = PLAYING;
	    	print("points:"+points);
	    	print("spring:"+springs);
	    	//print(origPoints[0].getPos()[1]+" before anything");
	    	origPoints = (ArrayList<Point>) points.clone();
			origSprings = (ArrayList<Spring>) springs.clone();	
			
	    	//print(origPoints.get(0).getPos()[1]+" origpoints[1] at ENTER");
	    	//make all points small
    		for (Point p : points) {
    			p.makeSmall();
    		}
    		
	    } else if (GameKeys.occurs(GameKeys.ESCAPE)) {
	    	init();
	    } 
	}
	
	public void updatePlayState(float[] mousePos) {
		if (indexOfClick != -1) {
			pinMouseToPoint(mousePos,indexOfClick);
		}
        //update points
	    updatePoints();
	    //update springs
	    int expensiveness = 500; //aka realisticness
	    for(int i = 0; i < expensiveness; i++) {
	    	updateFrames();
	    }
	}
	
	public void updateCreatingState(float[] mousePos) {
		if (isTouched && !buttons[MOVEBUTTON].getClicked()) {
			if (buttons[CURVEBUTTON].getClicked()) {
				drawCurve(mousePos);
			} else {
				//make points easier to click
				for (Point p : points) {
					p.makeBig();
				}
				//print(tmp1stPointIndex);
				if (tmp1stPointIndex != -1) {
					//print("hello");
					drawSpringBuilder(mousePos);
				}
	    		checkAt2ndPoint();
			}
    	}
		if (buttons[MOVEBUTTON].getClicked()) {
			if (tmp1stPointIndex != -1) {
				pinMouseToPoint(mousePos,tmp1stPointIndex);
			}
		}
    	//make point bigger if hovering above it, else smaller (normal size)
    	processHovering();
	}
	public void pinMouseToPoint(float[] mousePos,int index) {
		Point p = points.get(index);
    	p.setPos(mousePos);
    	p.setPrevPos(getMousePos());
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
        float[] mousePos = getMousePos();
        if (gameState == CREATING) {
        	updateCreatingState(mousePos);
        	//draw red/blue lines
            drawSprings();
            //draw points
        	drawPoints();
        } else if (gameState == PLAYING) {
        	updatePlayState(mousePos); 
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
		
	}
	
}

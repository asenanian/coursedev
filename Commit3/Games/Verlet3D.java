package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.mygdx.managers.GameKeys;
import com.mygdx.managers.GameStateManager;

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
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.entities.Button;
import com.mygdx.entities.Point;
import com.mygdx.entities.Spring;
//import com.mygdx.entities.Stick;
import com.mygdx.managers.CameraController;
import com.mygdx.managers.GameInputProcessor;
import com.mygdx.managers.GameKeys;

public class Verlet3D extends ApplicationAdapter {
	public static int WIDTH = 800;//Gdx.graphics.getWidth();
	public static int HEIGHT = 700;//Gdx.graphics.getHeight();
	public static int DEPTH = WIDTH;
	public static int FLOOR = HEIGHT/4;
	public static int CEILING = 3*HEIGHT/4;
	public static int LEFTWALL = WIDTH/4;
	public static int RIGHTWALL = 3*WIDTH/4;
	public static int FRONTWALL = -DEPTH/4;
	public static int BACKWALL = DEPTH/4;
	public static float[] CENTER = {(RIGHTWALL+LEFTWALL)/2,
									 (FLOOR+CEILING)/2,
									 0};
	public static PerspectiveCamera cam;
	public static int CREATING = 0;
	public static int PLAYING = 1;
	
	public int gameState;
	private ShapeRenderer shapeRenderer;
	private ArrayList<Point> origPoints;
	private ArrayList<Point> points;
	private ArrayList<Spring> origSprings;
	private ArrayList<Spring> springs;
	private float timeConstant = 0.001f;
	private float gravity = -timeConstant*100f;
	private float k = timeConstant*1000f;
	//private float w = 500f; //width of springs
	private int expensiveness = 10; //aka realisticness (1 to 1000 please)
	private Music danubeMusic;
	private Sound scratchSound;
	private boolean audioOn;
	public CameraController camController;
	public GameInputProcessor gameInputProcessor;
	public InputMultiplexer inputMultiplexer;
	//private GameStateManager gsm;
	private ModelBatch modelBatch;
	private Environment environment;
	
	@Override
	public void create () {
//		WIDTH = Gdx.graphics.getWidth();
//		HEIGHT = Gdx.graphics.getHeight();
//		DEPTH = WIDTH;
//		FLOOR = HEIGHT/6;
//		CEILING = 5*HEIGHT/6;
//		LEFTWALL = WIDTH/6;
//		RIGHTWALL = 5*WIDTH/6;
//		FRONTWALL = DEPTH/6;
//		BACKWALL = 5*DEPTH/6;
		//print("Floor:"+FLOOR);
		//print("CEILING:"+CEILING);
		
		cam = new PerspectiveCamera(67, WIDTH, HEIGHT);
		cam.position.set(WIDTH/2, HEIGHT/2, DEPTH);
		//cam.direction.set(0,0,0);
		cam.lookAt(WIDTH/2,HEIGHT/2,0f);
		cam.near = 0f;
		cam.far = 500f;
		cam.update();
		
		shapeRenderer = new ShapeRenderer();
		modelBatch = new ModelBatch();
		
		scratchSound = loadSound("scratch.mp3");
		danubeMusic = loadMusic("danube.mp3");
		audioOn = false;
		
		//shine light on stuff
	    environment = new Environment();
	    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
        camController = new CameraController(cam);
		
        gameInputProcessor = new GameInputProcessor();
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(camController);
		inputMultiplexer.addProcessor(gameInputProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
        init();
	}
	
	public void init() {
		initPoints();
	    initSprings();
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
	
	public void addPoint(float[] prevPos, float[] pos) {
		points.add(new Point(pos, prevPos, true, false, false));
	}
	public void addPoint(float[] pos, boolean isAbstract) {
		points.add(new Point(pos, pos, true, isAbstract,false));
	}
	public void addPoint(float[] pos) {
		points.add(new Point(pos, pos, true, false, false));
	}
	
	public void addSpring(int ind1, int ind2, boolean stick, boolean hidden) {
		if (stick) {
			springs.add(new Spring(points,ind1,ind2,9999f,hidden));
		} else {
			print("if k > 0.3f, you just added a stick: "+k);
			springs.add(new Spring(points,ind1,ind2,k,hidden));
		}
	}
	
	public void updateSphere(float[] pos, boolean stick, boolean create) {
		//ONLY DOES CIRCLE IN XY PLANE AT THE MOMENT...
		print(points.size()+"before");
		float radius = 100;
		int n = 20;
		//boolean hideSpokes = false;
		//int firstPind = points.size(); 
		//int firstSind = springs.size();
		//int lastSind = firstSind + (n-2)*4 + 1;
		if (create) {
			//add edge points
			for (int i = 0; i < n; i++) {
				float angle = i*2*(float)Math.PI/n;
				//outer points
				float x1 = pos[0]+(9*radius/10)*(float)Math.cos(angle);
				float y1 = pos[1]+(9*radius/10)*(float)Math.sin(angle);
				float z1 = 0f;
				//inner points
				float x2 = pos[0]+radius*(float)Math.cos(angle + (float)Math.PI/4);
				float y2 = pos[1]+radius*(float)Math.sin(angle + (float)Math.PI/4);
				float z2 = 0f;
				addPoint(new float[] {x1,y1,z1});
				addPoint(new float[] {x2,y2,z2});
				addSpring(points.size()-2,points.size()-1,stick,false);

			}
		}
		print(points.size()+"after");
		//print("updated sphere");
			//print(firstPind+" "+lastPind);
			//add edges and spokes
//			for (int i = firstPind+1; i < firstPind+n; i++) {
//				addSpring(i,i+1,stick,false); //inner edges
//				addSpring(firstPind,i, stick, hideSpokes); //inner spokes
//				addSpring(i+n,i+n+1,stick,false); //outer edge
//				addSpring(i,i+n,stick, hideSpokes); //outer spokes
//			}
//			//add last edges + last spokes
//			addSpring(firstPind+n,firstPind+1,stick,false);
//			addSpring(firstPind+2*n,firstPind+n+1,stick,false);
//			addSpring(firstPind,firstPind+n,stick,hideSpokes);
//			addSpring(firstPind+n,firstPind+2*n,stick,hideSpokes);
//		} else {
//			//delete circle
//			for (int i = firstPind; i < firstPind+2*n; i++) {
//				points.remove(i);
//			}
//			for (int i = firstSind; i < lastSind; i++) {
//				springs.remove(i);
//			}
//		}
	}
	
	public void updatePoints() {
		for (int i = 0; i < points.size(); i++) {
			//Point tmp = new Point(points.get(i));
			//tmp.update(gravity);
			//points.set(i, tmp);
			points.get(i).update(gravity);
			//print(points.get(i).getPos()[1]);
		}
	}

	public void updateSpringPoints() {
		for (int i = 0; i < springs.size(); i++) {
			Spring s = springs.get(i);
			if (s.getP1ind() >= points.size() || s.getP2ind() >= points.size()) {
				springs.remove(i);
				i--;
			} else {	
				//update versions of points that spring sees
				//print(springs.size());
				//print(points.size());
				s.update(points);
			}
		}
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
	
	public void drawBounds() {
		//draw big cube boundary
		
		shapeRenderer.begin(ShapeType.Line);
		//shapeRenderer.line(x, y, z, x2, y2, z2);
		// bottom: lf-rf,rf-rb,lf-lb,lb-rb
		// top: same as bottom
		// left side: (bo)f-(to)f,(bo)b-(to)b
		// right side
		//12 lines
		
		//ceiling
		shapeRenderer.line(LEFTWALL, CEILING, BACKWALL, RIGHTWALL, CEILING, BACKWALL);
		shapeRenderer.line(LEFTWALL, CEILING, FRONTWALL, RIGHTWALL, CEILING, FRONTWALL);
		shapeRenderer.line(LEFTWALL, CEILING, FRONTWALL, LEFTWALL, CEILING, BACKWALL);
		shapeRenderer.line(RIGHTWALL, CEILING, FRONTWALL, RIGHTWALL, CEILING, BACKWALL);
		
		//floor
		shapeRenderer.line(LEFTWALL, FLOOR, BACKWALL, RIGHTWALL, FLOOR, BACKWALL);
		shapeRenderer.line(LEFTWALL, FLOOR, FRONTWALL, RIGHTWALL, FLOOR, FRONTWALL);
		shapeRenderer.line(LEFTWALL, FLOOR, FRONTWALL, LEFTWALL, FLOOR, BACKWALL);
		shapeRenderer.line(RIGHTWALL, FLOOR, FRONTWALL, RIGHTWALL, FLOOR, BACKWALL);
		
		//four z-axis lines
		shapeRenderer.line(LEFTWALL, FLOOR, BACKWALL, LEFTWALL, CEILING, BACKWALL);
		shapeRenderer.line(LEFTWALL, FLOOR, FRONTWALL, LEFTWALL, CEILING, FRONTWALL);
		shapeRenderer.line(RIGHTWALL, FLOOR, BACKWALL, RIGHTWALL, CEILING, BACKWALL);
		shapeRenderer.line(RIGHTWALL, FLOOR, FRONTWALL, RIGHTWALL, CEILING, FRONTWALL);
		shapeRenderer.end();
	}
	
	public void drawSprings() {
	    for (Spring s : springs) {
	    	s.draw(modelBatch, environment);
	    }
	    //test making cylinder
	    ModelBuilder modelBuilder = new ModelBuilder();
	    Model cyl = new Model();
	    cyl = modelBuilder.createCylinder(10f, 200f, 20f, 24, 
	    		new Material(ColorAttribute.AmbientAlias),
    					Usage.Position | Usage.Normal, 0, 360);
	    ModelInstance inst = new ModelInstance(cyl);
	    inst.transform.setToRotation(Vector3.Y, Vector3.X);
	    inst.transform.setToLookAt(new Vector3(0,0,0), Vector3.Y);
	    inst.transform.setToTranslation(500,0,0);
	    inst.calculateTransforms();
	    modelBatch.render(inst,environment);
	}
	
	public void drawPoints() {
		for (Point p : points) {
			p.draw(modelBatch, environment);
		}
	}
	
	public void print(Object obj) {
		System.out.println(obj);
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
	
	public void updatePlayState(float dt) {
		//fade in music
		if (audioOn) {
			if (danubeMusic.getVolume() < 1.0f) {
				danubeMusic.setVolume(danubeMusic.getVolume()+0.01f);
			}
		}
//		//pin points to mouse position so you can move points
//		if (indexOfClick != -1) {
//			pinMouseToPoint(indexOfClick);
//		}
        //update points (move them)
	    updatePoints();
	    //update spring forces/rigid frames

	    for(int i = 0; i < expensiveness; i++) {
	    	updateFrames(dt);
	    }
	}
	
	public void processKeyPresses() {
		if (GameKeys.occurs(GameKeys.ENTER)) {
	    	//play music
	    	if (audioOn) {
	    		danubeMusic.play();
	    		danubeMusic.setVolume(0f);
	    	}
	    	
	    	//set gamestate to play
	    	gameState = PLAYING;
	    	updateSphere(CENTER,false,true);
	    	//copy points to origPoints in order to 
	    	// save original values of points (pos,vel,etc..)
	    	origPoints = (ArrayList<Point>) points.clone();
			origSprings = (ArrayList<Spring>) springs.clone();	
			
//	    	//make all points small
//    		for (Point p : points) {
//    			p.makeSmall();
//    		}
    		
	    } else if (GameKeys.occurs(GameKeys.ESCAPE)) {
	    	if (audioOn) stopMusic();
	    	init();
	    } 
	}
	
	
	@Override
	public void render() {
		//MAIN LOOP
		
		//clear screen to black
		Gdx.gl.glClearColor(0,0,0, 1);
		Gdx.gl.glViewport(0, 0, WIDTH, HEIGHT);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	    //print(pinned);
        
        camController.update();
        shapeRenderer.setProjectionMatrix(cam.combined);
        modelBatch.begin(cam);
        if (gameState == CREATING) {
        	//updateCreatingState();
        	//draw red/blue lines
//        	updateSphere(CENTER,false,true);
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
	    //drawButtons();
	    
	    drawBounds();
	    
	    modelBatch.end();
	    
	    //check mouse+key activity
	    processKeyPresses();
	    
	    GameKeys.update();
	    
	    
	    
	    //reset prevMousePos
	    //prevMousePos = (float[]) mousePos.clone();
		
	}
}

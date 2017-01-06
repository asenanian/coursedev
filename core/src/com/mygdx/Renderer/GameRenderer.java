package com.mygdx.Renderer;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.InputMultiplexer;


import com.mygdx.Entities.Joints.IJoint;
import com.mygdx.Entities.GameObjects.Field;
import com.mygdx.Entities.Modifiers.IModifier;
import com.mygdx.Entities.GameObjects.PolyBody;
import com.mygdx.Entities.GameObjects.Chain;
import com.mygdx.Entities.GameObjects.IGameObject;

import com.mygdx.GameWorld.GameConstants;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.InputProcessing.AssetLoader;
import com.mygdx.InputProcessing.GameInputProcessor;
import com.mygdx.InputProcessing.MenuInputProcessor;
import com.mygdx.ui.SimpleButton;

public class GameRenderer {
	
	private GameManager manager;
	private Builder builder;
	
	// Actors
	private ArrayList<IGameObject> points;
	private ArrayList<IJoint> joints;
	private ArrayList<IModifier> modifiers;
	private ArrayList<Field> fields;
	
	// User-Interface
	private ArrayList<Chain> bounds;
	private HashMap<String,SimpleButton> toolBar;
	private HashMap<String,SimpleButton> modifierBar;
	private HashMap<String,SimpleButton> controlBar;
	private HashMap<String,SimpleButton> menuButtons;
	
	//  Cameras
	private OrthographicCamera uiCam;
	private OrthographicCamera worldCam;
	
	// batchers/ renderers
	private ShapeRenderer worldRenderer;
	private SpriteBatch worldBatcher;
	private SpriteBatch screenBatcher;
	private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
	
	// Polygon Rendering
	private EarClippingTriangulator triangulator = new EarClippingTriangulator();
	private PolygonSpriteBatch polyBatch;
	private PolygonRegion polyReg;

	
	private State state;
	
	public enum State {
		NONE, CIRCLE, RECTANGLE, JOINT, MODIFIER, PATH
	}
	
	public GameRenderer(GameManager manager){
		this.state = State.NONE;
		this.manager = manager;
		this.builder = new Builder();
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		// camera with screen coordinates
		uiCam = new OrthographicCamera(GameConstants.WIDTH,GameConstants.HEIGHT);
		uiCam.position.set(GameConstants.WIDTH/2,GameConstants.HEIGHT/2,0);
		uiCam.update();
		
		screenBatcher = new SpriteBatch();
		screenBatcher.setProjectionMatrix(uiCam.combined);
		
		// camera with world coordinates
		worldCam = new OrthographicCamera( 30f, 30f * (h / w));
		worldCam.position.set(worldCam.viewportWidth / 2f - 5, worldCam.viewportHeight / 2f -5,0);
		worldCam.update();
		
		worldBatcher = new SpriteBatch();
		worldBatcher.setProjectionMatrix(worldCam.combined);
		
		polyBatch = new PolygonSpriteBatch();
		polyBatch.setProjectionMatrix(worldCam.combined);
		
		worldRenderer = new ShapeRenderer();
		worldRenderer.setAutoShapeType(true);
		worldRenderer.setProjectionMatrix(worldCam.combined);
					
		initGameObjects();
	}
	
	private void initGameObjects(){
		points = manager.getPoints();
		joints = manager.getJoints();
		modifiers = manager.getModifiers();
		fields = manager.getFields();
		bounds = manager.getBounds();
	}
	
	public void initButtons(){
		// buttons
		this.toolBar = ((GameInputProcessor)((InputMultiplexer) Gdx.input.getInputProcessor()).getProcessors().get(1)).getToolbar();
		this.modifierBar = ((GameInputProcessor)((InputMultiplexer) Gdx.input.getInputProcessor()).getProcessors().get(1)).getModifierBar();
		this.controlBar = ((GameInputProcessor)((InputMultiplexer) Gdx.input.getInputProcessor()).getProcessors().get(1)).getControlBar();
		this.menuButtons = ((MenuInputProcessor)((InputMultiplexer) Gdx.input.getInputProcessor()).getProcessors().get(0)).getMenuButtons();
	}
	
	public void render(float delta, float runTime){
		
		worldCam.update();
		worldBatcher.setProjectionMatrix(worldCam.combined);
		polyBatch.setProjectionMatrix(worldCam.combined);
		worldRenderer.setProjectionMatrix(worldCam.combined);
        
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glClearColor(79/255f, 190/255f, 241/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);    
        Gdx.gl.glLineWidth(2);
        
        screenBatcher.begin();
        screenBatcher.disableBlending();
        drawBackground();
        screenBatcher.enableBlending();
        screenBatcher.end();

        
        if(manager.isCreate() || manager.isRunning()){
        	
        	// draw in physics coordinates        	
        	worldBatcher.begin();
    		for( IGameObject body : points){
    			body.drawShadows(worldBatcher);
    		}
    		for ( IJoint joint : joints){
    			joint.draw(worldBatcher);
    		}
    		for( IGameObject body : points ){ 
    			body.draw(worldBatcher);
    		}
    		for ( Field field : fields ){
    			field.draw(worldBatcher);
    		}
    		for ( IModifier m : modifiers) {
    			m.draw(worldBatcher);
    		}
    		for ( Chain boundEdge : bounds){
    			boundEdge.drawShadows(worldBatcher);
    		}
    		for ( Chain boundEdge : bounds){
    			boundEdge.draw(worldBatcher);
    		}
    		worldBatcher.end();
    		
        	worldRenderer.begin();
        	drawCanvasShapes();
        	worldRenderer.end();
        	
        } 
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        screenBatcher.begin();
        if(manager.isCreate() || manager.isRunning()){
        	drawToolbar();
        	drawUISprites();
		}
        else if(manager.isMenu()){
        	drawMenuSprites();
        }
		screenBatcher.end();
        
        /* DEBUG 
        debugRenderer.render(manager.getWorld(), worldCam.combined);
        /* ********************************* */
	}
	
	public void drawBackground(){
		if(manager.isCreate() || manager.isRunning())
			screenBatcher.draw(AssetLoader.background, 0,0,GameConstants.WIDTH, GameConstants.HEIGHT);
	}
	
	public void drawMenuSprites(){
		for (SimpleButton b : menuButtons.values()) {
			b.draw(screenBatcher);
		}
	}
	
	public void drawCanvasShapes() {
		drawPoints();
		
		switch(state){
		case NONE:
			break;
		case CIRCLE:
			builder.drawCircle(worldRenderer);
			break;
		case RECTANGLE:
			builder.drawRectangle(worldRenderer);
			break;
		case JOINT:
			builder.drawJoint(worldRenderer);
			break;
		case MODIFIER:
			builder.drawModifier(worldRenderer);
			break;
		case PATH:
			builder.drawPath(worldRenderer);
			break;
		default:
			break;
		}

	}
	
	public void drawToolbar(){
		screenBatcher.draw(AssetLoader.rectangleShadow,-GameConstants.WIDTH,GameConstants.HEIGHT - GameConstants.LC_HEIGHT - 5*GameConstants.LC_PADDING, 3*GameConstants.WIDTH , GameConstants.LC_HEIGHT + 2*GameConstants.LC_PADDING);
		screenBatcher.setColor(new Color(64/255f, 64/255f,64/255f,1f));
		screenBatcher.draw(AssetLoader.chain,0, 0, GameConstants.WIDTH , GameConstants.LC_HEIGHT + 2*GameConstants.LC_PADDING);
		screenBatcher.draw(AssetLoader.chain,0,GameConstants.HEIGHT - GameConstants.LC_HEIGHT - 2*GameConstants.LC_PADDING, GameConstants.WIDTH , GameConstants.LC_HEIGHT + 2*GameConstants.LC_PADDING);
		screenBatcher.setColor(Color.WHITE);

	}
	
	public void drawUISprites() {
		for (SimpleButton b : toolBar.values()) {
			b.draw(screenBatcher);
		}
		for (SimpleButton b : controlBar.values()) {
			b.draw(screenBatcher);
		}
		for (SimpleButton b : modifierBar.values()){
			b.draw(screenBatcher);
		}
		switch(state){
		case MODIFIER:
			builder.drawModifierText(screenBatcher, worldCam);
			break;
		case NONE:
			break;
		default:
			break;
		}
	}
	
	public void drawPoints(){
		worldRenderer.set(ShapeType.Filled);

		for( IGameObject body : points ){

			if (body instanceof PolyBody){

				float vertices [] = ((PolyBody) body).getVertices();
				ShortArray triangleIndices = triangulator.computeTriangles(vertices);
				
				polyReg = new PolygonRegion(AssetLoader.rectangle, vertices, triangleIndices.toArray());

				polyBatch.begin();
				polyBatch.draw(polyReg, 0, 0);
				polyBatch.end();
			}
		}
	}
	
	public void restart(){
		initGameObjects();
	}
	
	public void buildCircle(float [] position){
		state = State.CIRCLE;
		builder.init(position[0], position[1]);
	}
	
	public void buildRect( float [] position){
		state = State.RECTANGLE;
		builder.init(position[0], position[1]);
	}
	
	public void buildJoint( float [] position ){
		state = State.JOINT;
		builder.init(position[0], position[1]);
	}
	
	public void buildModifier( float [] position ){
		state = State.MODIFIER;
		builder.init(position[0], position[1]);
	}
	
	public void buildPath(float [] position, ArrayList<Vector2> path){
		state = State.PATH;
		builder.init(position[0], position[1], path);
	}
	
	public void endBuilder(){
		state = State.NONE;
		builder.dispose();
	}
	
	public void updateBuilder(float [] position){
		builder.update(position[0], position[1]);
	}
	
	public boolean isBuilding(){
		return !(state == State.NONE);
	}
	
	public OrthographicCamera getWorldCam(){
		return this.worldCam;
	}
	
	public void zoomCamera(int value){
		worldCam.zoom += value*.25;
		
		clampCamera();
	}
	
	public void translateCamera(float dx, float dy){
		dx = dx/4*(float)(Math.sqrt(worldCam.zoom));
		dy = dy/4*(float)(Math.sqrt(worldCam.zoom));

		worldCam.translate(dx,dy,0);
		
		clampCamera();
	}
	
	private void clampCamera(){
		worldCam.zoom = MathUtils.clamp(worldCam.zoom, 0.1f, 120/worldCam.viewportWidth);
		
		float effectiveViewportWidth = worldCam.viewportWidth*worldCam.zoom;
		float effectiveViewportHeight = worldCam.viewportHeight*worldCam.zoom;
		
		// clamp the camera to a 100 x 100 square (meters)
		worldCam.position.x = MathUtils.clamp(worldCam.position.x, effectiveViewportWidth / 2f - 5, GameConstants.W_WIDTH - effectiveViewportWidth / 2f + 5);
		worldCam.position.y = MathUtils.clamp(worldCam.position.y, effectiveViewportHeight / 2f - 5, GameConstants.W_HEIGHT - effectiveViewportHeight / 2f + 5);
	}
	
	public void dispose(){
		screenBatcher.dispose();
	}
}

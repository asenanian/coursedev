package com.mygdx.Renderer;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.InputMultiplexer;


import com.mygdx.Entities.IJoint;
import com.mygdx.Entities.IModifier;
import com.mygdx.Entities.IGameObject;
import com.mygdx.Entities.Rect;

import com.mygdx.GameWorld.GameConstants;
import com.mygdx.GameWorld.GameManager;

import com.mygdx.managers.AssetLoader;
import com.mygdx.managers.GameInputProcessor;
import com.mygdx.managers.MenuInputProcessor;

import com.mygdx.ui.SimpleButton;

public class GameRenderer {
	
	private GameManager manager;
	private Builder builder;
	
	// Actors
	private ArrayList<IGameObject> points;
	private ArrayList<IJoint> joints;
	private ArrayList<IModifier> modifiers;
	private Rect bounds;
	
	// Buttons
	private HashMap<String,SimpleButton> toolbar;
	private HashMap<String,SimpleButton> controlBar;
	private HashMap<String,SimpleButton> menuButtons;

	private OrthographicCamera uiCam;
	private OrthographicCamera worldCam;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batcher;
	//private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer()

	
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
		
		// camera with world coordinates
		worldCam = new OrthographicCamera( 30f, 30f * (h / w));
		worldCam.position.set(worldCam.viewportWidth / 2f, GameConstants.W_HEIGHT - worldCam.viewportHeight / 2f, 0);
		worldCam.update();
		
		batcher = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
					
		initGameObjects();
	}
	
	private void initGameObjects(){
		points = manager.getPoints();
		joints = manager.getJoints();
		bounds = manager.getBounds();
		modifiers = manager.getModifiers();
	}
	
	public void initButtons(){
		// buttons
		this.toolbar = ((GameInputProcessor)((InputMultiplexer) Gdx.input.getInputProcessor()).getProcessors().get(1)).getToolbar();
		this.controlBar = ((GameInputProcessor)((InputMultiplexer) Gdx.input.getInputProcessor()).getProcessors().get(1)).getControlBar();
		this.menuButtons = ((MenuInputProcessor)((InputMultiplexer) Gdx.input.getInputProcessor()).getProcessors().get(0)).getMenuButtons();
	}
	
	public void render(float delta, float runTime){
		
		worldCam.update();
        
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glClearColor(79/255f, 190/255f, 241/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);    
        Gdx.gl.glLineWidth(2);
        
        batcher.setProjectionMatrix(uiCam.combined);
        batcher.begin();
        drawBackground();
		batcher.end();
        
        if(manager.isCreate() || manager.isRunning()){
        	
        	// draw in physics coordinates
        	shapeRenderer.setProjectionMatrix(worldCam.combined);
        	batcher.setProjectionMatrix(worldCam.combined);
        	
        	batcher.begin();
        	shapeRenderer.begin();
        	drawCanvas();
        	shapeRenderer.end();
        	batcher.end();
        	
        	//draw in screen coordinates
        	shapeRenderer.setProjectionMatrix(uiCam.combined);
        	shapeRenderer.begin();
        	drawToolbar();
        	shapeRenderer.end();
        } 
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        //draw sprites
    	batcher.setProjectionMatrix(uiCam.combined);
        batcher.begin();
        if(manager.isCreate() || manager.isRunning()){
        	drawUISprites();
		}
        else if(manager.isMenu()){
        	drawMenuSprites();
        }
        batcher.end();
        
        /* DEBUG 
        canvasRenderer.begin(); 
        canvasRenderer.circle(mouse.x, mouse.y, 0.1f);
        canvasRenderer.end();
        debugRenderer.render(manager.getWorld(), worldCam.combined);
        /* ********************************* */
	}
	

	
	public void drawBackground(){
		if(manager.isCreate() || manager.isRunning())
			batcher.draw(AssetLoader.background, 0,0,GameConstants.WIDTH, GameConstants.HEIGHT);
	}
	
	public void drawMenuSprites(){
		for (SimpleButton b : menuButtons.values()) {
			b.draw(batcher);
		}
	}
	
	public void drawCanvas() {
		shapeRenderer.set(ShapeType.Line);
		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.rect(0, 0, 100, 100);
		
		if ( manager.isCreate()) drawModifiers();
		drawJoints();
		drawPoints();

		// object builder activated by input processor
		switch(state){
		case NONE:
			break;
		case CIRCLE:
			builder.drawCircle(shapeRenderer);
			break;
		case RECTANGLE:
			builder.drawRectangle(shapeRenderer);
			break;
		case JOINT:
			builder.drawJoint(shapeRenderer);
			break;
		case MODIFIER:
			builder.drawModifier(shapeRenderer);
			break;
		case PATH:
			builder.drawPath(shapeRenderer);
			break;
		default:
			break;
		}

	}
	
	public void drawUISprites() {
		for (SimpleButton b : toolbar.values()) {
			b.draw(batcher);
		}
		for (SimpleButton b : controlBar.values()) {
			b.draw(batcher);
		}
		switch(state){
		case MODIFIER:
			builder.drawModifierText(batcher, worldCam);
			break;
		case NONE:
			break;
		default:
			break;
		}
	}
	
	public void drawToolbar(){
		shapeRenderer.set(ShapeType.Filled);
		shapeRenderer.setColor(64/255f, 64/255f, 64/255f, 1);
		shapeRenderer.rect(0, 0, GameConstants.WIDTH, GameConstants.LC_HEIGHT + GameConstants.LC_PADDING+5);
		shapeRenderer.setColor(128/255f, 128/255f, 128/255f, 1);
		shapeRenderer.rect(0, GameConstants.LC_HEIGHT + GameConstants.LC_PADDING + 5, 
				GameConstants.WIDTH, 5);
	}

	public void drawBounds() {
		shapeRenderer.set(ShapeType.Filled);
		bounds.draw(shapeRenderer);
	}
	
	public void drawJoints() {
		shapeRenderer.set(ShapeType.Filled);//for filled rectline
	    for (IJoint j : joints ) {
    		j.draw(manager.getPoints(),shapeRenderer);
	    }
	}
	
	public void drawModifiers(){
		shapeRenderer.set(ShapeType.Filled);
		for(IModifier m : modifiers) 
			m.draw(manager.getPoints(), shapeRenderer);
	}
	
	public void drawPoints(){
		shapeRenderer.set(ShapeType.Filled);
		for( IGameObject body : points){
			body.drawShadows(shapeRenderer, batcher);
		}
		for( IGameObject body : points ){
			body.draw(shapeRenderer);
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
		worldCam.zoom = MathUtils.clamp(worldCam.zoom, 0.1f, 100/worldCam.viewportWidth);
		
		float effectiveViewportWidth = worldCam.viewportWidth*worldCam.zoom;
		float effectiveViewportHeight = worldCam.viewportHeight*worldCam.zoom;
		
		// clamp the camera to a 100 x 100 square (meters)
		worldCam.position.x = MathUtils.clamp(worldCam.position.x, effectiveViewportWidth / 2f, GameConstants.W_WIDTH - effectiveViewportWidth / 2f);
		worldCam.position.y = MathUtils.clamp(worldCam.position.y, effectiveViewportHeight / 2f, GameConstants.W_HEIGHT - effectiveViewportHeight / 2f);
	}
}

package com.mygdx.GameWorld;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.InputMultiplexer;
import com.mygdx.Entities.Point;
import com.mygdx.Entities.Rect;
import com.mygdx.Entities.Spring;
import com.mygdx.managers.GameInputProcessor;
import com.mygdx.managers.MenuInputProcessor;
import com.mygdx.managers.Mouse;
import com.mygdx.ui.SimpleButton;

public class GameRenderer {
	
	private GameWorld world;
	
	// Actors
	//private ArrayList<Point> points;
	//private ArrayList<Spring> springs;
	private Rect bounds;
	
	// Buttons
	private HashMap<String,SimpleButton> toolbar;
	private HashMap<String,SimpleButton> controlBar;
	private HashMap<String,SimpleButton> menuButtons;

	private OrthographicCamera cam;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batcher;
	
	public GameRenderer(GameWorld world){
		this.world = world;
		
		cam = new OrthographicCamera(GameConstants.WIDTH,GameConstants.HEIGHT);
		cam.translate(GameConstants.WIDTH / 2, GameConstants.HEIGHT / 2); //camera looks at this point
		cam.update();
		
		batcher = new SpriteBatch();
		batcher.setProjectionMatrix(cam.combined);
		
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(cam.combined);
		shapeRenderer.setAutoShapeType(true);
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
				
		initGameObjects();
		
	}
	
	private void initGameObjects(){
		//points = world.getPoints();
		//springs = world.getSprings();
		bounds = world.getBounds();
	}
	
	public void initButtons(){
		// buttons
		this.toolbar = ((GameInputProcessor)((InputMultiplexer) Gdx.input.getInputProcessor()).getProcessors().get(1)).getToolbar();
		this.controlBar = ((GameInputProcessor)((InputMultiplexer) Gdx.input.getInputProcessor()).getProcessors().get(1)).getControlBar();
		this.menuButtons = ((MenuInputProcessor)((InputMultiplexer) Gdx.input.getInputProcessor()).getProcessors().get(0)).getMenuButtons();
	}
	
	public void render(float delta, float runTime){
        
		Gdx.gl.glClearColor(79/255f, 190/255f, 241/255f, 1);
		Gdx.gl.glViewport(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);    
        
        // draw shapes
        shapeRenderer.begin();
        if(world.isCreate() || world.isRunning()){
        	drawLevelShapes();
        } 
        shapeRenderer.end();
        
        //draw sprites
        batcher.begin();
        if(world.isCreate() || world.isRunning()){
        	drawLevelSprites();
		}
        else if(world.isMenu()){
        	drawMenuSprites();
        }
        batcher.end();
	}
	
	public void drawMenuSprites(){
		for (SimpleButton b : menuButtons.values()) {
			b.draw(batcher);
		}
	}
	
	public void drawLevelShapes() {
		bounds.draw(shapeRenderer);
		drawSprings();
		drawToolbar();
		drawPoints();
		bounds.drawOutline(shapeRenderer);
		//draw curve or chain
		if (world.isCreate()) { // in creative mode and a touchDown event occurs
			if (Mouse.getIndexBegin() != -1 && Mouse.isDragged()) {
				if(toolbar.get("CIRCLE").getClicked()){
					drawCircleBuilder();
				} else {
					drawSpringBuilder();
				}
			}
		}
	}
	
	public void drawLevelSprites() {
		for (SimpleButton b : toolbar.values()) {
			b.draw(batcher);
		}
		for (SimpleButton b : controlBar.values()) {
			b.draw(batcher);
		}
	}
	
	public void drawToolbar(){
		shapeRenderer.set(ShapeType.Filled);
		shapeRenderer.setColor(64/255f, 64/255f, 64/255f, 1);
		shapeRenderer.rect(0, 0, GameConstants.WIDTH, GameConstants.B_HEIGHT + GameConstants.B_PADDING);
	}

	public void drawBounds() {
		bounds.draw(shapeRenderer);
	}
	
	public void drawSprings() {
		shapeRenderer.set(ShapeType.Filled); //for filled rectline
	    for (Spring s : world.getSprings()) {
    		s.draw(world.getPoints(),shapeRenderer);
	    }
	}
	public void drawPoints() {
		shapeRenderer.set(ShapeType.Filled);
		for (Point p : world.getPoints()) {
			p.draw(shapeRenderer);
		}
	}
	
	public void drawCircleBuilder(){
		float[] pointPos = Mouse.clickedPos;
		float[] pointOnCircle = {Mouse.getMousePos()[0] - pointPos[0],Mouse.getMousePos()[1] - pointPos[1]};
		float radius = (float) Math.sqrt(Math.pow(pointOnCircle[0],2) + Math.pow(pointOnCircle[1],2));

		Color color;
		if (toolbar.get("HIDDEN").getClicked()) {
			color = Color.WHITE;
		} else if (toolbar.get("PINNED").getClicked()) {
			color = Color.BLUE;
		} else {
			//normal spring
			color = Color.RED;
		}
		shapeRenderer.setColor(color);
		shapeRenderer.circle(pointPos[0],pointPos[1],radius);
	}
	public void drawSpringBuilder() {
		
		float[] pointPos = world.getPoints().get(Mouse.getIndexBegin()).getPos();

		Color color;
		if (toolbar.get("HIDDEN").getClicked()) {
			color = Color.WHITE;
		} else if (toolbar.get("STICK").getClicked()) {
			color = Color.BLUE;
		} else {
			//normal spring
			color = Color.RED;
		}
		shapeRenderer.setColor(color);
		shapeRenderer.line(Mouse.getMousePos()[0],Mouse.getMousePos()[1],pointPos[0],pointPos[1]);
	}
	
	public void restart(){
		initGameObjects();
	}
	
	
}

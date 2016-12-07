package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.GameWorld.GameConstants;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class AssetLoader {
	
	private static final int LC_BUTTON_WIDTH = GameConstants.LC_WIDTH_TEXTURE;
	private static final int LC_BUTTON_HEIGHT = GameConstants.LC_HEIGHT_TEXTURE;
	private static final int M_BUTTON_WIDTH = GameConstants.MENU_BUTTON_WIDTH;
	private static final int M_BUTTON_HEIGHT = GameConstants.MENU_BUTTON_HEIGHT;
	
	private AssetLoader(){}	
	
	private static Texture gameObjects, levelCreatorUI, menuUI;
	public static Texture background;
	
	public static TextureRegion restartButtonUp, restartButtonDown, playButtonUp, playButtonDown, 
	stickButtonUp, stickButtonDown, hiddenButtonUp, hiddenButtonDown, pinnedButtonUp, pinnedButtonDown,
	curveButtonUp, curveButtonDown, springButtonUp, springButtonDown, moveButtonDown, moveButtonUp,
	circleButtonUp, circleButtonDown, panButtonDown, panButtonUp, pathButtonUp, pathButtonDown, rectangleButtonUp, rectangleButtonDown, velocityButtonUp,
	velocityButtonDown, forceButtonUp, forceButtonDown ;
	
	public static TextureRegion circle, circlePinned, circleShadow, rectangle, rectanglePinned, 
	rectangleShadow, rectangleShadowLandscape, rectangleShadowPortrait, chain, chainShadow, region;
	
	public static TextureRegion menuPlayButtonUp, menuPlayButtonDown, menuQuitButtonUp, menuQuitButtonDown;
	
	private static final int DIAMETER = 256;
	private static final int PADDING = 4;
	private static final int SHADOW_DIAMETER = 216;
	private static final int SHADOW_SPREAD = 60;
	private static final int PATH_DIAMETER = 40;
	private static final int PATH_SHADOW_DIAMETER = 10;
	
	
	public static void load(){
		
		levelCreatorUI = new Texture(Gdx.files.internal("UI/LevelCreatorUI.png"));
		levelCreatorUI.setFilter(TextureFilter.Linear,TextureFilter.Linear);

		// restart button
		restartButtonUp = new TextureRegion(levelCreatorUI,0,0,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		restartButtonDown = new TextureRegion(levelCreatorUI,0,LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		
		// play button
		playButtonUp = new TextureRegion(levelCreatorUI,LC_BUTTON_WIDTH,0,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		playButtonDown = new TextureRegion(levelCreatorUI,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		
		// stick button
		stickButtonUp = new TextureRegion(levelCreatorUI,2*LC_BUTTON_WIDTH,0,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		stickButtonDown = new TextureRegion(levelCreatorUI,2*LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		
		// stick button
		hiddenButtonUp = new TextureRegion(levelCreatorUI,3*LC_BUTTON_WIDTH,0,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		hiddenButtonDown = new TextureRegion(levelCreatorUI,3*LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		
		// pinned button
		pinnedButtonUp = new TextureRegion(levelCreatorUI,4*LC_BUTTON_WIDTH,0,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		pinnedButtonDown = new TextureRegion(levelCreatorUI,4*LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		
		// curve button
		curveButtonUp = new TextureRegion(levelCreatorUI,5*LC_BUTTON_WIDTH,0,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		curveButtonDown = new TextureRegion(levelCreatorUI,5*LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		
		// spring button
		springButtonUp = new TextureRegion(levelCreatorUI,6*LC_BUTTON_WIDTH,0,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		springButtonDown = new TextureRegion(levelCreatorUI,6*LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		
		// move button
		moveButtonUp = new TextureRegion(levelCreatorUI,7*LC_BUTTON_WIDTH,0,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		moveButtonDown = new TextureRegion(levelCreatorUI,7*LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		
		// circle button
		circleButtonUp = new TextureRegion(levelCreatorUI,8*LC_BUTTON_WIDTH,0,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		circleButtonDown = new TextureRegion(levelCreatorUI,8*LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		
		// pan button
		panButtonUp = new TextureRegion(levelCreatorUI,9*LC_BUTTON_WIDTH,0,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		panButtonDown = new TextureRegion(levelCreatorUI,9*LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
	
		// path button
		pathButtonUp = new TextureRegion(levelCreatorUI,0*LC_BUTTON_WIDTH,2*LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		pathButtonDown = new TextureRegion(levelCreatorUI,0*LC_BUTTON_WIDTH,3*LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		
		// path button
		rectangleButtonUp = new TextureRegion(levelCreatorUI,1*LC_BUTTON_WIDTH,2*LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		rectangleButtonDown = new TextureRegion(levelCreatorUI,1*LC_BUTTON_WIDTH,3*LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		
		// velocity button
		velocityButtonUp = new TextureRegion(levelCreatorUI,2*LC_BUTTON_WIDTH,2*LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		velocityButtonDown = new TextureRegion(levelCreatorUI,2*LC_BUTTON_WIDTH,3*LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		
		// force button
		forceButtonUp = new TextureRegion(levelCreatorUI,3*LC_BUTTON_WIDTH,2*LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		forceButtonDown = new TextureRegion(levelCreatorUI,3*LC_BUTTON_WIDTH,3*LC_BUTTON_HEIGHT,LC_BUTTON_WIDTH,LC_BUTTON_HEIGHT);
		
	
		menuUI = new Texture(Gdx.files.internal("UI/MenuUI.png"));
		menuUI.setFilter(TextureFilter.Nearest,TextureFilter.Nearest);
		
		// play button
		menuPlayButtonUp = new TextureRegion(menuUI,0,0,512 + 30,128+30);
		menuPlayButtonDown = new TextureRegion(menuUI,572,0,512 + 30,128+30);
		
		// quit button
		menuQuitButtonUp = new TextureRegion(menuUI,0,181,512 + 30,128+30);
		menuQuitButtonDown = new TextureRegion(menuUI,572,181,512 + 30,128+30);
		
		// background
		background = new Texture(Gdx.files.internal("UI/background4.png"));
		background.setFilter(TextureFilter.Linear,TextureFilter.Linear);
		
		// game objects texture
		gameObjects = new Texture(Gdx.files.internal("GameObjects/gameObjects.png"));
		gameObjects.setFilter(TextureFilter.Linear,TextureFilter.Linear);
		
		// rectangle
		rectangle = new TextureRegion(gameObjects, PADDING, PADDING, DIAMETER, DIAMETER);
		rectanglePinned = new TextureRegion(gameObjects, 2*PADDING + DIAMETER, PADDING, DIAMETER, DIAMETER);
		rectangleShadow = new TextureRegion(gameObjects, 3*PADDING + 2*DIAMETER, PADDING, 
				SHADOW_DIAMETER + 2*SHADOW_SPREAD, SHADOW_DIAMETER + 2*SHADOW_SPREAD);
		
		// circle
		circle = new TextureRegion(gameObjects, PADDING, PADDING + DIAMETER + PADDING, DIAMETER, DIAMETER);
		circlePinned = new TextureRegion(gameObjects, 2*PADDING + DIAMETER, 2*PADDING + DIAMETER, DIAMETER, DIAMETER);
		circleShadow = new TextureRegion(gameObjects, 3*PADDING + 2*DIAMETER, 2*PADDING + 2*SHADOW_SPREAD + SHADOW_DIAMETER, 
				SHADOW_DIAMETER + 2*SHADOW_SPREAD, SHADOW_DIAMETER + 2*SHADOW_SPREAD);
		
		// chain
		chainShadow = new TextureRegion(gameObjects, PADDING, 3*PADDING + 4*SHADOW_SPREAD + 2*SHADOW_DIAMETER, 
				860, PATH_SHADOW_DIAMETER + 2*SHADOW_SPREAD);
		chain = new TextureRegion(gameObjects, PADDING, chainShadow.getRegionY() + chainShadow.getRegionHeight() + PADDING, 
				848, PATH_DIAMETER);
		
		// region
		region = new TextureRegion(gameObjects,512 + 10 + 60 , 60, 216,216 );
		
		
	}
	
	public static void dispose(){
		levelCreatorUI.dispose();
	}

}

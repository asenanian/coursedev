package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.GameWorld.GameConstants;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class AssetLoader {
	
	private static final int LC_BUTTON_WIDTH = GameConstants.B_WIDTH;
	private static final int LC_BUTTON_HEIGHT = GameConstants.B_HEIGHT;
	private static final int M_BUTTON_WIDTH = GameConstants.MENU_BUTTON_WIDTH;
	private static final int M_BUTTON_HEIGHT = GameConstants.MENU_BUTTON_HEIGHT;
	
	private AssetLoader(){}	
	// TODO add pressed versus non-pressed 
	
	private static Texture levelCreatorUI, menuUI;
	
	public static TextureRegion restartButtonUp, restartButtonDown, playButtonUp, playButtonDown, 
	stickButtonUp, stickButtonDown, hiddenButtonUp, hiddenButtonDown, pinnedButtonUp, pinnedButtonDown,
	curveButtonUp, curveButtonDown, springButtonUp, springButtonDown, moveButtonDown, moveButtonUp,
	circleButtonUp, circleButtonDown;
	
	public static TextureRegion menuPlayButtonUp, menuPlayButtonDown, menuQuitButtonUp, menuQuitButtonDown;
	
	public static void load(){
		
		levelCreatorUI = new Texture(Gdx.files.internal("UI/LevelCreatorUI.png"));
		levelCreatorUI.setFilter(TextureFilter.Nearest,TextureFilter.Nearest);

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
		
		
		menuUI = new Texture(Gdx.files.internal("UI/MenuUI.png"));
		menuUI.setFilter(TextureFilter.Nearest,TextureFilter.Nearest);
		
		// play button
		menuPlayButtonUp = new TextureRegion(menuUI,0,0,512 + 30,128+30);
		menuPlayButtonDown = new TextureRegion(menuUI,572,0,512 + 30,128+30);
		
		// quit button
		menuQuitButtonUp = new TextureRegion(menuUI,0,181,512 + 30,128+30);
		menuQuitButtonDown = new TextureRegion(menuUI,572,181,512 + 30,128+30);
	}
	
	public static void dispose(){
		levelCreatorUI.dispose();
	}

}

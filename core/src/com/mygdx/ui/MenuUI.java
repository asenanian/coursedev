package com.mygdx.ui;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.mygdx.GameWorld.GameConstants;
import com.mygdx.managers.AssetLoader;

public class MenuUI {
	
	public void load(HashMap<String,SimpleButton> menuButtons){
		
		// Play button
		menuButtons.put("PLAY", new SimpleButton(
				(GameConstants.WIDTH / 2) - (GameConstants.MENU_BUTTON_WIDTH/2), 
				(GameConstants.HEIGHT / 2) + (GameConstants.MENU_BUTTON_HEIGHT) - 200, 
				GameConstants.MENU_BUTTON_WIDTH, GameConstants.MENU_BUTTON_HEIGHT, 
				AssetLoader.menuPlayButtonUp,AssetLoader.menuPlayButtonDown)
		);
		
		// Quit button
		menuButtons.put("QUIT", new SimpleButton(
				(GameConstants.WIDTH / 2) - (GameConstants.MENU_BUTTON_WIDTH/2), 
				(GameConstants.HEIGHT / 2) - (GameConstants.MENU_BUTTON_HEIGHT) - 200, 
				GameConstants.MENU_BUTTON_WIDTH, GameConstants.MENU_BUTTON_HEIGHT, 
				AssetLoader.menuQuitButtonUp,AssetLoader.menuQuitButtonDown)
		);
	}
}

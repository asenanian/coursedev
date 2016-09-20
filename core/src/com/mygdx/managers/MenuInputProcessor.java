package com.mygdx.managers;

import java.util.HashMap;

import com.mygdx.GameWorld.GameConstants;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.ui.MenuUI;
import com.mygdx.ui.SimpleButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class MenuInputProcessor implements InputProcessor {
	
	private GameManager world;
	private HashMap<String,SimpleButton> menuButtons = new HashMap<String,SimpleButton>();
	private MenuUI ui = new MenuUI();
	
	public MenuInputProcessor(GameManager world){
		
		this.world = world;		
		ui.load(menuButtons);
	}
	
	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {

		//Vector2 mousePos = mousePos(x,GameConstants.HEIGHT - y);
		y = GameConstants.HEIGHT - y;
		
		if(world.isMenu()){
			for(SimpleButton menuButton : menuButtons.values()){
				if(menuButton.isTouchDown(x,y)) {
					return true; 
				}
			}
		} else{
			return false; //pass event to next inputprocessor
		}
		return true; //other wise, pass true 
	}
	
	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		y = GameConstants.HEIGHT - y;
		
		if(world.isMenu()){
			if ( menuButtons.get("PLAY").isTouchUp(x,y) ){
				world.create();
			} else if ( menuButtons.get("QUIT").isTouchUp(x, y)){
				Gdx.app.exit(); //quit game
			}
		} else {
			return false; //pass event to next inputprocessor
		}
		return true; // otherwise, pass true
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	/*
	 * Getters
	 */
	
	public HashMap<String,SimpleButton> getMenuButtons(){
		return menuButtons;
	}


	
}

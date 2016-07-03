package com.mygdx.managers;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mygdx.gamestates.GameState;
import com.mygdx.gamestates.PlayState;

public class GameStateManager {
	//current game state
	private GameState gameState;
	
	public static final int MENU = 0;
	public static final int PLAY = 1;
	
	public GameStateManager() {
		setState(PLAY);
	}
	
	public void setState(int state) {
		if (gameState != null) gameState.dispose();
		if (state == MENU) {
			//gameState = new MenuState(this);
		}
		if (state == PLAY) {
			gameState = new PlayState(this);
		}
	}
	public void update(float dt, ModelBatch modelBatch) {
		gameState.update(dt, modelBatch);
	}
	public void draw(ModelBatch modelBatch) {
		gameState.draw(modelBatch);
	}
}

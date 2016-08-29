package com.mygdx.managers;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mygdx.gamestates.GameState;
import com.mygdx.gamestates.GameStateSpheres;
import com.mygdx.gamestates.PlayStateSpheres;
import com.mygdx.gamestates.MenuState;
import com.mygdx.gamestates.PlayState;

public class GameStateManagerSpheres {
	//current game state
	private GameStateSpheres gameState;
	
	public static final int MENU = 0;
	public static final int PLAY = 1;
	
	public GameStateManagerSpheres() {
		setState(PLAY);
	}
	
	public void setState(int state) {
		if (gameState != null) gameState.dispose();
//		if (state == MENU) {
//			gameState = new MenuState(this);
//		}
		if (state == PLAY) {
			gameState = new PlayStateSpheres(this);
		}
	}
	public void update(float dt) {
		gameState.update(dt);
	}
	public void draw(ModelBatch modelBatch) {
		gameState.draw(modelBatch);
	}
}


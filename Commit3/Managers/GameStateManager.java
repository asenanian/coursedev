package com.mygdx.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.gamestates.GameState;
import com.mygdx.gamestates.MenuState;
import com.mygdx.gamestates.PlayState;
import com.mygdx.gamestates.PlayStateSpheres;

public class GameStateManager {
	//current game state
	private GameState gameState;
	public static final int MENU = 0;
	public static final int PLAY = 1;
	
	public GameStateManager() {
		setState(MENU);
	}
	
	public void setState(int state) {
		if (gameState != null) gameState.dispose();
		if (state == MENU) {
			gameState = new MenuState(this);
		}
		if (state == PLAY) {
			gameState = new PlayState(this);
		}
	}
	public void update(float dt) {
		gameState.update(dt);
	}
	public void drawSprites(SpriteBatch batch) {
		gameState.drawSprites(batch);
	}
	public void drawShapes(ShapeRenderer shapeRenderer) {
		gameState.drawShapes(shapeRenderer);
	}
	public void dispose() {
		gameState.dispose();
	}
}

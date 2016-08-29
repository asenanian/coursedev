package com.mygdx.gamestates;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.managers.GameStateManager;

public abstract class GameState {
	protected GameStateManager gsm;
	protected GameState(GameStateManager gsm) {
		this.gsm = gsm;
		init();
	}
	public abstract void init();
	public abstract void update(float dt);
	public abstract void drawSprites(SpriteBatch batch);
	public abstract void drawShapes(ShapeRenderer shapeRenderer);
	public abstract void handleInput();
	public abstract void dispose();
}

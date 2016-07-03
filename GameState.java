package com.mygdx.gamestates;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mygdx.managers.GameStateManager;

public abstract class GameState {
	protected GameStateManager gsm;
	protected GameState(GameStateManager gsm) {
		this.gsm = gsm;
		init();
	}
	public abstract void init();
	public abstract void update(float dt, ModelBatch modelBatch);
	public abstract void draw(ModelBatch modelBatch);
	public abstract void handleInput();
	public abstract void dispose();
}

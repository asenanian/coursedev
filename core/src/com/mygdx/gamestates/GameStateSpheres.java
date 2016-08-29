package com.mygdx.gamestates;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mygdx.managers.GameStateManager;
import com.mygdx.managers.GameStateManagerSpheres;

public abstract class GameStateSpheres {
	protected GameStateManagerSpheres gsm;
	protected GameStateSpheres(GameStateManagerSpheres gsm) {
		this.gsm = gsm;
		init();
	}
	public abstract void init();
	public abstract void update(float dt);
	public abstract void draw(ModelBatch modelBatch);
	public abstract void handleInput();
	public abstract void dispose();
}

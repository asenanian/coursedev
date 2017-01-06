package com.mygdx.Entities.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public interface IGameObject extends IGraphObject {

	public void draw(SpriteBatch batcher);
	public void drawShadows(SpriteBatch batcher);
	public void initialize(World world);
	public void update();
	public Body getBody();
	public float getWidth();
	public float getHeight();
}
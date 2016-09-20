package com.mygdx.Entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public interface IGameObject {

	public void draw(ShapeRenderer shapeRenderer);
	public void drawShadows(ShapeRenderer shapeRenderer, SpriteBatch batcher);
	public void initialize(World world);
	public Body getBody();
	public boolean containsPos(float x, float y);
	public boolean isSelected(float x, float y);
	public boolean isSelecting(float x, float y);
	public float getWidth();
	public float getHeight();
}

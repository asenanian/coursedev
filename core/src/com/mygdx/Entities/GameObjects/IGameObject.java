package com.mygdx.Entities.GameObjects;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public interface IGameObject {

	public void draw(SpriteBatch batcher);
	public void drawShadows(SpriteBatch batcher);
	public void initialize(World world);
	public Body getBody();
	public boolean containsPos(float [] pos);
	public float getWidth();
	public float getHeight();
	public abstract Serializable getBean();
}
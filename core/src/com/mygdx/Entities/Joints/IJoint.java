package com.mygdx.Entities.Joints;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

public interface IJoint {
	public void draw(SpriteBatch batcher);
	public void update();
	public void initialize(World world);
	public Serializable getBean();
}

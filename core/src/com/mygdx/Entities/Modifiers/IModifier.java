package com.mygdx.Entities.Modifiers;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IModifier {
	public void initialize();
	public void draw(SpriteBatch batcher);	
	public Serializable getBean();
}

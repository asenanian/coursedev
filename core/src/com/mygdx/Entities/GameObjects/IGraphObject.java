package com.mygdx.Entities.GameObjects;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.Entities.Modifiers.Force;
import com.mygdx.Entities.Modifiers.Velocity;

public interface IGraphObject {
	
	public Vector2 getPosition();
	public boolean containsPos(float [] pos);
	
	public void attachForce(Force force);
	public void detachForce(Force force);
	public void attachBoost(Velocity velocity);
	
	public Serializable getBean();
}

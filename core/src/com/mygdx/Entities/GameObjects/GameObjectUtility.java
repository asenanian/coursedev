package com.mygdx.Entities.GameObjects;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.Entities.Modifiers.Force;
import com.mygdx.Entities.Modifiers.Velocity;

public class GameObjectUtility {
	
	protected Body body;
	protected ArrayList<Force> forces;
	
	public GameObjectUtility(){
		this.forces = new ArrayList<Force>();
	}
	
	public void attachForce(Force force){
		forces.add(force);
	}
	
	public void detachForce(Force force){
		forces.remove(force);
	}
	
	public void attachBoost(Velocity velocity){
		body.applyLinearImpulse(velocity.getVector().cpy().scl(body.getMass()), body.getPosition(), true);
	}
	
	public void update(){
		for (Force force : forces){
			body.applyForceToCenter(force.getVector(), true);
		}
	}
	
	public Vector2 getPosition(){
		return body.getPosition();
	}
	
	public Body getBody(){
		return body;
	}
}

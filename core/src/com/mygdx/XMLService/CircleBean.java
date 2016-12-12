package com.mygdx.XMLService;

import java.io.Serializable;

public class CircleBean implements Serializable {

    private float position[] = null;
    private float radius = 0;
    private float restitution = 0;
    private float friction = 0;
    private float density = 0;
    private boolean pinned = false;

    public CircleBean() {
    }
    
    public float[] getPos(){
    	return position;
    }
    
    public float getRadius(){
    	return radius;
    }
    
    public boolean getPinned(){
    	return pinned;
    }
    
	public float getRestitution() {
		return restitution;
	}

	public float getFriction() {
		return friction;
	}

	public float getDensity() {
		return density;
	}
    
    public void setPos(float position[]){
    	this.position = position;
    }
    
    public void setRadius(float radius){
    	this.radius = radius;
    }
    
    public void setPinned(boolean pinned){
    	this.pinned = pinned;
    }
    
    public void setRestitution(float restitution){
    	this.restitution = restitution;
    }
    
    public void setFriction(float friction){
    	this.friction = friction;
    }
    
    public void setDensity(float density){
    	this.density = density;
    }
    
    @Override
    public String toString(){
    	return "CIRCLE";
    }
   
}

package com.mygdx.XMLService.Beans;

import java.io.Serializable;

public class RectangleBean implements Serializable {

    private float x = 0;
    private float y = 0;
    private float width = 0;
    private float height = 0;
    private float restitution = 0;
    private float friction = 0;
    private float density = 0;
    private boolean pinned = false;

    public RectangleBean() {
    }
    
    public float getX(){
    	return x;
    }
    
    public float getY(){
    	return y;
    }
    
    public float getWidth(){
    	return width;
    }
    
    public float getHeight(){
    	return height;
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
    
    public void setX(float x){
    	this.x = x;
    }
    
    public void setY(float y){
    	this.y = y;
    }
    
    public void setWidth(float width){
    	this.width = width;
    }
    
    public void setHeight(float height){
    	this.height = height;
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
    	return "RECTANGLE";
	}    
   
}

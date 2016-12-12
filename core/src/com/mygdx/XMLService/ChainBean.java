package com.mygdx.XMLService;

import java.io.Serializable;

public class ChainBean implements Serializable {
	
	private float [] vertices = null;
    private float restitution = 0;
    private float friction = 0;
    private float density = 0;

    public ChainBean() {
    }
	
	public float [] getVertices(){
		return vertices;
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
	
	public void setVertices(float [] vertices){
		this.vertices = vertices;
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
    	return "CHAIN";
    }
	
	

}

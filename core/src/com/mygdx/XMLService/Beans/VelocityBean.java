package com.mygdx.XMLService.Beans;

import java.io.Serializable;

public class VelocityBean implements Serializable {

	private float [] vector = null;
	private float [] positionOfObject = null;

	public VelocityBean(){}
	
	public float [] getVector(){
		return vector;
	}
	
	public float [] getPositionOfObject(){
		 return positionOfObject;
	}
	
	public void setVector(float [] vector){
		this.vector = vector;
	}
	
	public void setPositionOfObject(float [] positionOfObject){
		this.positionOfObject = positionOfObject;
	}
	
	@Override
    public String toString(){
    	return "VELOCITY";
	}
}

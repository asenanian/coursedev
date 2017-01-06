package com.mygdx.XMLService.Beans;

import java.io.Serializable;

public class StickBean implements Serializable {
	
	private float length = 0;
	private float [] positionOfFirstObject = null;
	private float [] positionOfSecondObject = null;

	public StickBean(){}
	
	public float getLength(){
		return length;
	}
	
	public float [] getPositionOfFirstObject(){
		 return positionOfFirstObject;
	}
	
	public float [] getPositionOfSecondObject(){
		return positionOfSecondObject;
	}
	
	public void setLength(float length){
		this.length = length;
	}
	
	public void setPositionOfFirstObject(float [] positionOfFirstObject){
		this.positionOfFirstObject = positionOfFirstObject;
	}
	
	public void setPositionOfSecondObject(float [] positionOfSecondObject){
		this.positionOfSecondObject = positionOfSecondObject;
	}
	
	@Override
    public String toString(){
    	return "STICK";
	}
}

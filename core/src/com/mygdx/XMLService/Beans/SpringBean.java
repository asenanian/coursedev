package com.mygdx.XMLService.Beans;

import java.io.Serializable;

public class SpringBean implements Serializable {
	
	private float equilibriumLength = 0;
	private float springConstant = 0;
	private float damping = 0;
	private float [] positionOfFirstObject = null;
	private float [] positionOfSecondObject = null;

	public SpringBean(){}
	
	public float getEquilibriumLength(){
		return equilibriumLength;
	}
	
	public float getSpringConstant(){
		return springConstant;
	}
	
	public float getDamping(){
		return damping;
	}
	
	public float [] getPositionOfFirstObject(){
		 return positionOfFirstObject;
	}
	
	public float [] getPositionOfSecondObject(){
		return positionOfSecondObject;
	}
	
	public void setEquilibriumLength(float equilibriumLength){
		this.equilibriumLength = equilibriumLength;
	}
	
	public void setSpringConstant(float springConstant){
		this.springConstant = springConstant;
	}
	
	public void setDamping(float damping){
		this.damping = damping;
	}
	
	public void setPositionOfFirstObject(float [] positionOfFirstObject){
		this.positionOfFirstObject = positionOfFirstObject;
	}
	
	public void setPositionOfSecondObject(float [] positionOfSecondObject){
		this.positionOfSecondObject = positionOfSecondObject;
	}
	
	@Override
    public String toString(){
    	return "SPRING";
	}
    
}

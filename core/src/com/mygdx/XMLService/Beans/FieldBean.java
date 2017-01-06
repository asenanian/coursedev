package com.mygdx.XMLService.Beans;

import java.io.Serializable;

public class FieldBean implements Serializable {

    private float x = 0;
    private float y = 0;
    private float width = 0;
    private float height = 0;
    private boolean pinned = false;

    public FieldBean() {
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
    
    @Override
    public String toString(){
    	return "FIELD";
    }
   
}

package com.mygdx.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

//TODO swap names of buttons

public class SimpleButton {
	private final float x, y, width, height;
	
	private TextureRegion buttonUp;
	private TextureRegion buttonDown;
	private Rectangle bounds;
	
	private boolean isPressed = false; //On-Release, is false. Only true when mouse is pressed down.
	private boolean isEnabled = false; //for toggle buttons.
	
	public SimpleButton(float x, float y, float width, float height, 
			TextureRegion buttonUp, TextureRegion buttonDown){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.buttonUp = buttonUp;
		this.buttonDown = buttonDown;
		
		bounds = new Rectangle(x, y, width, height);
	}
	
	
	public void release(){
		isEnabled = false;
	}
	
	public boolean getClicked(){
		return isEnabled;
	}
	
	
	public void draw(SpriteBatch batcher){
		if(isPressed || isEnabled){
			batcher.draw(buttonDown, x, y, width, height);
		} else{
			batcher.draw(buttonUp, x, y, width, height);
		}
	}
	
	public boolean isTouchDown(int screenX, int screenY){
		if(bounds.contains(screenX,screenY)){
			isPressed = true;
			return true;
		}
		return false;
	}
	
	public boolean isTouchUp(int screenX, int screenY){
		if(bounds.contains(screenX, screenY) && isPressed){
			isPressed = false;
			isEnabled = !isEnabled; // toggle button
			return true;
		}
		
		isPressed = false;
		return false;
	}
	
	public void action(){
		
	}
}

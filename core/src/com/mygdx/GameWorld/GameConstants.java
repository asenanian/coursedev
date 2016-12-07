package com.mygdx.GameWorld;

import com.badlogic.gdx.Gdx;

public final class GameConstants {
	
	private GameConstants(){}
	
	public static void setConstants(){
		W_WIDTH = 100;
		W_HEIGHT = 60;
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		ASPECT_RATIO = HEIGHT/WIDTH;
		FLOOR = 90;
		CEILING = HEIGHT - 10;
		LEFTWALL = 10;
		RIGHTWALL = WIDTH - 10;
	}
	
	public static final int LC_WIDTH_TEXTURE = 128;
	public static final int LC_HEIGHT_TEXTURE = 80;
	public static final int LC_WIDTH = 96;
	public static final int LC_HEIGHT = 60;
	public static final int LC_PADDING = 5;
	public static final int MENU_BUTTON_WIDTH = 312;
	public static final int MENU_BUTTON_HEIGHT = 96;
	
	public static final float GRAVITY = 10f;
	public static final float SPRING_CONSTANT = 10f;
	public static final float SPRING_WIDTH = .05f;
	public static final float MODIFIER_WIDTH = .1f;
	public static final float MODIFIER_SCL = 3f; 
	
	
	public static int HEIGHT, W_HEIGHT;
	public static int WIDTH, W_WIDTH;
	public static float ASPECT_RATIO;
	public static int FLOOR, CEILING, LEFTWALL, RIGHTWALL;
	
}

package com.mygdx.GameWorld;

import com.badlogic.gdx.Gdx;

public final class GameConstants {
	
	private GameConstants(){}
	
	public static void setConstants(){
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		FLOOR = 90;
		CEILING = HEIGHT - 10;
		LEFTWALL = 10;
		RIGHTWALL = WIDTH - 10;
	}
	
	public static final int B_WIDTH = 128;
	public static final int B_HEIGHT = 80;
	public static final int B_PADDING = 5;
	public static final int MENU_BUTTON_WIDTH = 312;
	public static final int MENU_BUTTON_HEIGHT = 96;
	
	public static final float CIRCLE_RADIUS = 14f;
	public static final float TIME_CONSTANT = 0.001f;
	public static final float GRAVITY = -TIME_CONSTANT*100f;
	public static final float STRING_SEGMENT_LENGTH = 10f;
	public static final float SPRING_CONSTANT = TIME_CONSTANT*50f;
	public static final float SPRING_WIDTH = 5f;
	public static final int EXPENSIVENESS = 10;
	public static final int NUM_CREATE_BUTTONS = 9;
	
	public static int HEIGHT;
	public static int WIDTH;
	public static int FLOOR, CEILING, LEFTWALL, RIGHTWALL;
	
}

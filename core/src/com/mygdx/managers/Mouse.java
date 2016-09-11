package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.mygdx.GameWorld.GameConstants;

//TODO get rid of getters and setters, make fields public.
// consider making this an encapsulated class. 

public final class Mouse {
	
	private static int pointIndexBegin = -1;
	private static int pointIndexEnd = -1;
	private static int pinnedIndex;
	private static int pointHovered;
	public static float[] clickedPos = {0,0};
	private static boolean dragged;
	
	private Mouse(){}
	
	public static void setIndexBegin(int indexBegin){
		pointIndexBegin = indexBegin;
	}
	
	public static void setIndexEnd(int indexEnd){
		pointIndexEnd = indexEnd;
	}
	
	public static void setIndexPinned(int indexPinned){
		pinnedIndex = indexPinned;
	}
	
	public static void setPointHovered(int pointHovered){
		Mouse.pointHovered = pointHovered;
	}
	
	public static void setDragged(boolean isDragging){
		Mouse.dragged = isDragging;
	}
	
	public static int getIndexBegin(){
		return pointIndexBegin;
	}
	
	public static int getIndexEnd(){
		return pointIndexEnd;
	}
	
	public static int getIndexPinned(){
		return pinnedIndex;
	}
	
	public static int getPointHovered(){
		return pointHovered;
	}
	
	public static boolean isDragged(){
		return dragged;
	}
	
	public static float[] getMousePos(){
		return new float[] {Gdx.input.getX(), GameConstants.HEIGHT - Gdx.input.getY()};
	}
	
	

}

package com.mygdx.Actions;

public interface IAction {
	
	public boolean actOnTouchDown(float [] mousePos);
	public boolean actOnTouchUp(float [] mousePos);
	public boolean actOnTouchDragged(float [] mousePos);
}
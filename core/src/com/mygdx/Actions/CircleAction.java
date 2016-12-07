package com.mygdx.Actions;

import com.mygdx.Entities.GameObjects.Circle;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.Renderer.GameRenderer;
import com.mygdx.ui.SimpleButton;

public class CircleAction extends ActionUtility implements IAction  {
	
	private float mouseDown [];
	private SimpleButton pinnedButton;
	private boolean creatingObject;
	
	public CircleAction(GameManager manager, GameRenderer renderer, SimpleButton simpleButton){
		super(manager,renderer);
		mouseDown = null;
		pinnedButton = simpleButton;
		creatingObject = false;
	}
	
	@Override
	public boolean actOnTouchDown(float [] mousePos) {
		if (getObject(mousePos) == null) { // Didn't click on a GameObject
			renderer.buildCircle(mousePos); // notify renderer to begin drawing non-physical circle
			mouseDown = mousePos; 
			creatingObject = true;
		}
		return true;
	}
	
	@Override
	public boolean actOnTouchUp(float [] mousePos) {
		if (getObject(mousePos) == null && creatingObject){
			float radius = distance(mousePos, mouseDown);
			Circle circle = new Circle.Constructor(mouseDown, radius, pinnedButton.getClicked()).Construct();
			manager.addGameObject(circle);
		}
		creatingObject = false;
		renderer.endBuilder();
		return true;
	}

	@Override
	public boolean actOnTouchDragged(float [] mousePos) {
		if (renderer.isBuilding())
			renderer.updateBuilder(mousePos);
		return true;
	}


}

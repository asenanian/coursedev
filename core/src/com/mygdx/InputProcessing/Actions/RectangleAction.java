package com.mygdx.InputProcessing.Actions;

import com.mygdx.Entities.GameObjects.Rectangle;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.Renderer.GameRenderer;
import com.mygdx.ui.SimpleButton;

public class RectangleAction extends ActionUtility implements IAction  {
	
	private float mouseDown [];
	private SimpleButton pinnedButton;
	private boolean creatingObject;
	
	public RectangleAction(GameManager manager, GameRenderer renderer, SimpleButton simpleButton){
		super(manager,renderer);
		mouseDown = null;
		pinnedButton = simpleButton;
		creatingObject = false;
	}

	@Override
	public boolean actOnTouchDown(float [] mousePos) {
		if (getObject(mousePos) == null){ // Didn't click on a GameObject
			renderer.buildRect(mousePos);
			mouseDown = mousePos;
			creatingObject = true;
		} 
		return true;
	}
	
	@Override
	public boolean actOnTouchUp(float [] mousePos) {
		if (getObject(mousePos) == null && creatingObject){ // Didn't release on a GameObject
			
			// processing mouse clicks for params of new rectangle object
			float x = mouseDown[0] > mousePos[0] ? mousePos[0] : mouseDown[0];
			float y = mouseDown[1] > mousePos[1] ? mousePos[1] : mouseDown[1];
			float width = mouseDown[0] > mousePos[0] ? mouseDown[0] - x : mousePos[0] - x;
			float height = mouseDown[1] > mousePos[1] ? mouseDown[1] - y : mousePos[1] - y;
			
			Rectangle rectBody = new Rectangle.Constructor(x, y, width, height, pinnedButton.getClicked()).Construct();
			manager.addGameObject(rectBody);
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

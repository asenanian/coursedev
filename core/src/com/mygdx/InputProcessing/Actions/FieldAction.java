package com.mygdx.InputProcessing.Actions;

import com.mygdx.Entities.GameObjects.Field;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.Renderer.GameRenderer;

public class FieldAction extends ActionUtility implements IAction  {
	
	private float mouseDown [];
	private boolean creatingObject;
	
	public FieldAction(GameManager manager, GameRenderer renderer){
		super(manager,renderer);
		mouseDown = null;
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
			
			Field field = new Field(x, y, width, height);
			manager.addField(field);

		}
		creatingObject = false;
		renderer.endBuilder();

		return true; 				// stop checking for other actions
	}

	@Override
	public boolean actOnTouchDragged(float [] mousePos) {
		if (renderer.isBuilding())
			renderer.updateBuilder(mousePos);
		return true;
	}

}

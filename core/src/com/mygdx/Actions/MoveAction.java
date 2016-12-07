package com.mygdx.Actions;

import com.mygdx.Entities.GameObjects.IGameObject;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.Renderer.GameRenderer;

public class MoveAction extends ActionUtility implements IAction {

	public MoveAction(GameManager manager, GameRenderer renderer) {
		super(manager, renderer);
	}

	@Override
	public boolean actOnTouchDown(float[] mousePos) {
		IGameObject objectOnDown;
		if( (objectOnDown = getObject(mousePos)) != null )
			manager.pinObject(objectOnDown, mousePos);
		return true;
	}

	@Override
	public boolean actOnTouchUp(float[] mousePos) {
		if ( manager.isPinning())
			manager.releaseObject();
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean actOnTouchDragged(float[] mousePos) {
		// move points 
		if( manager.isPinning() )
			manager.updatePinner(mousePos);
		return false;
	}

}

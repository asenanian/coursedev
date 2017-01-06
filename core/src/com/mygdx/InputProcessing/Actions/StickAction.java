package com.mygdx.InputProcessing.Actions;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.Entities.GameObjects.IGameObject;
import com.mygdx.Entities.Joints.Stick;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.Renderer.GameRenderer;

public class StickAction extends ActionUtility implements IAction {
	
	private IGameObject objectOnDown;
	
	public StickAction(GameManager manager, GameRenderer renderer){
		super(manager,renderer);
	}

	@Override
	public boolean actOnTouchDown(float [] mousePos) {
		// save object clicked down on.
		if ( (objectOnDown = getObject(mousePos)) != null){ 
			Vector2 pointPos = objectOnDown.getPosition();
			renderer.buildJoint(new float [] {pointPos.x, pointPos.y} );
		}
		return true;
	}

	@Override
	public boolean actOnTouchUp(float [] mousePos) {
		// __1: not from p1 to p1. __2: p1 is a point. __3: p2 is a point.
		IGameObject objectOnUp = getObject(mousePos);
		if (objectOnDown != null && objectOnUp != null && objectOnDown != objectOnUp ){
			manager.addJoint(new Stick(objectOnDown,objectOnUp));
		}
		renderer.endBuilder();
		return true;
	}

	@Override
	public boolean actOnTouchDragged(float [] mousePos) {
		// __1: not from p1 to p1. __2: p1 is a point. __3: p2 is a point.
		IGameObject objectOnDrag = getObject(mousePos);
		
		if (renderer.isBuilding())
			renderer.updateBuilder(mousePos);
		
		if (objectOnDown != null && objectOnDrag != null && objectOnDown != objectOnDrag ){
			manager.addStick(objectOnDrag, objectOnDown);
			
			Vector2 pointPos = objectOnDrag.getPosition();
			renderer.endBuilder();
			renderer.buildJoint(new float [] {pointPos.x, pointPos.y} );
			objectOnDown = objectOnDrag;
		}
		return true;
	}
}

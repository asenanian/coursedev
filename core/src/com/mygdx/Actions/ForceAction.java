package com.mygdx.Actions;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.Entities.GameObjects.IGameObject;
import com.mygdx.Entities.Modifiers.Field;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.Renderer.GameRenderer;

public class ForceAction extends ActionUtility implements IAction {
	
	private IGameObject objectOnDown;
	private Field fieldOnDown;
	
	public ForceAction(GameManager manager, GameRenderer renderer){
		super(manager,renderer);
		objectOnDown = null;
		fieldOnDown = null;
	}

	@Override
	public boolean actOnTouchDown(float [] mousePos) {
		
		if ( (objectOnDown = getObject(mousePos)) != null ){ // clicked on an object
			Vector2 pointPos = objectOnDown.getBody().getPosition();
			renderer.buildModifier(new float [] {pointPos.x, pointPos.y} );
		} else if ( (fieldOnDown = getField(mousePos)) != null ){ // clicked on a field
			Vector2 regionPos = fieldOnDown.getCenter();
			renderer.buildModifier(new float [] {regionPos.x, regionPos.y} );
		}
		return true;
	}

	@Override
	public boolean actOnTouchUp(float [] mousePos) {
		if (objectOnDown != null){
			manager.addForceToObject(objectOnDown, mousePos);
		} else if (fieldOnDown != null){
			manager.addForceToField(fieldOnDown, mousePos);
		}
		renderer.endBuilder();
		return true;
	}

	@Override
	public boolean actOnTouchDragged(float [] mousePos) {
		if (renderer.isBuilding())
			renderer.updateBuilder(mousePos);
		return true;
	}
	
	protected Field getField(float [] mousePos){
		for(Field field : manager.getFields()){
			if( field.containsPos(mousePos[0], mousePos[1])){
				return field;
			}
		}
		return null;
	}
}

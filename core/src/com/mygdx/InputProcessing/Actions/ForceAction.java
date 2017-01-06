package com.mygdx.InputProcessing.Actions;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.Entities.GameObjects.Field;
import com.mygdx.Entities.GameObjects.IGraphObject;
import com.mygdx.Entities.Modifiers.Force;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.Renderer.GameRenderer;

public class ForceAction extends ActionUtility implements IAction {
	
	private IGraphObject clickableOnDown;
	
	public ForceAction(GameManager manager, GameRenderer renderer){
		super(manager,renderer);
	}
	
	@Override
	public boolean actOnTouchDown(float [] mousePos) {
		if ( (clickableOnDown = getClickable(mousePos)) != null ){
			Vector2 clickablePos = clickableOnDown.getPosition();
			renderer.buildModifier(new float [] {clickablePos.x, clickablePos.y});
		}
		return true;
	}

	@Override
	public boolean actOnTouchUp(float [] mousePos) {
		if ( clickableOnDown != null ){
			manager.addModifier(new Force(clickableOnDown,new Vector2(mousePos[0],mousePos[1])));

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
			if( field.containsPos(mousePos)){
				return field;
			}
		}
		return null;
	}
}

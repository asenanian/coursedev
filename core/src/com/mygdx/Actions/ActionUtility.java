package com.mygdx.Actions;

import com.mygdx.Entities.GameObjects.IGameObject;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.Renderer.GameRenderer;

public class ActionUtility {

	protected GameManager manager;
	protected GameRenderer renderer;
	
	public ActionUtility(GameManager manager, GameRenderer renderer){
		this.manager = manager;
		this.renderer = renderer;
	}
	
	protected float distance(float [] pos1, float [] pos2){
		float distance = (pos2[0] - pos1[0])*(pos2[0] - pos1[0]) + (pos2[1] - pos1[1])*(pos2[1] - pos1[1]);
		return (float)(Math.sqrt(distance));
	}	
	
	protected IGameObject getObject(float [] mousePos){
		for(IGameObject object : manager.getPoints()){
			if (object.containsPos(mousePos))
				return object;
		}
		return null;
	}

}

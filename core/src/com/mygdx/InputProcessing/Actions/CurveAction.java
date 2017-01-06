package com.mygdx.InputProcessing.Actions;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.Entities.GameObjects.Chain;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.Renderer.GameRenderer;

public class CurveAction extends ActionUtility implements IAction  {
	
	private ArrayList<Vector2> vertices = new ArrayList<Vector2>();
	private float mouseDown [];
	private boolean creatingObject;
	
	public CurveAction(GameManager manager, GameRenderer renderer){
		super(manager,renderer);
		mouseDown = null;
		creatingObject = false;
	}
	
	@Override
	public boolean actOnTouchDown(float [] mousePos) {
		if (getObject(mousePos) == null){ // Didn't click on a GameObject
			renderer.buildPath(mousePos, vertices);
			mouseDown = mousePos;
			creatingObject = true;
		}
		return true;
	}
	
	@Override
	public boolean actOnTouchUp(float [] mousePos) {
		if (getObject(mousePos) == null && creatingObject){ // Didn't release on a GameObject
			
			// convert dynamic structure to params needed for Chain construction.
			Vector2 vert[] = toArray(vertices);
			Chain chainBody = new Chain.Constructor(vert).Construct();
			manager.addGameObject(chainBody);
			
			vertices.clear();
			creatingObject = false;
		}
		renderer.endBuilder();
		return true;
	}

	@Override
	public boolean actOnTouchDragged(float [] mousePos) {
		// if not dragging over a point and enough distance has been made => add a joint to the curve
		if (getObject(mousePos) == null && creatingObject && (distance(mouseDown,mousePos) > .5)){
			vertices.add(new Vector2(mousePos[0],mousePos[1]));
			renderer.updateBuilder(mousePos);
			mouseDown = mousePos;
		}
		return true;
	}
	
	private Vector2[] toArray(ArrayList<Vector2> arrayList){
		Vector2 vert[] = new Vector2[arrayList.size()];
		for(int i = 0; i < arrayList.size(); i++){
			vert[i] = arrayList.get(i);
		}
		return vert;
	}

}

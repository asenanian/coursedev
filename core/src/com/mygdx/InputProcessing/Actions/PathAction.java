package com.mygdx.InputProcessing.Actions;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.Entities.GameObjects.Chain;
import com.mygdx.Entities.GameObjects.PolyBody;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.Renderer.GameRenderer;
import com.mygdx.ui.SimpleButton;

public class PathAction extends ActionUtility implements IAction  {

	private ArrayList<Vector2> vertices;
	private float mouseDown [];
	private SimpleButton pinnedButton;
	
	public PathAction(GameManager manager, GameRenderer renderer, SimpleButton simpleButton){
		super(manager,renderer);
		mouseDown = null;
		vertices = new ArrayList<Vector2>();
		this.pinnedButton = simpleButton;
	}
	
	@Override
	public boolean actOnTouchDown(float [] mousePos) {
		
		if ( getObject(mousePos) == null ){ // Didn't click on a GameObject
			// notifies renderer to start rendering path builder
			if (vertices.size() == 0){
				renderer.buildPath(mousePos, vertices);
				mouseDown = mousePos;
			} 
			
			// escape condition: create polygon if created a loop.
			if ( isALoop(vertices, mousePos) ){
				vertices.add(vertices.get(0).cpy());
				Vector2 vert[] = toArray(vertices);
				PolyBody polygonBody = new PolyBody.Constructor(vert, pinnedButton.getClicked()).Construct();
				manager.addGameObject(polygonBody);
				vertices.clear();
				renderer.endBuilder();
				return true;
				
			// escape condition: create chain if clicked near previous point
			} else if ( distance(mousePos, mouseDown ) < 1 && vertices.size() > 1){
				Vector2 vert[] = toArray(vertices);
				Chain chainBody = new Chain.Constructor(vert).Construct();
				manager.addGameObject(chainBody);
				vertices.clear();
				renderer.endBuilder();
				return true;
				
			// else just add a point to the chain.
			} else {
				vertices.add(new Vector2(mousePos[0],mousePos[1]));
				renderer.updateBuilder(mousePos);
				mouseDown = mousePos;
				return true;
			}
		}
		return true;
	}
	
	@Override
	public boolean actOnTouchUp(float [] mousePos) {
		return true;
	}

	@Override
	public boolean actOnTouchDragged(float [] mousePos) {
		return true;
	}
	
	private boolean isALoop(ArrayList<Vector2> vertices, float [] newPoint){
		return (vertices.size() > 2 && distance(new float [] {vertices.get(0).x, vertices.get(0).y}, newPoint) < 1 );
	}
	
	private Vector2[] toArray(ArrayList<Vector2> arrayList){
		Vector2 vert[] = new Vector2[arrayList.size()];
		for(int i = 0; i < arrayList.size(); i++){
			vert[i] = arrayList.get(i);
		}
		return vert;
	}

}

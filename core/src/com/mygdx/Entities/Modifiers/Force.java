package com.mygdx.Entities.Modifiers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.Entities.GameObjects.IGameObject;
import com.mygdx.GameWorld.GameConstants;

public class Force implements IModifier {
	private Vector2 vector;
	private IGameObject gameObject;
	
	public Force(IGameObject gameObject, Vector2 beginPos, Vector2 endPos){
		this.vector = new Vector2(endPos.x - beginPos.x, endPos.y - beginPos.y);
		this.gameObject = gameObject;
	}
	
	@Override
	public void initialize(){
		//
	}
	
	@Override
	public void update(){
		if (gameObject == null) return;
		gameObject.getBody().applyForceToCenter(vector.cpy().scl(GameConstants.MODIFIER_SCL), true);
	}
	
	@Override
	public void draw(ShapeRenderer shapeRenderer){
		
		Vector2 pos1 = gameObject.getBody().getPosition().cpy();
		Vector2 pos2 = new Vector2(pos1.cpy().add(vector));
		Vector2 triPoint = vector.cpy().setLength(GameConstants.MODIFIER_WIDTH*4);
		
		shapeRenderer.set(ShapeType.Filled);
		shapeRenderer.setColor(new Color(0,0,0,0.1f));
		shapeRenderer.rectLine(pos1.x , pos1.y, pos2.x, pos2.y, GameConstants.MODIFIER_WIDTH);
		shapeRenderer.triangle(
				pos2.x + triPoint.cpy().rotate90(1).x, 
				pos2.y + triPoint.cpy().rotate90(1).y, 
				pos2.x + triPoint.cpy().rotate90(-1).x, 
				pos2.y + triPoint.cpy().rotate90(-1).y, 
				pos2.x + triPoint.x, 
				pos2.y + triPoint.y);
	}

	@Override
	public void setGameObject(IGameObject gameObject) {
		this.gameObject = gameObject;		
	}

}

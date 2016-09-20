package com.mygdx.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.GameWorld.GameConstants;

public class Force implements IModifier{
	private int index;
	private Vector2 vector;
	
	public Force(int index, Vector2 beginPos, Vector2 endPos){
		this.index = index;
		this.vector = new Vector2(endPos.x - beginPos.x, endPos.y - beginPos.y);
	}
	
	@Override
	public boolean isLarge(ArrayList<IGameObject> points){
		return true;
	}
	
	@Override
	public void initialize(ArrayList<IGameObject> points){
		points.get(index).getBody().applyForce(vector.cpy().scl(10*GameConstants.MODIFIER_SCL), points.get(index).getBody().getPosition(), true);
	}
	
	@Override
	public void update(ArrayList<IGameObject> points){
		points.get(index).getBody().applyForceToCenter(vector.cpy().scl(GameConstants.MODIFIER_SCL), true);
	}
	
	@Override
	public void draw(ArrayList<IGameObject> points, ShapeRenderer shapeRenderer){
		
		Vector2 pos1 = points.get(index).getBody().getPosition().cpy();
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

}

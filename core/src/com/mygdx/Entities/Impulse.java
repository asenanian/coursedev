package com.mygdx.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.GameWorld.GameConstants;

public class Impulse implements IModifier{
	private int index;
	private Vector2 vector;
	
	public Impulse(int index, Vector2 beginPos, Vector2 endPos){
		this.index = index;
		this.vector = new Vector2(endPos.x - beginPos.x, endPos.y - beginPos.y);
	}
	
	public boolean isLarge(ArrayList<IGameObject> points){
		return points.get(index).getBody().getLinearVelocity().len() > 1f;
	}
	
	@Override
	public void initialize(ArrayList<IGameObject> points){
		points.get(index).getBody().applyLinearImpulse(vector.cpy().scl(points.get(index).getBody().getMass()*
				GameConstants.MODIFIER_SCL), points.get(index).getBody().getPosition(), true);
	}

	@Override
	public void update(ArrayList<IGameObject> points){
		vector = points.get(index).getBody().getLinearVelocity().cpy().scl(points.get(index).getBody().getMass()/GameConstants.MODIFIER_SCL);	
	}
	
	@Override
	public void draw(ArrayList<IGameObject> points, ShapeRenderer shapeRenderer){
		
		Vector2 pos1 = points.get(index).getBody().getPosition().cpy();
		Vector2 pos2 = new Vector2(pos1.cpy().add(vector));
		Vector2 triPoint = vector.cpy().setLength(GameConstants.MODIFIER_WIDTH*2);
		
		shapeRenderer.setColor(Color.WHITE);
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

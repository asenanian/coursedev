package com.mygdx.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.GameWorld.GameConstants;

public class Spring implements IJoint{
	private int p1Index;
	private int p2Index;
	private float equilibriumLength;
	private float springConstant;
	
	public Spring(ArrayList<IGameObject> points, int p1Index, int p2Index, float springConstant){
		this.equilibriumLength = points.get(p1Index).getBody().getPosition().cpy().sub(points.get(p2Index).getBody().getPosition()).len();
		this.p1Index = p1Index;
		this.p2Index = p2Index;
		this.springConstant = springConstant;
	}
	
	public void update(ArrayList<IGameObject> points){
		Body body1 = points.get(p1Index).getBody();
		Body body2 = points.get(p2Index).getBody();
		
		Vector2 distance = body1.getPosition().cpy().sub(body2.getPosition());		
		float forceMag = springConstant*(distance.len() - equilibriumLength);
		Vector2 force = distance.scl(forceMag).scl(-1);
		
		
		points.get(p1Index).getBody().applyForceToCenter(force,  true);
		points.get(p2Index).getBody().applyForceToCenter(force.scl(-1), true);
	}
	
	public void draw(ArrayList<IGameObject> points, ShapeRenderer shapeRenderer) {
		
		Vector2 pos1 = points.get(p1Index).getBody().getPosition();
		Vector2 pos2 = points.get(p2Index).getBody().getPosition();
		
		shapeRenderer.set(ShapeType.Filled);		
		shapeRenderer.rectLine(pos1.x , pos1.y, pos2.x, pos2.y, GameConstants.SPRING_WIDTH);

	}
}

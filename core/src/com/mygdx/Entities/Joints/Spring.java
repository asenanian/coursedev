package com.mygdx.Entities.Joints;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.Entities.GameObjects.IGameObject;
import com.mygdx.GameWorld.GameConstants;

public class Spring implements IJoint{

	private IGameObject point1;
	private IGameObject point2;
	private float equilibriumLength;
	private float springConstant;
	private float damping = springConstant*.5f;
	
	public Spring(IGameObject point1, IGameObject point2, float springConstant){
		this.equilibriumLength = point1.getBody().getPosition().cpy().sub(point2.getBody().getPosition()).len();
		this.point1 = point1;
		this.point2 = point2;
		this.springConstant = springConstant;
	}
	
	
	public void update(ArrayList<IGameObject> points){
		Body body1 = point1.getBody();
		Body body2 = point2.getBody();
		
		Vector2 distance = body1.getPosition().cpy().sub(body2.getPosition());		
		float forceMag = springConstant*(distance.len() - equilibriumLength);
		float velMag = body1.getLinearVelocity().len() + body2.getLinearVelocity().len();
		forceMag = forceMag + 2*damping*velMag;
		Vector2 force = distance.scl(forceMag).scl(-1);
		
		
		point1.getBody().applyForceToCenter(force,  true);
		point2.getBody().applyForceToCenter(force.scl(-1), true);
	}
	
	public void draw(ArrayList<IGameObject> points, ShapeRenderer shapeRenderer) {
		
		Vector2 pos1 = point1.getBody().getPosition();
		Vector2 pos2 = point2.getBody().getPosition();
		
		shapeRenderer.set(ShapeType.Filled);		
		shapeRenderer.rectLine(pos1.x , pos1.y, pos2.x, pos2.y, GameConstants.SPRING_WIDTH);

	}
}

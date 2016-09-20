package com.mygdx.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.mygdx.GameWorld.GameConstants;
import com.mygdx.GameWorld.GameManager;

public class Stick implements IJoint{
	private int p1Index;
	private int p2Index;
	private float length;
	
	private DistanceJointDef defJoint;
	private DistanceJoint joint;
	
	public Stick(ArrayList<IGameObject> points, int p1Index, int p2Index){
		this.length = points.get(p1Index).getBody().getPosition().cpy().sub(points.get(p2Index).getBody().getPosition()).len();
		this.p1Index = p1Index;
		this.p2Index = p2Index;
	}
	
	public void initialize(GameManager manager){
		Body body1 = manager.getPoints().get(p1Index).getBody();
		Body body2 = manager.getPoints().get(p2Index).getBody();
		
		defJoint = new DistanceJointDef();
		defJoint.length = length;
		defJoint.initialize(body1, body2, body1.getPosition(), body2.getPosition());
		
		joint = (DistanceJoint) manager.getWorld().createJoint(defJoint);
	}

	@Override
	public void draw(ArrayList<IGameObject> points, ShapeRenderer shapeRenderer) {
		Vector2 pos1 = points.get(p1Index).getBody().getPosition();
		Vector2 pos2 = points.get(p2Index).getBody().getPosition();
		
		shapeRenderer.set(ShapeType.Filled);		
		shapeRenderer.rectLine(pos1.x , pos1.y, pos2.x, pos2.y, GameConstants.SPRING_WIDTH);
	}

}

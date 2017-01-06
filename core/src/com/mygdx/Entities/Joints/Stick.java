package com.mygdx.Entities.Joints;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.mygdx.Entities.GameObjects.IGameObject;
import com.mygdx.GameWorld.GameConstants;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.InputProcessing.AssetLoader;
import com.mygdx.XMLService.Beans.StickBean;

public class Stick implements IJoint{

	private float length;
	
	private IGameObject point1, point2;
	
	private DistanceJointDef defJoint;
	
	public Stick(IGameObject point1, IGameObject point2){
		this.length = point1.getPosition().cpy().sub(point2.getPosition()).len();
		this.point1 = point1;
		this.point2 = point2;
	}
	
	// bean constructor
	public Stick(StickBean stickBean, GameManager manager){
		this.length = stickBean.getLength();
		
		for(IGameObject gameObject : manager.getPoints()){
			if (gameObject.containsPos(stickBean.getPositionOfFirstObject())){
				this.point1 = gameObject;
				break;
			}
		}
		for(IGameObject gameObject : manager.getPoints()){
			if (gameObject.containsPos(stickBean.getPositionOfSecondObject())){
				this.point2 = gameObject;
				break;
			}
		}
	}
	
	public void initialize(World world){
		Body body1 = point1.getBody();
		Body body2 = point2.getBody();
		
		defJoint = new DistanceJointDef();
		defJoint.length = length;
		defJoint.initialize(body1, body2, body1.getPosition(), body2.getPosition());
		
		world.createJoint(defJoint);
	}

	@Override
	public void draw(SpriteBatch batcher) {
		Vector2 pos1 = point1.getPosition();
		Vector2 pos2 = point2.getPosition();
		
		Sprite sprite = new Sprite(AssetLoader.chain);
		sprite.setOrigin(0, 0);
		
		Vector2 diff = pos1.cpy().sub(pos2);
		
		sprite.setPosition(pos2.x, pos2.y - GameConstants.SPRING_WIDTH);
		sprite.setSize(diff.len(), GameConstants.SPRING_WIDTH*2);

		sprite.setRotation(diff.angle());
		sprite.draw(batcher);
	}

	@Override
	public void update() {
		//
	}

	@Override
	public Serializable getBean() {
		StickBean stickBean = new StickBean();
		stickBean.setLength(length);
		stickBean.setPositionOfFirstObject(new float [] {point1.getPosition().x,
				point1.getPosition().y});
		stickBean.setPositionOfSecondObject(new float [] {point2.getPosition().x,
					point2.getPosition().y});
		return stickBean;
	}

}

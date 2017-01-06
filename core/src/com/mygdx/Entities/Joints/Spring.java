package com.mygdx.Entities.Joints;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.Entities.GameObjects.IGameObject;
import com.mygdx.GameWorld.GameConstants;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.InputProcessing.AssetLoader;
import com.mygdx.XMLService.Beans.SpringBean;

public class Spring implements IJoint{

	private IGameObject point1;
	private IGameObject point2;
	private float equilibriumLength;
	private float springConstant;
	private float damping = springConstant*.5f;
	
	public Spring(IGameObject point1, IGameObject point2, float springConstant){
		this.equilibriumLength = point1.getPosition().cpy().sub(point2.getPosition()).len();
		this.point1 = point1;
		this.point2 = point2;
		this.springConstant = springConstant;
	}
	
	public Spring(SpringBean springBean, GameManager manager){
		
		this.equilibriumLength = springBean.getEquilibriumLength();
		this.damping = springBean.getDamping();
		this.springConstant = springBean.getSpringConstant();
		
		for(IGameObject gameObject : manager.getPoints()){
			if (gameObject.containsPos(springBean.getPositionOfFirstObject())){
				this.point1 = gameObject;
				break;
			}
		}
		for(IGameObject gameObject : manager.getPoints()){
			if (gameObject.containsPos(springBean.getPositionOfSecondObject())){
				this.point2 = gameObject;
				break;
			}
		}
	}
	
	
	public void update(){
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
	public Serializable getBean() {
		SpringBean springBean = new SpringBean();
		
		springBean.setEquilibriumLength(equilibriumLength);
		springBean.setDamping(damping);
		springBean.setSpringConstant(springConstant);
		springBean.setPositionOfFirstObject(new float [] {point1.getPosition().x,
			point1.getPosition().y});
		springBean.setPositionOfSecondObject(new float [] {point2.getPosition().x,
				point2.getPosition().y});
		
		return springBean;
	}

	@Override
	public void initialize(World world) {
		// does nothing.
	}
}

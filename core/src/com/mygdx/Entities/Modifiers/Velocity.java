package com.mygdx.Entities.Modifiers;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.GameWorld.GameConstants;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.InputProcessing.AssetLoader;
import com.mygdx.XMLService.Beans.VelocityBean;
import com.mygdx.Entities.GameObjects.*;

public class Velocity implements IModifier{
	private Vector2 vector;
	private Vector2 drawnVector;
	private IGraphObject graphObject;
	private Sprite sprite;
	
	public Velocity(IGraphObject graphObject, Vector2 endPos){
		this.drawnVector = new Vector2(endPos.x - graphObject.getPosition().x, endPos.y - graphObject.getPosition().y);
		this.vector = drawnVector.cpy().scl(GameConstants.MODIFIER_SCL);
		this.graphObject = graphObject;
		sprite = new Sprite(AssetLoader.chain);
	}
	
	public Velocity(VelocityBean velocityBean, GameManager manager){
		this.drawnVector = new Vector2(velocityBean.getVector()[0],velocityBean.getVector()[1]);
		this.vector = drawnVector.cpy().scl(GameConstants.MODIFIER_SCL);
		this.sprite = new Sprite(AssetLoader.chain);
		
		for(IGameObject graphObject : manager.getPoints()){
			if (graphObject.containsPos(velocityBean.getPositionOfObject())){
				this.graphObject = graphObject;
				return;
			}
		}
		
		for(IGraphObject graphObject : manager.getFields()){
			if (graphObject.containsPos(velocityBean.getPositionOfObject())){
				this.graphObject = graphObject;
				return;
			}
		}
		
	}
	
	@Override
	public void initialize(){
		sprite.setOrigin(0, 0);
		graphObject.attachBoost(this);
	}
	
	@Override
	public void draw(SpriteBatch batcher){
		Vector2 pos1 = graphObject.getPosition();

		Vector2 pos2 = new Vector2(pos1.cpy().add(drawnVector));
		Vector2 triPoint = drawnVector.cpy().setLength(GameConstants.MODIFIER_WIDTH*4);		
		
		Vector2 diff = pos1.cpy().sub(pos2);
		
		sprite.setPosition(pos2.x, pos2.y - GameConstants.MODIFIER_WIDTH);
		sprite.setSize(diff.len(), GameConstants.MODIFIER_WIDTH*2);

		sprite.setRotation(diff.angle());
		sprite.draw(batcher);
		/*
		shapeRenderer.triangle(
				pos2.x + triPoint.cpy().rotate90(1).x, 
				pos2.y + triPoint.cpy().rotate90(1).y, 
				pos2.x + triPoint.cpy().rotate90(-1).x, 
				pos2.y + triPoint.cpy().rotate90(-1).y, 
				pos2.x + triPoint.x, 
				pos2.y + triPoint.y);*/
		
	}

	public Vector2 getVector() {
		return vector;
	}
	
	@Override
	public Serializable getBean() {
		VelocityBean velocityBean = new VelocityBean();
		
		velocityBean.setPositionOfObject(new float [] {graphObject.getPosition().x,graphObject.getPosition().y});
		velocityBean.setVector(new float [] {drawnVector.x,drawnVector.y});
		
		return velocityBean;
	}

}

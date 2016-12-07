package com.mygdx.Entities.Modifiers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.GameWorld.GameConstants;
import com.mygdx.Entities.GameObjects.*;

public class Velocity implements IModifier{
	private Vector2 vector;
	private IGameObject gameObject;
	
	public Velocity(IGameObject gameObject, Vector2 beginPos, Vector2 endPos){
		this.vector = new Vector2(endPos.x - beginPos.x, endPos.y - beginPos.y);
		this.gameObject = gameObject;
	}
	
	@Override
	public void initialize(){
		gameObject.getBody().applyLinearImpulse(vector.cpy().scl(gameObject.getBody().getMass()*
				GameConstants.MODIFIER_SCL), gameObject.getBody().getPosition(), true);
	}

	@Override
	public void update(){
		// for draw
		vector = gameObject.getBody().getLinearVelocity().cpy().scl(gameObject.getBody().getMass()/GameConstants.MODIFIER_SCL);	
	}
	
	@Override
	public void draw(ShapeRenderer shapeRenderer){
		
		Vector2 pos1 = gameObject.getBody().getPosition().cpy();
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

	@Override
	public void setGameObject(IGameObject gameObject) {
		this.gameObject = gameObject;		
	}

}

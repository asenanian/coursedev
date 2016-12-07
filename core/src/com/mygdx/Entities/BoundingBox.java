package com.mygdx.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.Entities.GameObjects.IGameObject;

public class BoundingBox {
	private int index;
	private final float BB_CORNER_DIM = 0.08f;
	
	public BoundingBox(int index){
		this.index = index;
	}
	
	public void draw(ShapeRenderer shapeRenderer, ArrayList<IGameObject> points){
		IGameObject object = points.get(index);
		float width = object.getWidth() + 0.1f;
		float height = object.getHeight() + 0.1f;
		float angle = (float)(Math.toDegrees(object.getBody().getAngle()));
		Vector2 pos = object.getBody().getPosition();
		shapeRenderer.set(ShapeType.Line);
		shapeRenderer.setColor(Color.GRAY);
		shapeRenderer.rect(
				(pos.x - width), 
				(pos.y - height), 
				width, 
				height, 
				width*2,
				height*2,
				1f,1f,angle);
		
		Vector2 nPos1 = new Vector2(width, height).rotate(angle);
		Vector2 nPos2 = new Vector2(-width, height).rotate(angle);

		this.draw(shapeRenderer,
				pos.x + nPos1.x,
				pos.y + nPos1.y, 
				angle);
		this.draw(shapeRenderer, 
				pos.x + nPos2.x,
				pos.y + nPos2.y,
				angle);
		this.draw(shapeRenderer, 
				pos.x - nPos2.x,
				pos.y - nPos2.y, 
				angle);
		this.draw(shapeRenderer, 
				pos.x - nPos1.x,
				pos.y - nPos1.y, 
				angle);
	}
	
	public void draw(ShapeRenderer shapeRenderer, float x, float y, float angle){
		shapeRenderer.rect(
				(x - BB_CORNER_DIM), 
				(y - BB_CORNER_DIM), 
				BB_CORNER_DIM, 
				BB_CORNER_DIM, 
				BB_CORNER_DIM*2,
				BB_CORNER_DIM*2,
				1f,1f,angle);	
	}

}

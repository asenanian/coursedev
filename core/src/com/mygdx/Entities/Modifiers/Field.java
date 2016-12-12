package com.mygdx.Entities.Modifiers;

import java.io.Serializable;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.Entities.GameObjects.IGameObject;
import com.mygdx.XMLService.FieldBean;
import com.mygdx.managers.AssetLoader;

public class Field {
	private IModifier modifier;
	
	private float x,y;
	private float width, height;
	
	public Field(float x, float y, float width, float height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void update(ArrayList<IGameObject> points) {
		if (modifier == null) return;
		for (IGameObject p : points){
			if (this.containsPos(p)){
				modifier.setGameObject(p);
				if (modifier instanceof Force){
					modifier.update();
				} else {
					
				}
				modifier.setGameObject(null);
			} 
		}
	}
	
	public boolean containsPos(IGameObject point) {
		Vector2 pos = point.getBody().getPosition();
		return ( (pos.x >  this.x) && (pos.x < this.x + width) ) &&
				( (pos.y > this.y) && (pos.y < this.y + height)  );
	}
	
	public boolean containsPos(float x, float y){
		return ( (x >  this.x) && (x < this.x + width) ) &&
				( (y > this.y) && (y < this.y + height)  );
	}
	
	public void setModifier(IModifier modifier){
		this.modifier = modifier;
	}
	
	public Vector2 getCenter(){
		return new Vector2(x + .5f*width,y + .5f*height);
	}
	
	public void draw(SpriteBatch batcher){
		batcher.draw(AssetLoader.region, x, y, width, height);
	}
	
	public Serializable getBean(){
		
		FieldBean fieldBean = new FieldBean();
		
		fieldBean.setX(x);
		fieldBean.setY(y);
		fieldBean.setWidth(width);
		fieldBean.setHeight(height);
		
		return fieldBean;
	}
}

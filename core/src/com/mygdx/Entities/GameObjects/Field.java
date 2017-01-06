package com.mygdx.Entities.GameObjects;

import java.io.Serializable;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.Entities.GameObjects.IGraphObject;
import com.mygdx.Entities.Modifiers.Force;
import com.mygdx.Entities.Modifiers.IModifier;
import com.mygdx.Entities.Modifiers.Velocity;
import com.mygdx.InputProcessing.AssetLoader;
import com.mygdx.Entities.GameObjects.IGameObject;
import com.mygdx.XMLService.Beans.FieldBean;

public class Field implements IGraphObject {
	private ArrayList<IGameObject> objectsInField;
	
	private IModifier modifier;
	private boolean force;
	private float x,y;
	private float width, height;
	
	public Field(float x, float y, float width, float height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.modifier = null;
		this.force = false;
		this.objectsInField = new ArrayList<IGameObject>();
	}
	
	public Field(FieldBean fieldBean){
		this.x = fieldBean.getX();
		this.y = fieldBean.getY();
		this.width = fieldBean.getWidth();
		this.height = fieldBean.getHeight();
		this.modifier = null;
		this.force = false;
		this.objectsInField = new ArrayList<IGameObject>();
	}
	
	public void update(ArrayList<IGameObject> points) {
		if (modifier == null)
			return;
		for (IGameObject p : points){
			if (this.containsPos(p)){
				if ( !objectsInField.contains(p) ) { // first time inside
					if(force)
						p.attachForce((Force)modifier);
					else
						p.attachBoost((Velocity)modifier);
					objectsInField.add(p);
				}
			} else if ( objectsInField.contains(p) ) { // not inside, but considered inside
				if(force) p.detachForce((Force)modifier);
				objectsInField.remove(p);
			}
		}
	}
	
	public ArrayList<IGameObject> getObjectsInField(){
		return objectsInField;
	}
	
	public boolean containsPos(float [] pos){
		return ( (pos[0] >  this.x) && (pos[0] < this.x + width) ) &&
				( (pos[1] > this.y) && (pos[1] < this.y + height)  );
	}
	
	public boolean containsPos(IGameObject object){
		float [] pos = new float [] {object.getPosition().x,object.getPosition().y};
		return ( (pos[0] >  this.x) && (pos[0] < this.x + width) ) &&
				( (pos[1] > this.y) && (pos[1] < this.y + height)  );
	}
	
	@Override
	public Vector2 getPosition(){
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

	@Override
	public void attachForce(Force force) {
		this.modifier = force;		
		this.force = true;
	}

	@Override
	public void attachBoost(Velocity velocity) {
		this.modifier = velocity;		
		this.force = false;
	}

	// never used
	@Override
	public void detachForce(Force force) {		
		if(this.force) this.modifier = null;
	}
}

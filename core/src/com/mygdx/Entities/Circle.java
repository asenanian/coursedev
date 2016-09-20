package com.mygdx.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import com.mygdx.managers.AssetLoader;

public class Circle implements IGameObject{
	private final float pos[];
	private final boolean pinned;
	private final float radius;
	private final float restitution;
	private final float friction;
	private final float density;
	private final Color color;
	
	private Body circleBody;
	private Fixture fixture;
	
	private boolean isPressed = false;
	private boolean isSelected = false;
	
	public static class Constructor {
		// required params
		private float pos[];
		private final boolean pinned;
		private final float radius;
		
		// option params
		private float restitution = 0.6f;
		private float friction = 0.1f;
		private float density = 1.0f;
		
		public Constructor(float [] pos, float radius, boolean pinned){
			this.pos = pos;
			this.radius = radius;
			this.pinned = pinned;
		}
		
		public Constructor restitution(float val)
		{ this.restitution = val;		return this; }
		
		public Constructor friction(float val)
		{ this.friction = val;		return this; }
		
		public Constructor density(float val)
		{ this.density = val;		return this; }
		
		public Circle Construct(){
			return new Circle(this);
		}
	}
	
	public Circle(Constructor constructor){
		pos = constructor.pos;
		pinned = constructor.pinned;
		radius = constructor.radius > 0.5 ? constructor.radius : 0.25f;
		restitution = constructor.restitution;
		friction = constructor.friction;
		density = constructor.density;
		color = pinned ? Color.BLACK : Color.MAROON;
	}
	
	@Override
	public void initialize(World world){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = pinned ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(pos[0],pos[1]);
		
		circleBody = world.createBody(bodyDef);
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		
		fixture = circleBody.createFixture(fixtureDef);
		circle.dispose();

	}
	
	@Override
	public void draw(ShapeRenderer shapeRenderer){
		shapeRenderer.set(ShapeType.Filled);
		
		shapeRenderer.setColor(Color.MAROON);
		shapeRenderer.circle(circleBody.getPosition().x, circleBody.getPosition().y, this.radius,42);
		
		shapeRenderer.setColor(this.color.cpy().mul(0.9f));
		shapeRenderer.circle(circleBody.getPosition().x, circleBody.getPosition().y, this.radius * .8f,42);
		
	}
	
	@Override
	public void drawShadows(ShapeRenderer shapeRenderer, SpriteBatch batcher){
		batcher.draw(AssetLoader.circleShadow, circleBody.getPosition().x - (radius/64)*(64 + 24.2f) , circleBody.getPosition().y - (radius/64)*(64+27.5f)  ,
				(radius/64)*(128+39.f), (radius/64)*(128 + 40));
	}
	
	@Override
	public boolean containsPos(float x, float y){

		float r = (this.radius == 0 ? this.radius : this.radius);
		if (x > circleBody.getPosition().x-r && x < circleBody.getPosition().x+r &&
				y > circleBody.getPosition().y-r && y < circleBody.getPosition().y+r) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isSelecting(float x, float y){
		if ( containsPos(x,y) ){
			isPressed = true;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isSelected(float x, float y){
		if( containsPos(x, y) && isPressed ){
			isPressed = false;
			isSelected = !isSelected; 
			return true;
		}
		
		isPressed = false;
		return false;
	}
	
	@Override
	public Body getBody(){
		return circleBody;
	}
	
	@Override
	public float getWidth(){
		return this.radius;
	}
	
	@Override
	public float getHeight(){
		return this.radius;
	}
}

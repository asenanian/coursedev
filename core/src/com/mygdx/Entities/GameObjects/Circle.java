package com.mygdx.Entities.GameObjects;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.InputProcessing.AssetLoader;
import com.mygdx.XMLService.Beans.CircleBean;

public class Circle extends GameObjectUtility implements IGameObject {
	private final float pos[];
	private final boolean pinned;
	private final boolean hidden;
	private final float radius;
	private final float restitution;
	private final float friction;
	private final float density;
	
	private Sprite objectSprite;
	private Sprite shadowSprite;
	//private Fixture fixture;
	
	public static class Constructor {
		// required params
		private float pos[];
		private final boolean pinned;
		private final float radius;
		
		// option params
		private float restitution = 0.6f;
		private float friction = 0.1f;
		private float density = 1.0f;
		private boolean hidden = false;
		
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
		
		public Constructor hidden(boolean hidden)
		{ this.hidden = hidden;		return this;}
		
		public Circle Construct(){
			return new Circle(this);
		}
	}
	
	public Circle(Constructor constructor){
		super();
		pos = constructor.pos;
		pinned = constructor.pinned;
		hidden = constructor.hidden;
		radius = constructor.radius > 0.5 ? constructor.radius : 0.25f;
		restitution = constructor.restitution;
		friction = constructor.friction;
		density = constructor.density;
		objectSprite = new Sprite(pinned ? AssetLoader.circlePinned : AssetLoader.circle);
		shadowSprite = new Sprite(AssetLoader.circleShadow);
	}
	
	public Circle(CircleBean circleBean){
		super();
		this.pos = circleBean.getPos();
		this.radius = circleBean.getRadius();
		this.pinned = circleBean.getPinned();
		this.hidden = circleBean.getHidden();
		this.restitution = circleBean.getRestitution();
		this.friction = circleBean.getFriction();
		this.density = circleBean.getDensity();
		objectSprite = new Sprite(pinned ? AssetLoader.circlePinned : AssetLoader.circle);
		shadowSprite = new Sprite(AssetLoader.circleShadow);
	}
	
	@Override
	public void initialize(World world){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = pinned ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(pos[0],pos[1]);
		
		body = world.createBody(bodyDef);
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		
		body.createFixture(fixtureDef);
		circle.dispose();
		
		objectSprite.setOrigin(radius,radius);
		objectSprite.setSize(radius*2, radius*2);
		
		shadowSprite.setOrigin((radius/128)*(108+60),(radius/128)*(108+60));
		shadowSprite.setSize((radius*2/256)*(216 + 120),(radius*2/256)*(216 + 120));
	}
	
	@Override
	public void draw(SpriteBatch batcher){
		objectSprite.setPosition(body.getPosition().x - radius, body.getPosition().y - radius);
		objectSprite.setRotation((float)(Math.toDegrees(body.getAngle())));
		
		objectSprite.draw(batcher);
	}
	
	@Override
	public void drawShadows(SpriteBatch batcher){
		shadowSprite.setPosition((body.getPosition().x - (radius/128)*(128 + 60)), 
								 (body.getPosition().y - (radius/128)*(128 + 60)));
		shadowSprite.setRotation((float)(Math.toDegrees(body.getAngle())));
		
		shadowSprite.draw(batcher);
		
	}
	
	@Override
	public boolean containsPos(float [] pos){

		float r = (this.radius == 0 ? this.radius : this.radius);
		if (pos[0] > body.getPosition().x-r && pos[0] < body.getPosition().x+r &&
				pos[1] > body.getPosition().y-r && pos[1] < body.getPosition().y+r) {
			return true;
		}
		return false;
	}
	
	@Override
	public float getWidth(){
		return this.radius;
	}
	
	@Override
	public float getHeight(){
		return this.radius;
	}

	@Override
	public Serializable getBean() {
		
		CircleBean circleBean = new CircleBean();
		
		circleBean.setPinned(pinned);
		circleBean.setHidden(hidden);
		circleBean.setRadius(radius);
		circleBean.setPos(new float []{body.getPosition().x,body.getPosition().y});
		circleBean.setRestitution(restitution);
		circleBean.setFriction(friction);
		circleBean.setDensity(density);
		
		return circleBean;
	}
}

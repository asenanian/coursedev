package com.mygdx.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.GameWorld.GameConstants;

public class Chain implements IGameObject{
	private final Vector2 [] vertices;
	private final float restitution;
	private final float friction;
	private final float density;
	private final Color color;
	
	private Body body;
	private Fixture fixture;
	
	public static class Constructor {
		// required params
		private final Vector2 [] vertices;
		
		// option params
		private float restitution = 0.6f;
		private float friction = 0.1f;
		private float density = 1.0f;
		
		public Constructor(Vector2 [] vertices){

			this.vertices = vertices;	
		}
		
		public Constructor restitution(float val)
		{ this.restitution = val;		return this; }
		
		public Constructor friction(float val)
		{ this.friction = val;		return this; }
		
		public Constructor density(float val)
		{ this.density = val;		return this; }
		
		public Chain Construct(){
			return new Chain(this);
		}
	}
	
	public Chain(Constructor constructor){
		vertices = constructor.vertices;
		restitution = constructor.restitution;
		friction = constructor.friction;
		density = constructor.density;
		color = Color.BLACK;
	}
	
	@Override
	public void initialize(World world){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(0,0);
		
		body = world.createBody(bodyDef);
		
		ChainShape chainShape = new ChainShape();
		chainShape.createChain(vertices);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = chainShape;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		
		fixture = body.createFixture(fixtureDef);
		chainShape.dispose();
	}
	
	@Override
	public void draw(ShapeRenderer shapeRenderer){

		shapeRenderer.setColor(this.color);
		shapeRenderer.set(ShapeType.Filled);
		
		for(int i = 0 ; i < vertices.length - 1; i++){
			shapeRenderer.rectLine(vertices[i],	vertices[i+1], GameConstants.MODIFIER_WIDTH);
		}
	}
	
	@Override
	public void drawShadows(ShapeRenderer shapeRenderer, SpriteBatch batcher){

		shapeRenderer.setColor(50/255f,50/255f,50/255f,0.5f);
		shapeRenderer.set(ShapeType.Filled);
		
		for(int i = 0 ; i < vertices.length - 1; i++){
			shapeRenderer.rectLine(vertices[i].x - 0.05f, vertices[i].y - 0.05f, vertices[i+1].x - 0.05f, vertices[i+1].y - 0.05f, GameConstants.MODIFIER_WIDTH);
		}
	}
	
	@Override
	public boolean containsPos(float x, float y){
		return false;
	}
	
	@Override
	public boolean isSelecting(float x, float y){
		return false;
	}
	
	@Override
	public boolean isSelected(float x, float y){
		return false;
	}
	
	@Override
	public Body getBody(){
		return body;
	}
	
	@Override
	public float getWidth(){
		return this.density;
	}
	
	@Override
	public float getHeight(){
		return this.density;
	}
}

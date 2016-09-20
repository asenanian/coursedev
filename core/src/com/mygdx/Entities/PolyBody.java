package com.mygdx.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class PolyBody implements IGameObject{
	private final Vector2 [] vertices;
	private final float restitution;
	private final float friction;
	private final float density;
	private final Color color;
	private final boolean pinned;
	
	private Body body;
	private Fixture fixture;
	private Polygon bounds;
	
	public static class Constructor {
		// required params
		private final Vector2 [] vertices;
		private final boolean pinned;
		
		// option params
		private float restitution = 0.6f;
		private float friction = 0.1f;
		private float density = 1.0f;
		
		public Constructor(Vector2 [] vertices, boolean pinned){

			this.vertices = vertices;	
			this.pinned = pinned;
		}
		
		public Constructor restitution(float val)
		{ this.restitution = val;		return this; }
		
		public Constructor friction(float val)
		{ this.friction = val;		return this; }
		
		public Constructor density(float val)
		{ this.density = val;		return this; }
		
		public PolyBody Construct(){
			return new PolyBody(this);
		}
	}
	
	public PolyBody(Constructor constructor){
		vertices = constructor.vertices;
		pinned = constructor.pinned;
		restitution = constructor.restitution;
		friction = constructor.friction;
		density = constructor.density;
		color = pinned? Color.BLACK: Color.MAROON;
	}
	
	@Override
	public void initialize(World world){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = pinned ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;

		//bodyDef.position.set(.5f*(vertices[0].x + vertices[vertices.length - 1].x),
		//		.5f*(vertices[vertices.length - 1].y + vertices[0].y));
		
		bodyDef.position.set(0,0);
		
		body = world.createBody(bodyDef);
		
		PolygonShape polyShape = new PolygonShape();
		polyShape.set(vertices);
		/*
		for(int i = 1; i < vertices.length - 1; i++){
			chainShape.setNextVertex(vertices[i]);
		}
		*/		
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polyShape;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		
		fixture = body.createFixture(fixtureDef);
		polyShape.dispose();
		
		float vertexBounds [] = new float[vertices.length*2];
		
		for(int i = 0; i < vertices.length; i++){
			vertexBounds[2*i] = vertices[i].x;
			vertexBounds[2*i+1] = vertices[i].y;			
		}
		
		bounds = new Polygon(vertexBounds);
		
	}
	
	@Override
	public void draw(ShapeRenderer shapeRenderer){		
		bounds.translate(0.05f, 0.05f);

		//Gdx.app.log("",body.getPosition().x + "," +body.getPosition().y);
		shapeRenderer.setColor(this.color);
		shapeRenderer.set(ShapeType.Filled);
		shapeRenderer.polygon(bounds.getTransformedVertices());
		
	}
	
	@Override
	public void drawShadows(ShapeRenderer shapeRenderer, SpriteBatch batcher){
		bounds.setPosition(body.getPosition().x, body.getPosition().y);
		bounds.setRotation((float)(Math.toDegrees(body.getAngle())));
		
		
		//Gdx.app.log("",body.getPosition().x + "," +body.getPosition().y);
		shapeRenderer.setColor(50/255f,50/255f,50/255f,0.5f);
		shapeRenderer.set(ShapeType.Line);
		
		shapeRenderer.polyline(bounds.getTransformedVertices());
	}
	
	@Override	
	public boolean containsPos(float x, float y){
		return bounds.contains(x, y);
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

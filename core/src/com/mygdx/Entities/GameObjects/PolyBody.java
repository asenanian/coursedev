package com.mygdx.Entities.GameObjects;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.XMLService.Beans.PolygonBean;

public class PolyBody extends GameObjectUtility implements IGameObject{
	private final Vector2 [] vertices;
	private final float restitution;
	private final float friction;
	private final float density;
	private final boolean pinned;
	
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
		
		public Constructor(float [] p_vertices, boolean pinned){
			this.vertices = new Vector2 [p_vertices.length/2];
			
			for(int i = 0; i < p_vertices.length/2;i++){
				vertices[i] = new Vector2(p_vertices[2*i],p_vertices[2*i+1]);
			}
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
		super();
		vertices = constructor.vertices;
		pinned = constructor.pinned;
		restitution = constructor.restitution;
		friction = constructor.friction;
		density = constructor.density;
	}
	
	public PolyBody(PolygonBean polygonBean){
		super();
		float [] verticesInFloatArray = polygonBean.getVertices();
		this.vertices = new Vector2 [verticesInFloatArray.length/2];
		
		for(int i = 0; i < verticesInFloatArray.length/2;i++){
			vertices[i] = new Vector2(verticesInFloatArray[2*i],verticesInFloatArray[2*i+1]);
		}
		pinned = polygonBean.getPinned();
		restitution = polygonBean.getRestitution();
		friction = polygonBean.getFriction();
		density = polygonBean.getDensity();
	}
	
	@Override
	public void initialize(World world){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = pinned ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
		
		bodyDef.position.set(0,0);
		
		body = world.createBody(bodyDef);
		
		PolygonShape polyShape = new PolygonShape();
		polyShape.set(vertices);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polyShape;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		
		body.createFixture(fixtureDef);
		polyShape.dispose();
		
		float vertexBounds [] = new float[vertices.length*2];
		
		for(int i = 0; i < vertices.length; i++){
			vertexBounds[2*i] = vertices[i].x;
			vertexBounds[2*i+1] = vertices[i].y;	
		}
		
		bounds = new Polygon(vertexBounds);
		
	}
	
	@Override
	public void draw(SpriteBatch batcher){		
		bounds.setPosition(body.getPosition().x, body.getPosition().y);
		bounds.setRotation((float)(Math.toDegrees(body.getAngle())));
		//bounds.translate(0.05f, 0.05f);
	}
	
	@Override
	public void drawShadows(SpriteBatch batcher){

	}
	
	@Override	
	public boolean containsPos(float [] pos){
		return bounds.contains(pos[0],pos[1]);
	}
	
	@Override
	public float getWidth(){
		return this.density;
	}
	
	@Override
	public float getHeight(){
		return this.density;
	}
	
	public float [] getVertices(){
		return bounds.getTransformedVertices();
	}

	@Override
	public Serializable getBean() {
		PolygonBean polygonPacket = new PolygonBean();
		
		polygonPacket.setPinned(pinned);
		polygonPacket.setDensity(density);
		polygonPacket.setFriction(friction);
		polygonPacket.setRestitution(restitution);
		polygonPacket.setVertices(bounds.getTransformedVertices());

		return polygonPacket;
	}
}

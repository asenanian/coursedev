package com.mygdx.Entities.GameObjects;

import java.io.Serializable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.GameWorld.GameConstants;
import com.mygdx.InputProcessing.AssetLoader;
import com.mygdx.XMLService.Beans.ChainBean;

public class Chain extends GameObjectUtility implements IGameObject {
	private final Vector2 [] vertices;
	private final float restitution;
	private final float friction;
	private final float density;
	private final float width = GameConstants.MODIFIER_WIDTH / 2f; // distance from center to edge
	
	private Sprite objectSprite;
	private Sprite shadowSprite;
	
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
		
		public Constructor(float [] p_vertices){
			this.vertices = new Vector2 [p_vertices.length/2];
			
			for(int i = 0; i < p_vertices.length/2;i++){
				vertices[i] = new Vector2(p_vertices[2*i],p_vertices[2*i+1]);
			}
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
		super();
		vertices = constructor.vertices;
		restitution = constructor.restitution;
		friction = constructor.friction;
		density = constructor.density;
		objectSprite = new Sprite(AssetLoader.chain);
		shadowSprite = new Sprite(AssetLoader.chainShadow);
	}
	
	public Chain(ChainBean chainBean){
		super();
		
		float [] verticesInFloatArray = chainBean.getVertices();
		this.vertices = new Vector2 [verticesInFloatArray.length/2];
		
		for(int i = 0; i < verticesInFloatArray.length/2;i++){
			vertices[i] = new Vector2(verticesInFloatArray[2*i],verticesInFloatArray[2*i+1]);
		}
		
		restitution = chainBean.getRestitution();
		friction = chainBean.getFriction();
		density = chainBean.getDensity();
		objectSprite = new Sprite(AssetLoader.chain);
		shadowSprite = new Sprite(AssetLoader.chainShadow);
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
		
		body.createFixture(fixtureDef);
		chainShape.dispose();
		
		objectSprite.setOrigin(0, 0);
		shadowSprite.setOrigin(0, 0 );
	}
	
	@Override
	public void draw(SpriteBatch batcher){
		
		for(int i = 0 ; i < vertices.length - 1; i++){
			
			Vector2 diff = vertices[i+1].cpy().sub(vertices[i]);
			
			objectSprite.setPosition(vertices[i].x, vertices[i].y - width);
			objectSprite.setSize(diff.len(), width*2);

			objectSprite.setRotation(diff.angle());
			objectSprite.draw(batcher);
		}
		
	}
	
	@Override
	public void drawShadows(SpriteBatch batcher){
		
		for(int i = 0 ; i < vertices.length - 1; i++){
			Vector2 diff = vertices[i+1].cpy().sub(vertices[i]);
			
			shadowSprite.setPosition(vertices[i].x - ( width / 32 ) * (32 + 60), 
					vertices[i].y - ( width / 32 ) * (32 + 60) );
			shadowSprite.setSize(diff.len(), (width / 32) * ( 60 + 10 + 60));

			shadowSprite.setRotation(diff.angle());
			shadowSprite.draw(batcher);
		}

	}
	
	@Override
	public boolean containsPos(float [] pos){
		return false;
	}
	
	@Override
	public float getWidth(){
		return this.density;
	}
	
	@Override
	public float getHeight(){
		return this.density;
	}

	@Override
	public Serializable getBean() {
		float [] vertexArray = new float [vertices.length*2];
		for(int i = 0; i < vertices.length; i++){
			vertexArray[2*i] = vertices[i].x;
			vertexArray[2*i+1] = vertices[i].y;	
		}
		ChainBean chainbean = new ChainBean();
		chainbean.setVertices(vertexArray);
		return chainbean;
	}
}

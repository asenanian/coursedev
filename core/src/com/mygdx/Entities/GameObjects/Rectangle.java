package com.mygdx.Entities.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.shared.rectanglePacket;
import com.mygdx.managers.AssetLoader;

public class Rectangle implements IGameObject{
	private final float x;
	private final float y;
	private final float height;
	private final float width;
	private final boolean pinned;
	private final float restitution;
	private final float friction;
	private final float density;
	private final TextureRegion textureRegion;
	
	private Body body;
	private Fixture fixture;
	
	private boolean isSelected = false;
	private boolean isPressed = false;
	
	public static class Constructor {
		// required params
		private final float x;
		private final float y;
		private final float width;
		private final float height;
		
		private final boolean pinned;
		
		// option params
		private float restitution = 0.6f;
		private float friction = 0.1f;
		private float density = 1.0f;
		
		public Constructor(float x, float y, float width, float height, boolean pinned){

			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.pinned = pinned;			
		}
		
		public Constructor restitution(float val)
		{ this.restitution = val;		return this; }
		
		public Constructor friction(float val)
		{ this.friction = val;		return this; }
		
		public Constructor density(float val)
		{ this.density = val;		return this; }
		
		public Rectangle Construct(){
			return new Rectangle(this);
		}
	}
	
	public Rectangle(Constructor constructor){
		x = constructor.x;
		y = constructor.y;
		width = constructor.width > 0.05 ? constructor.width / 2 : 0.025f;
		height= constructor.height > 0.05? constructor.height / 2 : 0.025f;
		pinned = constructor.pinned;
		restitution = constructor.restitution;
		friction = constructor.friction;
		density = constructor.density;
		textureRegion = pinned ? AssetLoader.rectanglePinned : AssetLoader.rectangle;

	}
	
	@Override
	public void initialize(World world){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = pinned ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(x + width,y + height);
		
		body = world.createBody(bodyDef);
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(width, height);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		
		fixture = body.createFixture(fixtureDef);
		polygonShape.dispose();
	}
	
	@Override
	public void draw( SpriteBatch batcher){
		
		batcher.draw(textureRegion, 
				(body.getPosition().x - width), 
				(body.getPosition().y - height), 
				width, 
				height, 
				width*2,
				height*2,
				1f,1f,(float)(Math.toDegrees(body.getAngle())));

	}
	
	@Override
	public void drawShadows(SpriteBatch batcher){
		
		Sprite sprite = new Sprite(AssetLoader.rectangleShadow);
		sprite.setPosition(body.getPosition().x - (width/(128))*(128 + 60 ), 
				body.getPosition().y - (height/(128))*(128 + 60));
		sprite.setSize(  (width*2/(256))*(216 + 120),
						 (height*2/(256))*(216 + 120));
		sprite.setOrigin((width/(128))*(108 + 60), 
				 		 (height/(128))*(108 + 60));
		//sprite.setScale(xscale,yscale);
		sprite.setRotation((float)(Math.toDegrees(body.getAngle())));
		sprite.draw(batcher);		
	}
	
	@Override
	public boolean containsPos(float x, float y){

		return ( (x > (body.getPosition().x - width) && (x < body.getPosition().x + width) ) &&
				( (y > body.getPosition().y - height) && (y < body.getPosition().y + height) ) );
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
			isSelected = !isSelected; // toggle button
			return true;
		}
		
		isPressed = false;
		return false;
	}
	
	@Override
	public Body getBody(){
		return body;
	}
	
	@Override
	public float getHeight(){
		return this.height;
	}
	
	@Override
	public float getWidth(){
		return this.width;
	}
	
	@Override
	public Object getPacket(){
		return new rectanglePacket(x,y,width,height,pinned);
	}
}

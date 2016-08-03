package com.mygdx.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.mygdx.math.Vect3;

public abstract class Actor implements Solid {
       
	protected float x;
	protected float y;
	protected float z;
	
	protected float px;
	protected float py;
	protected float pz;
	
	protected float dx;
	protected float dy;
	protected float dz;
	
	protected float mass;
	
	protected Texture texture;
	protected Model model;
	protected ModelInstance instance;
        
	protected float gravity = -20000f;//-300f;
        
	protected Actor(float[] position, float[] velocity, String imageName, float mass) {		
		this.mass = mass;
		
		this.x = position[0];
		this.y = position[1];
		this.z = position[2];
		
		this.px = this.x;
		this.py = this.y;
		this.pz = this.z;
                
		this.dx = velocity[0];
		this.dy = velocity[1];
		this.dz = velocity[2];
				
		texture = new Texture(Gdx.files.internal(imageName));
	}
	public void init(ModelBuilder modelBuilder) {
		model = buildModel(modelBuilder);
		instance = new ModelInstance(this.model);
		instance.transform.translate(x,y,z);
	}
	public void print(Object obj) {
		System.out.println(obj);
	}
    public void update(float gravity,float dt) {
    	if (this instanceof Wall) return; //don't move walls
    	if (Math.abs(dx) < 0.001) dx = 0;
    	if (Math.abs(dy) < 0.001) dy = 0;
    	if (Math.abs(dz) < 0.001) dz = 0;
    	print("BEFORE: "+x+y+z);
		px = x;
		py = y;
		pz = z;
		dy += gravity;
		x += dx*dt;
		y += dy*dt;
		z += dz*dt;
		print("p"+px+py+pz);
		print("ds: "+dx+dy+dz);
		print("gravity:"+gravity);
		print("dt:"+dt);
		print("AFTER: "+x+y+z);
//		print("IN UPDATE ACTOR:");
//		print(x);
//		print(y);
//		print(z);
		instance.transform.setTranslation(x,y,z);
		instance.calculateTransforms();
    }
	
	public boolean collided(Solid other) {
		if (other instanceof Sphere) {
			return this.collidedWithSphere((Sphere)other);
		} else if (other instanceof Wall) {
			return this.collidedWithWall((Wall)other);
		} else {
			return false;
		}
	}

	public abstract boolean collidedWithSphere(Sphere other);
	public abstract boolean collidedWithWall(Wall wall);
	public abstract Model buildModel(ModelBuilder modelBuilder);
	
	public void draw(ModelBatch modelBatch, Environment environment) {
		modelBatch.render(instance,environment);
	}
	
	public Vect3 getPosition(){
		return new Vect3(this.x,this.y,this.z);
	}
	
	public Vect3 getVelocity(){
		return new Vect3(dx,dy,dz);
	}
	
	public float getMass(){
		return this.mass;
	}
	
	public Vect3 getPrevPosition() {
		return new Vect3(this.px,this.py,this.pz);
	}
	public double distance(Actor other) {
		return Math.sqrt(Math.pow(other.x-this.x,2) + 
		                   Math.pow(other.y-this.y,2) +
		                    Math.pow(other.z-this.z,2));
	}
	
       
}
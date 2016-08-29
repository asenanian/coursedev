package com.mygdx.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.TestVerlet;
import com.mygdx.game.Verlet3D;
import com.mygdx.gamestates.PlayState;

public class Point {
	public static ModelBuilder MODELBUILDER = new ModelBuilder();
	protected float[] pos;
	protected float[] prevPos;
	protected float[] vel;
	protected float bounce;
	protected float friction;
	protected float mass;
	protected float radius;
	protected boolean isAbstract;
	protected boolean is3D;
	protected boolean pinned;
	protected Color color;
	protected Model model;
	protected ModelInstance instance;
	//private ModelBuilder MODELBUILDER;
	public Point(float[] position, float[] prevPosition, 
					boolean is3D, boolean isAbstract, boolean pinned) {
		this.is3D = is3D;
		this.pos = position;
		//print(this.pos[0]+","+this.pos[1]);
		this.prevPos = prevPosition;
		this.pinned = pinned;
		this.isAbstract = isAbstract;
		init();
		//print(this);
	}
	public Point(float[] position, float[] prevPosition, boolean is3D, boolean pinned) {
		this(position,prevPosition,is3D,false,pinned);
	}
	public Point(Point p) {
		//this((float[])p.pos.clone(), (float[])p.prevPos.clone(), 
		//  			p.is3D,p.isAbstract,p.pinned);
		this.pos = cloneFloatArr(p.pos);
		this.prevPos = cloneFloatArr(p.prevPos);
		this.pinned = p.pinned;
		this.radius = p.radius;
		this.mass = p.mass;
		this.isAbstract = p.isAbstract;
		this.is3D = p.is3D;
		this.model = p.model;
		this.instance = p.instance;
		this.color = p.color;
		this.vel = cloneFloatArr(p.vel);
		this.bounce = p.bounce;
		this.friction = p.friction;
		if (this.isAbstract) { 
			initAbstract();
		}
	}
	public float[] cloneFloatArr(float[] arr) {
		float[] newArr = new float[arr.length];
		for (int i = 0; i < arr.length; i++) {
			newArr[i] = arr[i];
		}
		return newArr;
	}
	public void init() {
		this.color = (this.pinned ? Color.BLUE : Color.WHITE);
		if (!this.is3D) {
			//make z-coord = 0
			this.pos = new float[] {this.pos[0],this.pos[1],0};
			this.prevPos = new float[] {this.prevPos[0],this.prevPos[1],0};
		} else {
			//point in 3D
			this.radius = 4;
			makeModel(); 
			makeInstance();
			//print(this.instance+"hello");
		}
		this.vel = new float[] {pos[0]-prevPos[0],
								pos[1]-prevPos[1],
								pos[2]-prevPos[2]};
		this.bounce = 0.9f;
		this.friction = 0.999f;
		this.mass = 1;
		if (!(this instanceof Circle)) {
			this.radius = 4;
		}
		if (this.isAbstract) { 
			initAbstract();
		}
	}
	
//	public String toString() {
//		String msg = "";
//		msg += "Pos: ["+this.pos[0]+", "+this.pos[1]+"]\n";
//		msg += "Vel: ["+this.vel[0]+", "+this.vel[1]+"]\n";
//		return msg;
//	}
	public void makeModel() {
		this.model = Point.MODELBUILDER.createSphere(
				this.radius*2, this.radius*2, this.radius*2, 24, 24, 
					new Material(TextureAttribute.AmbientAlias,ColorAttribute.createSpecular(this.color),
							FloatAttribute.createShininess(20f)), 
						Usage.Position | Usage.Normal | Usage.TextureCoordinates);
	}
	public void makeInstance() {
		this.instance = new ModelInstance(this.model);
		this.instance.transform.translate(this.pos[0],this.pos[1],this.pos[2]);
	}
	
	public void initAbstract() {
		this.radius = 0;
	}
	public void print(Object obj) {
		System.out.println(obj);
	}
	public void update(float gravity) {
		//print(this.color);
		if (this.pinned) return;
		//print("pos: "+pos[1]);
		//print("prevPos: "+prevPos[1]);
		vel[0] = (pos[0] - prevPos[0]) * friction;
		vel[1] = (pos[1] - prevPos[1]) * friction;
		vel[2] = (pos[2] - prevPos[2]) * friction;
//		for (int i = 0; i < 3; i++) {
//			if (Math.abs(vel[i]) < 0.001f) {
//				vel[i] = 0;
//			}
//		}
		//print("point vel: "+this.vel[1]);
		prevPos[0] = pos[0];
		prevPos[1] = pos[1];
		prevPos[2] = pos[2];
		pos[0] += vel[0];
		pos[1] += vel[1];
		pos[2] += vel[2];
		pos[1] += gravity;
		keepInBounds();
		
//		print("PREVPOS: "+prevPos[0]+","+prevPos[1]);
//		print("POS: "+pos[0]+","+pos[1]);
//		print("VEL: "+vel[0]+","+vel[1]);
//		print("");
		
		if (this.is3D) {
			this.instance.transform.setToTranslation(this.pos[0],this.pos[1],this.pos[2]);
			this.instance.calculateTransforms();
		}
		//print("after update:"+this);
	}
	public boolean inBounds() {
		if (is3D) {
			return inBounds3D();
		} else {
			return inBounds2D();
		}
	}
	public void keepInBounds() {
		if (is3D) {
			keepInBounds3D();
		} else {
			keepInBounds2D();
		}
	}
	public void keepInBounds3D() {
		//bounce off walls
		//print("checking: "+pos[1]+" < "+(Verlet3D.FLOOR+radius)+" = "+(pos[1] < Verlet3D.FLOOR + radius));
		if (pos[0] > Verlet3D.RIGHTWALL-radius) {
			pos[0] = Verlet3D.RIGHTWALL-radius;
			prevPos[0] = pos[0] + vel[0] * bounce;
		} else if (pos[0] < Verlet3D.LEFTWALL+radius) {
			pos[0] = Verlet3D.LEFTWALL+radius;
			prevPos[0] = pos[0] + vel[0] * bounce;
		}
		if (pos[1] > Verlet3D.CEILING-radius) {
			pos[1] = Verlet3D.CEILING-radius;
			prevPos[1] = pos[1] + vel[1] * bounce;
		} else if (pos[1] < Verlet3D.FLOOR+radius) {
			pos[1] = Verlet3D.FLOOR+radius;
			prevPos[1] = pos[1] + vel[1] * bounce;
		}
		if (pos[2] > Verlet3D.BACKWALL-radius) {
			pos[2] = Verlet3D.BACKWALL-radius;
			prevPos[2] = pos[2] + vel[2] * bounce;
		} else if (pos[2] < Verlet3D.FRONTWALL+radius) {
			pos[2] = Verlet3D.FRONTWALL+radius;
			prevPos[2] = pos[2] + vel[2] * bounce;
		}
	}
	public void keepInBounds2D() {
		//bounce off walls
		//print("checking: "+pos[1]+" < "+(Verlet3D.FLOOR+radius)+" = "+(pos[1] < Verlet3D.FLOOR + radius));
		if (pos[0] > PlayState.RIGHTWALL-radius) {
			pos[0] = PlayState.RIGHTWALL-radius;
			prevPos[0] = pos[0] + vel[0] * bounce;
		} else if (pos[0] < PlayState.LEFTWALL+radius) {
			pos[0] = PlayState.LEFTWALL+radius;
			prevPos[0] = pos[0] + vel[0] * bounce;
		}
		if (pos[1] > PlayState.CEILING-radius) {
			pos[1] = PlayState.CEILING-radius;
			prevPos[1] = pos[1] + vel[1] * bounce;
		} else if (pos[1] < PlayState.FLOOR+radius) {
			pos[1] = PlayState.FLOOR+radius;
			prevPos[1] = pos[1] + vel[1] * bounce;
		}
	}
	
	public boolean inBounds3D() {
		if (pos[0] > Verlet3D.LEFTWALL && pos[0] < Verlet3D.RIGHTWALL &&
				pos[1] > Verlet3D.FLOOR && pos[1] < Verlet3D.CEILING && 
				pos[2] > Verlet3D.FRONTWALL && pos[2] < Verlet3D.BACKWALL) {
			return true;
		}
		return false;
	}
	
	public boolean inBounds2D() {
		if (pos[0] > PlayState.LEFTWALL && pos[0] < PlayState.RIGHTWALL &&
				pos[1] > PlayState.FLOOR && pos[1] < PlayState.CEILING) {
			return true;
		}
		return false;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	public void setPos(float[] p) {
		//if (this.pinned) return;
		if (!is3D) {
			p = new float[] {p[0],p[1],0};
		}
		this.pos = new float[] {p[0],p[1],p[2]};
	}
	
	public void setPrevPos(float[] p) {
		//if (this.pinned) return;
		if (!is3D) {
			p = new float[] {p[0],p[1],0};
		}
		this.prevPos = new float[] {p[0],p[1],p[2]};
	}
	
	public void setPos(float x, float y) {
		this.pos = new float[] {x,y,0};
	}
	
	public void setPos(float x, float y, float z) {
		this.pos = new float[] {x,y,z};
	}
	
	public void addXY(float dx, float dy) {
		if (this.pinned) return;
		this.pos[0] += dx;
		this.pos[1] += dy;
	}
	
	public void addXYZ(float dx, float dy, float dz) {
		if (this.pinned) return;
		this.pos[0] += dx;
		this.pos[1] += dy;
		this.pos[2] += dz;
	}
	
	public void addVect2D(float scl, Point p1, Point p2) {
		if (this.pinned) return;
		float angle = getTheta(p1.getPos(),p2.getPos());
		float dx = scl * (float)Math.cos(angle);
		float dy = scl * (float)Math.sin(angle);
		this.addXY(dx,dy);
	}
	public void addVect3D(float scl, Point p1, Point p2) {
		if (this.pinned) return;
		float theta = getTheta(p1.getPos(), p2.getPos());
		float phi = getPhi(p1.getPos(), p2.getPos());
		float dx = scl * (float)(Math.sin(phi)*Math.cos(theta));
		float dy = scl * (float)(Math.sin(phi)*Math.sin(theta));
		float dz = scl * (float)(Math.cos(phi));
		this.addXYZ(dx,dy,dz);
	}
	
	public float getTheta(float[] p1, float[] p2) {
		return (float)Math.atan2(p2[1]-p1[1],p2[0]-p1[0]);
	}
	public float getPhi(float[] p1, float[] p2) {
		float xydist = (float)Math.sqrt(Math.pow(p1[0]-p2[0],2)+Math.pow(p1[1]-p2[1],2));
		return (float)Math.atan2(xydist, p2[2]-p1[1]);
	}
	
	public float[] getPos() {
		return this.pos;
	}
	public float[] getPrevPos() {
		return this.prevPos;
	}
	public float getRadius() {
		return this.radius;
	}
	public boolean getAbstract() {
		return this.isAbstract;
	}
	public boolean get3D() {
		return this.is3D;
	}
	public boolean getPinned() {
		return this.pinned;
	}
	public float getMass() {
		return this.mass;
	}
	
	
	public void setRadius(float newRadius) {
		this.radius = newRadius;
	}
	public void toggleAbstract() {
		this.isAbstract = !this.isAbstract;
		if (this.isAbstract) {
			initAbstract();
		}
	}
	public void makeBig() {
		if (isAbstract || this instanceof Circle) return;
		this.radius = 10;
	}
	public void makeSmall() {
		if (isAbstract || this instanceof Circle) return;
		this.radius = 4;
	}
	
	public boolean containsPos(float[] pos) {
		//DOESN'T WORK FOR 3D
		float r = (this.radius == 0 ? 4 : this.radius);
		if (pos[0] > this.pos[0]-r && pos[0] < this.pos[0]+r &&
			  pos[1] > this.pos[1]-r && pos[1] < this.pos[1]+r) {
			return true;
		}
		return false;
	}
	
	public void draw(ShapeRenderer shapeRenderer) {
		//for 2D
		shapeRenderer.setColor(this.color);
		//print(this.pos[1]+"pos[1] in draw");
		if (!this.isAbstract) {
			shapeRenderer.circle(this.pos[0], this.pos[1], this.radius);
		}
	}
	public void draw(ModelBatch modelBatch, Environment environment) {
		//for 3D
		if (!this.isAbstract) {
			//print(this.instance.transform.getScaleX())
			//print(this.instance);
			modelBatch.render(this.instance,environment);
			//print(this.instance.toString());
		}
	}

}
package com.mygdx.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.GameWorld.GameConstants;


public class Point {

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
	//private ModelBuilder MODELBUILDER;
	public Point(float[] position, float[] prevPosition, 
					boolean isAbstract, boolean pinned) {
		this.pos = position;
		//print(this.pos[0]+","+this.pos[1]);
		this.prevPos = prevPosition;
		this.pinned = pinned;
		this.isAbstract = isAbstract;
		init();
		//print(this);
	}
	public Point(float[] position, float[] prevPosition, boolean pinned) {
		this(position,prevPosition,false,pinned);
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
		this.color = (this.pinned ? Color.BLUE : Color.MAROON);
		//make z-coord = 0
		this.pos = new float[] {this.pos[0],this.pos[1],0};
		this.prevPos = new float[] {this.prevPos[0],this.prevPos[1],0};
		
		this.vel = new float[] {pos[0]-prevPos[0],
								pos[1]-prevPos[1]};
		this.bounce = 0.999f;
		this.friction = 0.999f;
		this.mass = 1;
		if (!(this instanceof Circle)) {
			this.radius = 4;
		}
		if (this.isAbstract) { 
			initAbstract();
		}
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
//		for (int i = 0; i < 3; i++) {
//			if (Math.abs(vel[i]) < 0.001f) {
//				vel[i] = 0;
//			}
//		}
		//print("point vel: "+this.vel[1]);
		prevPos[0] = pos[0];
		prevPos[1] = pos[1];
		pos[0] += vel[0];
		pos[1] += vel[1];
		pos[1] += gravity;
		keepInBounds();
		
	}
	public boolean inBounds() {
		if (pos[0] > GameConstants.LEFTWALL && pos[0] < GameConstants.RIGHTWALL &&
				pos[1] > GameConstants.FLOOR && pos[1] < GameConstants.CEILING) {
			return true;
		}
		return false;
	}
	public void keepInBounds() {
		if (pos[0] > GameConstants.RIGHTWALL-radius) {
			pos[0] = GameConstants.RIGHTWALL-radius;
			prevPos[0] = pos[0] + vel[0] * bounce;
		} else if (pos[0] < GameConstants.LEFTWALL+radius) {
			pos[0] = GameConstants.LEFTWALL+radius;
			prevPos[0] = pos[0] + vel[0] * bounce;
		}
		if (pos[1] > GameConstants.CEILING-radius) {
			pos[1] = GameConstants.CEILING-radius;
			prevPos[1] = pos[1] + vel[1] * bounce;
		} else if (pos[1] < GameConstants.FLOOR+radius) {
			pos[1] = GameConstants.FLOOR+radius;
			prevPos[1] = pos[1] + vel[1] * bounce;
		}
	}

	
	public void setColor(Color color) {
		this.color = color;
	}
	public void setPos(float[] p) {
		//if (this.pinned) return;
		this.pos = new float[] {p[0],p[1]};
	}
	
	public void setPrevPos(float[] p) {
		this.prevPos = new float[] {p[0],p[1]};
	}
	
	public void setPos(float x, float y) {
		this.pos = new float[] {x,y};
	}
	
	public void setRadius(float newRadius) {
		this.radius = newRadius;
	}
	
	public void addXY(float dx, float dy) {
		if (this.pinned) return;
		this.pos[0] += dx;
		this.pos[1] += dy;
	}
	
	public void addVect2D(float scl, Point p1, Point p2) {
		if (this.pinned) return;
		float angle = getTheta(p1.getPos(),p2.getPos());
		float dx = scl * (float)Math.cos(angle);
		float dy = scl * (float)Math.sin(angle);
		this.addXY(dx,dy);
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
	public boolean getPinned() {
		return this.pinned;
	}
	public float getMass() {
		return this.mass;
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


}

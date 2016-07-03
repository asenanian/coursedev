package com.mygdx.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.TestVerlet;

public class Point {
	private float[] pos;
	private float[] prevPos;
	private float[] vel;
	private float bounce;
	private float friction;
	private float mass;
	private float radius;
	private boolean isAbstract;
	private boolean isBig;
	private boolean isSmall;
	private boolean pinned;
	public Point(float[] position, float[] prevPosition, boolean abstractPoint, boolean pinned) {
		this.pos = position;
		this.prevPos = prevPosition;
		this.pinned = pinned;
		if (abstractPoint) {
			this.isAbstract = true;
			this.radius = 0;
		} else {
			this.isAbstract = false;
			this.radius = 4;
		}
		init();
	}
	public Point(float[] position, float[] prevPosition, boolean pinned) {
		this.pos = position;
		this.prevPos = prevPosition;
		this.pinned = pinned;
		this.radius = 4;
		init();
	}
	public Point(Point p) {
		this.pos = (float[]) p.pos.clone();
		this.prevPos = (float[]) p.prevPos.clone();
		this.pinned = p.pinned;
		this.radius = p.radius;
		init();
	}
	
	public String toString() {
		String msg = "";
		msg += "Pos: ["+this.pos[0]+", "+this.pos[1]+"]\n";
		msg += "Vel: ["+this.vel[0]+", "+this.vel[1]+"]\n";
		return msg;
	}
	
	public void init() {
		this.vel = new float[] {pos[0]-prevPos[0],pos[1]-prevPos[1]};
		this.bounce = 0.9f;
		this.friction = 0.99f;
		this.mass = 1;
		this.isBig = false;
		this.isSmall = true;
	}
	public void print(Object obj) {
		System.out.println(obj);
	}
	public void update(float gravity) {
		if (this.pinned) return;
		//print("pos: "+pos[1]);
		//print("prevPos: "+prevPos[1]);
		this.vel[0] = (pos[0] - prevPos[0]) * friction;
		this.vel[1] = (pos[1] - prevPos[1]) * friction;
		//print("point vel: "+this.vel[1]);
		prevPos[0] = pos[0];
		prevPos[1] = pos[1];
		pos[0] += vel[0];
		pos[1] += vel[1];
		pos[1] += gravity;
		
		//bounce off walls
		if (pos[0] > TestVerlet.RIGHTWALL) {
			pos[0] = TestVerlet.RIGHTWALL;
			prevPos[0] = pos[0] + vel[0] * bounce;
		} else if (pos[0] < TestVerlet.LEFTWALL) {
			pos[0] = TestVerlet.LEFTWALL;
			prevPos[0] = pos[0] + vel[0] * bounce;
		}
		if (pos[1] > TestVerlet.CEILING) {
			pos[1] = TestVerlet.CEILING;
			prevPos[1] = pos[1] + vel[1] * bounce;
		} else if (pos[1] < TestVerlet.FLOOR) {
			pos[1] = TestVerlet.FLOOR;
			prevPos[1] = pos[1] + vel[1] * bounce;
		}
	}
	
	public void setPos(float[] pos) {
		//if (this.pinned) return;
		this.pos = new float[] {pos[0],pos[1]};
	}
	public void setPrevPos(float[] pos) {
		//if (this.pinned) return;
		this.prevPos = new float[] {pos[0],pos[1]};
	}
	
	public void setPos(float x, float y) {
		this.pos = new float[] {x,y};
	}
	public void addXY(float dx, float dy) {
		if (this.pinned) return;
		this.pos[0] += dx;
		this.pos[1] += dy;
	}
	public void addVect(float scl, float ang) {
		if (this.pinned) return;
		float dx = scl * (float)Math.cos(ang);
		float dy = scl * (float)Math.sin(ang);
		this.addXY(dx,dy);
	}
	
	public float[] getPos() {
		return this.pos.clone();
	}
	public float getRadius() {
		return this.radius;
	}
	public void setRadius(float newRadius) {
		this.radius = newRadius;
	}
	public void makeBig() {
		if (isAbstract) return;
		this.isBig = true;
		this.isSmall = false;
		this.radius = 10;
	}
	public void makeSmall() {
		if (isAbstract) return;
		this.isBig = false;
		this.isSmall = true;
		this.radius = 4;
	}
	
	public boolean getPinned() {
		return this.pinned;
	}
	public boolean containsPos(float[] pos) {
		float r = (this.radius == 0 ? 4 : this.radius);
		if (pos[0] > this.pos[0]-r && pos[0] < this.pos[0]+r &&
			  pos[1] > this.pos[1]-r && pos[1] < this.pos[1]+r) {
			return true;
		}
		return false;
	}
	
	public void draw(ShapeRenderer shapeRenderer) {
		if (!this.isAbstract) {
			shapeRenderer.circle(this.pos[0], this.pos[1], this.radius);
		}
		//point(this.pos[0],this.pos[1],0);
	}

	public float getMass() {
		return this.mass;
	}
}

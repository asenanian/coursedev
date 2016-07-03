package com.mygdx.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class Spring {
	private float length;
	private ArrayList<Point> points;
	private int p1ind, p2ind;
	private Point p1,p2;
	private float k; //spring constant
	private float w; //width of spring
	public Spring(ArrayList<Point> points, int ind1, int ind2, float k, float w) {
		this.points = points;
		this.p1ind = ind1;
		this.p2ind = ind2;
		this.p1 = points.get(p1ind);
		this.p2 = points.get(p2ind);
		//System.out.println("Just made p1 and p2: "+this.p1+" "+this.p2);
		this.length = distance(p1,p2);
		this.k = k;
		this.w = w;
	}
	public Spring(ArrayList<Point> points, int ind1, int ind2, float k) {
		this(points,ind1,ind2,k,1);
	}
	public Spring(Spring s) {
		this(s.getPoints(),s.getP1ind(),s.getP2ind(),s.getK());
	}
	
	public ArrayList<Point> getPoints() {
		return this.points;
	}
	public float getLength() {
		return this.length;
	}
	
	public Point getPoint1() {
		return this.p1;
	}
	public Point getPoint2() {
		return this.p2;
	}
	public float getK() {
		return this.k;
	}
	
	public void setPoint1(Point p) {
		this.p1 = p;
	}
	public void setPoint2(Point p) {
		this.p2 = p;
	}
	
	public void print(Object obj) {
		System.out.println(obj);
	}
	
	public float[] getRestoringAccels(float disp) {
		float force = k*disp;
		//System.out.println(disp);
		float accel1 = force / p1.getMass();
		float accel2 = force / p2.getMass();
		//print("accel1: "+accel1);
		return new float[] {accel1, -accel2};
	}
	
	public void update(ArrayList<Point> points) {
		this.p1 = points.get(p1ind);
		this.p2 = points.get(p2ind);
	}
	
	public float distance(Point p1, Point p2) {
		float[] pos1 = p1.getPos();
		float[] pos2 = p2.getPos();
		return (float)Math.sqrt(Math.pow(pos1[0]-pos2[0],2) + 
								Math.pow(pos1[1]-pos2[1],2));
	}
	
	public void draw(ShapeRenderer shapeRenderer) {
		if (p1 != null && p2 != null) {
			float[] pos1 = this.p1.getPos();
			float[] pos2 = this.p2.getPos();
			//shapeRenderer.line(pos1[0], pos1[1], pos2[0], pos2[1]);
			shapeRenderer.rectLine(pos1[0], pos1[1], pos2[0], pos2[1], this.w);
		}
	}
	public int getP1ind() {
		return this.p1ind;
	}
	public int getP2ind() {
		return this.p2ind;
	}
}

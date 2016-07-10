package com.mygdx.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class Spring {
	private float length;
	private ArrayList<Point> points;
	private int p1ind, p2ind;
	private Point p1,p2;
	private float k; //spring constant
	private float w; //width of spring
	private boolean hidden; //if spring is hidden or not
	private Color color;
	private ModelBuilder modelBuilder;
	private MeshBuilder meshBuilder;
	private Model model;
	private ModelInstance instance;
	public Spring(ArrayList<Point> points, int ind1, int ind2, float k, float w, boolean hidden) {
		this.points = points;
		this.p1ind = ind1;
		this.p2ind = ind2;
		this.p1 = points.get(p1ind);
		this.p2 = points.get(p2ind);
		if (this.p1.get3D()) {
			this.modelBuilder = new ModelBuilder();
			this.model = makeModel();
			this.instance = makeInstance();
		}
		//System.out.println("Just made p1 and p2: "+this.p1+" "+this.p2);
		this.length = distance(p1,p2);
		this.k = k;
		this.w = w;
		this.hidden = hidden;
		this.color = Color.RED;
	}
	public Spring(ArrayList<Point> points, int ind1, int ind2, float w, boolean hidden) {
		this(points,ind1,ind2,-99999,w,hidden);
	}
	
	public Vector3 floatArrayToVect(float[] pos) {
		return new Vector3(pos[0],pos[1],pos[2]);
	}
	public Model makeModel() {
		Vector3 p1vect = floatArrayToVect(p1.getPos());
		Vector3 p2vect = floatArrayToVect(p2.getPos());
		return this.modelBuilder.createCylinder(this.w, p2vect.sub(p1vect).len(), this.w, 24, 
					new Material(ColorAttribute.AmbientAlias), 
						Usage.Position | Usage.Normal);
//		model = modelBuilder.createCylinder(w, w, w, 24,
//				new Material(ColorAttribute.AmbientAlias), Usage.Position | Usage.Normal);
//        model = modelBuilder.createArrow(p1vect, p2vect, 
//    				new Material(ColorAttribute.AmbientAlias),
//    					Usage.Position | Usage.Normal);
	}	
	public ModelInstance makeInstance() {
		Vector3 p1vect = floatArrayToVect(p1.getPos());
		Vector3 p2vect = floatArrayToVect(p2.getPos());
        ModelInstance tmpInstance = new ModelInstance(this.model);
        tmpInstance.transform.setTranslation(100,100,100);//p1vect);
        tmpInstance.transform.setToLookAt(p2vect, Vector3.Y);
        tmpInstance.calculateTransforms();
        //instance.transform.setToRotation(p1vect, p2vect);
        return tmpInstance;
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
	
	public float[] getRestoringAccels(float displacement,float dt) {
		float force = k*displacement*dt;
		//System.out.println(disp);
		float accel1 = force / p1.getMass();
		float accel2 = force / p2.getMass();
		//print("accel1: "+accel1);
		return new float[] {accel1, -accel2};
	}
	
	public void update(ArrayList<Point> points) {
		this.p1 = points.get(p1ind);
		this.p2 = points.get(p2ind);
		if (this.p1.get3D()) {
			this.model = makeModel();
			this.instance = makeInstance();
		}
	}
	
	public float distance(Point p1, Point p2) {
		float[] pos1 = p1.getPos();
		float[] pos2 = p2.getPos();
		return (float)Math.sqrt(Math.pow(pos1[0]-pos2[0],2) + 
								Math.pow(pos1[1]-pos2[1],2) +
								Math.pow(pos1[2]-pos2[2],2));
	}
	
	public void draw(ShapeRenderer shapeRenderer) {
		//FOR 2D only
		if (!this.hidden) {
			float[] pos1 = this.p1.getPos();
			float[] pos2 = this.p2.getPos();
			//shapeRenderer.line(pos1[0], pos1[1], pos2[0], pos2[1]);
			shapeRenderer.setColor(this.color);
			shapeRenderer.rectLine(pos1[0], pos1[1], pos2[0], pos2[1], this.w);
		}
	}
	public void draw(ModelBatch modelBatch, Environment environment) {
		modelBatch.render(this.instance, environment);
	}
	
	public int getP1ind() {
		return this.p1ind;
	}
	public int getP2ind() {
		return this.p2ind;
	}
	public Color getColor() {
		return this.color;
	}
}

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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.math.Vect3;

public class Spring {
	public static ModelBuilder MODELBUILDER = new ModelBuilder();
	public static int LINEWIDTH = 2;
	private float length;
	private ArrayList<Point> points;
	private int p1ind, p2ind;
	private Point p1,p2;
	private float k; //spring constant
	private boolean hidden; //if spring is hidden or not
	private boolean rigid; //if spring is springy or rigid
	private Color color;
	private ModelBuilder modelBuilder;
	private Model model;
	private ModelInstance modelInstance;
	public Spring(ArrayList<Point> points, int ind1, int ind2, float k, boolean hidden) {
		this.points = (ArrayList<Point>) points.clone();
		this.p1ind = ind1;
		this.p2ind = ind2;
		this.p1 = points.get(p1ind);
		this.p2 = points.get(p2ind);
		if (this.p1.get3D()) {
			makeModel();
			makeInstance();
			if (this.p1ind == 0) {
				this.p1.setColor(Color.ORANGE);
				this.p2.setColor(Color.ORANGE);
				this.p1.makeModel();
				this.p1.makeInstance();
				this.p2.makeModel();
				this.p2.makeInstance();
			}
		}
		//System.out.println("Just made p1 and p2: "+this.p1+" "+this.p2);
		this.length = distance(p1,p2);
		this.k = k;
		this.hidden = hidden;
		this.rigid = this.k > 10f;
		this.color = (this.rigid ? Color.BLUE : Color.RED);
	}
	
	public void makeModel() {
		Vector3 p1vect = new Vector3(p1.getPos());
		Vector3 p2vect = new Vector3(p2.getPos());
		this.model = Spring.MODELBUILDER.createCylinder(Spring.LINEWIDTH, //width
											p2vect.sub(p1vect).len(), //height 
											Spring.LINEWIDTH, 24,  //depth
							new Material(ColorAttribute.AmbientAlias), 
						Usage.Position | Usage.Normal);
//		model = modelBuilder.createCylinder(w, w, w, 24,
//				new Material(ColorAttribute.AmbientAlias), Usage.Position | Usage.Normal);
//        model = modelBuilder.createArrow(p1vect, p2vect, 
//    				new Material(ColorAttribute.AmbientAlias),
//    					Usage.Position | Usage.Normal);
	}
	public void makeInstance() {
		//only for 3D--something is wrong with setting the position
		//Vector3 p1vect = new Vector3(p1.getPos());
		//Vector3 p2vect = new Vector3(p2.getPos());
        //(1000000,0,0);//p1vect);
        //Vector3 trans = modelInstance.transform.getTranslation(p1vect);
        //modelInstance.transform.trn(trans);
        //modelInstance.transform.setToLookAt(p2vect, Vector3.Y);
//      modelInstance.transform.setToRotation(p1vect, p2vect);
//		modelInstance.transform.setTranslation(new Vector3(p1.getPos()));//[0],p1.getPos()[1],p1.getPos()[2]);
//		modelInstance.transform.setToRotation(p1vect, p2vect);
		this.modelInstance = new ModelInstance(this.model);
		modelInstance.calculateTransforms();
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
	
	public ArrayList<Point> updatePoints(ArrayList<Point> points, float dt) {
		//for updating spring force or stick algorithm (only for 2D)
		this.update(points); //update this.p1 and this.p2 with this.points
		//print(p1);
		//print(p1.getPos()[0]+","+p1.getPos()[1]);
		float[] p1pos = this.p1.getPos();
		float[] p2pos = this.p2.getPos();
		float dx = p2pos[0]-p1pos[0];
		float dy = p2pos[1]-p1pos[1];
		float dz = p2pos[2]-p1pos[2];
		float dist;
		if (this.p1.get3D()) {
			//3D
			dist = (float)Math.sqrt(dx * dx + dy * dy + dz * dz); //dist bt points
		} else {
			//2D
			dist = (float)Math.sqrt(dx * dx + dy * dy); //dist bt points
		}
		float diff = dist - this.length;
		//print("It is quite " + this.rigid + " that this spring is rigid.");
		if (this.rigid) {
			//for rigid spring
			if (diff > 0) {
				float percent = diff / dist / 2;
				float offsetX = dx * percent;
				float offsetY = dy * percent;
				float offsetZ = dz * percent;
				this.p1.addXYZ(offsetX, offsetY, offsetZ);
				this.p2.addXYZ(-offsetX, -offsetY, -offsetZ);
				//print(p1.getPos()[1]);
			}
		} else {
			//for springy (low k) spring
			float[] restoringAccels = this.getRestoringAccels(diff,dt);
			float p1accel = restoringAccels[0];
			float p2accel = restoringAccels[1];
			if (this.p1.get3D()) {
				this.p1.addVect3D(p1accel,this.p1,this.p2);
				this.p2.addVect3D(p2accel,this.p1,this.p2);
			} else {
				this.p1.addVect2D(p1accel,this.p1,this.p2);
				this.p2.addVect2D(p2accel,this.p1,this.p2);
			}
		}
		this.points.set(p1ind, p1);
		this.points.set(p2ind, p2);
		return this.points;
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
		this.points = (ArrayList<Point>) points.clone();
		this.p1 = this.points.get(p1ind);
		this.p2 = this.points.get(p2ind);
		if (this.p1.get3D()) {
			
			//DOESN'T WORK--spring isn't connecting points yet
			Vector3 p1vect = new Vector3(p1.getPos());
			Vector3 p2vect = new Vector3(p2.getPos());
			//this.model = new Model();
			makeModel();
			//this.modelInstance = new ModelInstance(this.model);
			makeInstance();
			//modelInstance.transform.setToLookAt(new Vector3(p2.getPos()), Vector3.Y);
			//modelInstance.transform.setToTranslation(new Vector3(p1.getPos()));
			//modelInstance.transform.setToRotation(p1vect, p2vect);
			//modelInstance.transform.rotate(Vector3.X, 100);
			Vector3 segmentAxis = p2vect.cpy().sub(p1vect).nor();
			//Vector3 perpAxis = p2vect
			Vector3 midPoint = p1vect.cpy().add(p2vect.cpy().sub(p1vect).scl(0.5f));
			//modelInstance.transform.rotate(p1vect,p2vect);//p2vect.cpy().sub(p1vect),Vector3.Y);
			//modelInstance.transform.setToRotation(Vector3.Y, tmp.set(endPoints[0]).sub(endPoints[1]).nor());
			//Vector3 up = Vector3.Y.crs(p)
			modelInstance.transform.setToLookAt(segmentAxis,Vector3.Y);
			modelInstance.transform.trn(midPoint);
			modelInstance.calculateTransforms();
			//float[] vals = modelInstance.transform.getValues();
			//print(vals[0]+","+vals[1]+","+vals[2]+","+vals[3]);
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
			shapeRenderer.rectLine(pos1[0], pos1[1], pos2[0], pos2[1], Spring.LINEWIDTH);
		}
	}
	public void draw(ModelBatch modelBatch, Environment environment) {
		modelBatch.render(modelInstance, environment);
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

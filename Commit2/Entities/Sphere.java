package com.mygdx.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.mygdx.math.Vect3;

public class Sphere extends Solid {
	
	protected float px;
	protected float py;
	protected float pz;
	
	protected float dx;
	protected float dy;
	protected float dz;
	
	protected float mass;
	protected float radius;
	protected float radians;
	protected float rotationSpeed;
	protected float elasticity;
	
	protected Texture texture;
	protected String imageName = "Spheres/tiles.png";
	protected Model model;
	protected ModelInstance instance;
	public Sphere(float[] position, float[] velocity, float mass, float radius, ModelBuilder modelBuilder) {
		super("sphere");
		this.x = position[0];
		this.y = position[1];
		this.z = position[2];
		this.px = this.x;
		this.py = this.y;
		this.pz = this.z;
		this.dx = velocity[0];
		this.dy = velocity[1];
		this.dz = velocity[2];
		this.mass = mass;
		this.radius = radius;
		this.radians = 0;
		this.elasticity = 0.8f;
		//no slipping
		this.rotationSpeed = (float)Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2)+Math.pow(dz, 2));
		texture = new Texture(Gdx.files.internal(imageName));
		model = buildSphereModel(modelBuilder);
		instance = new ModelInstance(this.model);
		instance.transform.translate(x,y,z);
	}
	
	public void update(float dt) {
		//System.out.println((float)dx*dt);
		//instance.transform.translate(new Vect3(dx*dt,dy*dt,dz*dt));
		//print("pos: "+getPosition());
		px = x;
		py = y;
		pz = z;
		dy += gravity*dt;
		x += dx*dt;
		y += dy*dt;
		z += dz*dt;
		instance.transform.setTranslation(x,y,z);
		//instance.transform.setToRotation(new Vect3(dx,dy,dz), degrees);
		instance.calculateTransforms();
	}

	public void draw(ModelBatch modelBatch, Environment environment) {
		modelBatch.render(instance,environment);
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
	
	public boolean collidedWithSphere(Sphere other) {
		if (this.distance(other) - (this.radius + other.radius) < 0.01f) {
			return true;
		}
		return false;
	}
	
	public void checkInOtherSpheres(Sphere[] spheres) {
		for (Sphere sphere : spheres) {
			if (this.equals(sphere)) continue;
			if (sphere == null) continue;
			if (this.inOtherSphere(sphere)) {
				this.getOutOf(sphere);
			}
		}
	}
	//bounds of a 200x200x200 box centered at origin
	public boolean inBounds() {;
		float r = radius;
		if (x > -95+r && x < 95-r &&
			  y > -95+r && y < 95-r &&
				z > -95+r && z < 95-r) {
			return true;
		}
		return false;
	}
	public boolean inLegalPosition(Sphere[] spheres, Wall[] walls) {
		if (!inBounds() || inWall(walls)) {
			return false;
		}
		for (Sphere sphere : spheres) {
			if (sphere != null) {
				if (this.equals(sphere)) continue;
				if (inOtherSphere(sphere)) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	
	
	
	private int getCloserIndex(Wall wall) {
		int closerIndex = 4; //default
		Vect3[] corners = wall.getCorners();
		Vect3 pos = getPosition();
		Vect3 prevpos = getPrevPosition();
		if (distance(pos,corners[0]) < distance(pos,corners[4]) &&
			  distance(prevpos,corners[0]) < distance(prevpos,corners[4])) {
			closerIndex = 0; //index of closer corner 
		}
		return closerIndex;
	}
	
	public boolean collidedWithWall(Wall wall) {
		//Get points on edge of sphere (edgePoints), then
		//check if any are within wall bounds.
		
		//collisionPrecision is how many edgePoints are used
		float collisionPrecision = 4;
		//get the 8 corners of the wall
		Vect3[] corners = wall.getCorners();
		//get 4 points on the edge of the sphere
		Vect3[] edgePoints = this.getEdgePoints(collisionPrecision);
		
		for (int p = 0; p < edgePoints.length; p++) {
			Vect3 point = new Vect3(edgePoints[p]);
			
			for (int c = 0; c < corners.length; c++) {
				int closerIndex = getCloserIndex(wall);
				int furtherIndex = (closerIndex == 0 ? 4 : 0);
				
				//get dist from point on sphere to closest corner on wall
				Vect3 AP = corners[closerIndex].sub(point);
				//get unit vectors at right angles to each other on closer plane of wall
				Vect3 AB = corners[closerIndex].sub(corners[closerIndex+1]).setLength(1);
				Vect3 AC = corners[closerIndex].sub(corners[closerIndex+2]).setLength(1);
				//get perpendicular dist from point to closer side of wall
				float d1 = Math.abs(AP.dot(AB.crs(AC).scl(-1)));
				
				//get dist from point on sphere to far corner on wall
				Vect3 EP = corners[furtherIndex].sub(point);
				//get unit vectors at right angles to each other on further plane of wall
				Vect3 EF = corners[furtherIndex].sub(corners[furtherIndex+1]).setLength(1);
				Vect3 EG = corners[furtherIndex].sub(corners[furtherIndex+2]).setLength(1);
				//get perpendicular dist from point to far side of wall
				float d2 = Math.abs(EP.dot(EF.crs(EG)));
				//print("d1 ="+d1+" , d2 ="+d2);
				//check if point is within wall bounds
				if (d1 + d2 <= wall.getThickness()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private Vect3[] getEdgePoints(float n) {
		Vect3[] edgePoints = new Vect3[(int)(n*n)];
		double[] zangles = new double[(int)n];
		double[] xyangles = new double[(int)n];
		for (int i = 0; i < (int)n; i++) {
			zangles[i] = i*2*Math.PI/n;
			xyangles[i] = i*2*Math.PI/n;
		}
		//make edgePoints
		for (int i = 0; i < (int)n; i++) {
			for (int j = 0; j < (int)n; j++) {
				edgePoints[j+i*(int)n] = new Vect3((float)(this.radius*Math.sin(zangles[i])*Math.cos(xyangles[j])),
												(float)(this.radius*Math.sin(zangles[i])*Math.sin(xyangles[j])),
												(float)(this.radius*Math.cos(zangles[i])));
			}
		}
		for (int i = 0; i < edgePoints.length; i++) {
			edgePoints[i].x += this.x;
			edgePoints[i].y += this.y;
			edgePoints[i].z += this.z;
			//print("["+edgePoints[i].x+", "+edgePoints[i].y+", "+edgePoints[i].z+"]");
		}
		//print("\n");
		
		return edgePoints;
	}
	
	public void processCollision(Solid other) {
		if (other instanceof Sphere) {
			processSphereCollision((Sphere)other);
			
		} else if (other instanceof Wall) {
			//print("hello?");
			processWallCollision((Wall)other);
		} else {
			print("Can't process collision with "+other);
		}
	}
	
	public void processSphereCollision(Sphere other) {
		Vect3 pos1 = new Vect3(this.x,this.y,this.z);
		Vect3 pos2 = new Vect3(other.x,other.y,other.z);
		Vect3 dist12 = pos2.cpy().sub(pos1);
		Vect3 dist21 = pos1.cpy().sub(pos2);
		Vect3 v1i = getVelocity();
		//print(this+"\n"+"initial v:"+v1i);
		Vect3 v2i = other.getVelocity();
		Vect3 vcm = v1i.scl(this.mass).add( v2i.scl(other.mass) ).scl(1/(this.mass+other.mass));
		Vect3 v1f = v1i.sub( dist12.scl(v1i.sub(vcm).dot(dist12)).scl(1/dist12.len2()) );
		Vect3 v2f = v2i.sub( dist21.scl(v2i.sub(vcm).dot(dist21)).scl(1/dist21.len2()) );
		this.dx = v1f.x*elasticity;
		this.dy = v1f.y*elasticity;
		this.dz = v1f.z*elasticity;
		other.dx = v2f.x*elasticity;
		other.dy = v2f.y*elasticity;
		other.dz = v2f.z*elasticity;
		
		//get spheres out of each other...works reasonably well
		this.getOutOf(other);
		
		
		
		//print("initial v: "+v1i);
		//print("final v: "+v1f);
		//print("\n");
		
	}
	
	public void getOutOf(Sphere other) {
		if (this.collidedWithSphere(other)) {
			Vect3 dist21 = this.getPosition().sub(other.getPosition());
			Vect3 disp = dist21.cpy().setLength((float)(this.radius+other.radius-this.distance(other)));
			this.x += disp.x/2;
			this.y += disp.y/2;
			this.z += disp.z/2;
			other.x -= disp.x/2;
			other.y -= disp.y/2;
			other.z -= disp.z/2;
		}
	}
	
	public void getOutOf(Wall wall, Vect3 N, Vect3[] corners, int close) {
		if (this.collidedWithWall(wall)) {
			
		}
	}
	
	public void processWallCollision(Wall wall) {
		print("COLLIDED WITH WALL");
		Vect3[] corners = wall.getCorners();
		int closerIndex = getCloserIndex(wall);
		int furtherIndex = (closerIndex == 0 ? 4 : 0);
		//Vect3 AB = corners[closerIndex].cpy().sub(corners[closerIndex+1]).setLength(1);
		//Vect3 AC = corners[closerIndex].cpy().sub(corners[closerIndex+2]).setLength(1);
		Vect3 vi = getVelocity();
		Vect3 N = corners[closerIndex].sub(corners[furtherIndex]).setLength(1);
		//print("N = "+N);
		//N = AB.cpy().crs(AC).setLength(1);
		//if (N.cpy().add(vi).len() > vi.len()) N.scl(-1);
		Vect3 vfn = new Vect3(); //vi in plane of normal vector
		vfn.x = N.crs(vi.cpy().scl(-1)).len();
		vfn.y = -N.dot(vi.cpy().scl(-1*this.elasticity));
		vfn.z = 0;
		//print("vin: "+new Vect3(vfn.x,-vfn.y,0));
		//print("vfn: "+vfn);
		//print("N: "+N);
		//print("vi: "+vi);
		//print("N x vi: "+N.cpy().crs(vi.cpy().scl(-1)).len());
		//print(N.cpy().crs(vi.cpy().scl(-1)).len());
		
		Vect3 vf = N.setLength(vfn.y).add((vi.scl(-1).crs(N)).crs(N).setLength(vfn.x)); 
		
		//vf = N.cpy().scl(N.cpy().dot(vi.setLength(1))*2).sub(vi.setLength(1));
		//print("V initial: "+vi);
		//print("V final: "+vf);
		//print(N.cpy().setLength(vfn.y));
		//print(vi.cpy().scl(-1).crs(N));
		//print((vi.cpy().scl(-1).crs(N)).crs(N).setLength(vfn.x));
		//if (vf.len() > 1e10) for(int i=0;i<100;i++)print("yourfaceisstupid");
		this.dx = vf.x*elasticity;
		this.dy = vf.y*elasticity;
		this.dz = vf.z*elasticity;
		//get out of wall
		Vect3 bottomOfSphere = this.getPosition().sub(N.cpy().scl(this.radius));
		Vect3 bottomToCloserCorner = corners[closerIndex].cpy().sub(bottomOfSphere);
		Vect3 perpDist = N.cpy().scl(bottomToCloserCorner.cpy().dot(N));
		//print("bottomOfSphere, pos: "+bottomOfSphere+" , "+this.getPosition());
		//print("bottomToCloserCorner: "+bottomToCloserCorner);
		//print("perp dist: "+perpDist);
		//this.getOutOf(wall);
		this.x += perpDist.x;
		this.y += perpDist.y;
		this.z += perpDist.z;
	}
	

	public boolean inOtherSphere(Sphere other) {
		if (this.distance(other.getPosition()) < this.radius + other.radius) {
			return true;
		}
		return false;
	}
	public boolean inWall(Wall[] walls) {
		for (Wall wall : walls) {
			if (collidedWithWall(wall)) {
				return true;
			}
		}
		return false;
	}
	
	public void print(Object obj) {
		System.out.println(obj);
	}
	
	private double distance(Solid other) {
		return Math.sqrt(Math.pow(other.x-this.x,2) + 
		                   Math.pow(other.y-this.y,2) +
		                    Math.pow(other.z-this.z,2));
	}
	private double distance(Vect3 point) {
		return Math.sqrt((double)(Math.pow(point.x-this.x,2) + 
				                   Math.pow(point.y-this.y,2) +
				                    Math.pow(point.z-this.z,2)));
	}
	public double distance(Vect3 p1, Vect3 p2) {
		return Math.sqrt((double)(Math.pow(p1.x-p2.x,2) + 
                Math.pow(p1.y-p2.y,2) +
                 Math.pow(p1.z-p2.z,2)));
	}
	
	public Vect3 getPosition() {
		return new Vect3(this.x,this.y,this.z);
	}
	
	public Vect3 getPrevPosition() {
		return new Vect3(this.px,this.py,this.pz);
	}
	
	private Vect3 getVelocity() {
		return new Vect3(dx,dy,dz);
	}
	
	public float getRadius() {
		return this.radius;
	}
	
	private Model buildSphereModel(ModelBuilder modelBuilder) {
		final Material material = new Material(TextureAttribute.createDiffuse(texture), 
									ColorAttribute.createSpecular(1, 1, 1, 1),
									FloatAttribute.createShininess(20f));
		final long attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
		Model model = modelBuilder.createSphere(radius*2, radius*2, radius*2, 24, 24, material, attributes);
		return model;
	}

}

package com.mygdx.math;

import com.badlogic.gdx.math.Vector3;

public class Vect3 extends Vector3 {
	public Vect3() {
		super();
	}
	public Vect3(float x, float y, float z) {
		super(x,y,z);
	}
	public Vect3(Vect3 vect) {
		super(vect);
	}
	
	public Vect3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	@Override
	public Vect3 cpy () {
		return new Vect3(this);
	}
	
	//@Override
	public Vect3 add (final Vect3 vector) {
		return new Vect3(this.x+vector.x, this.y+vector.y, this.z+vector.z);
	}
	//@Override
	public Vect3 sub (final Vect3 vector) {
		return new Vect3(this.x-vector.x, this.y-vector.y, this.z-vector.z);
	}
	public Vect3 setLength(float scalar) {
		return new Vect3(this.x, this.y, this.z).scl(scalar/this.len());
	}
	public Vect3 scl (float scalar) {
		return new Vect3(this.x * scalar, this.y * scalar, this.z * scalar);
	}
	public Vect3 crs (Vect3 vector) {
		return new Vect3(this.y * vector.z - this.z * vector.y, 
						this.z * vector.x - this.x * vector.z, 
						this.x * vector.y - this.y * vector.x);
	}
}

package com.mygdx.Entities;


public class Circle extends Point {
	public float elasticity = 0.9f;
	//private float[] pos;
	//private Color color;
	//private boolean pinned;
	public Circle(float[] position, float[] prevPosition, float radius, boolean pinned) {
		super(position, prevPosition, false, pinned);
		setRadius(radius);
	}
	public Circle(float[] position, float radius, boolean pinned) {
		this(position,position,radius,pinned);
	}
	public Circle(Circle circle) {
		super(circle);
	}
	public boolean collided(Circle other) {
		//print("CHECKING COLLISIONS"+distance(other)+" < "+this.radius+" + "+other.radius+"?");
		if (distance(other) < this.radius + other.radius) {
			return true;
		}
		return false;
	}

	public void processCollision(Circle other,float gravity) {
		/*
		 * REMAINING BUGS:
		 * -vertical stacks of circles sometimes stick to each other
		 * 	and don't seem to obey gravity.
		 * -user can squish circles together with mouse
		 * -bad collisions for some (low) elasticities
		 */
	    if (collided(other)) {
	    	//keepInBounds();
	    	//print(this.pos[1]+","+other.pos[1]+": thisy, othery");
	    	vel[0] = (pos[0] - prevPos[0]) * friction;
			vel[1] = (pos[1] - prevPos[1]) * friction;
			other.vel[0] = (other.pos[0] - other.prevPos[0]) * friction;
			other.vel[1] = (other.pos[1] - other.prevPos[1]) * friction;
	    	float nHatX = this.pos[0] - other.pos[0];
	    	float nHatY = this.pos[1] - other.pos[1];
	    	//print(nHatY+"nhatY");
	    	float norm = (float) Math.sqrt(nHatX*nHatX + nHatY*nHatY);
	    	//print(norm+"norm");
	    	nHatX /= norm; nHatY /= norm;
	    	//project v's in nHat direction
	    	float v1 = this.vel[0]*nHatX + this.vel[1]*nHatY;
	    	float v2 = other.vel[0]*nHatX + other.vel[1]*nHatY;
	    	//collision in nHat direction same as 1d problem
	    	float mTotal = mass+other.mass;
	    	float coef =  (mass-other.mass)/mTotal;
	    	float v1New  = coef*v1 + 2*other.mass*v2/mTotal;
	    	float v2New = 2*mass*v1/mTotal - coef*v2;
	    	//no change in v perpendicular to nHat
	    	float v1Perp = this.vel[0]*nHatY - this.vel[1]*nHatX;
	    	float v2Perp = other.vel[0]*nHatY - other.vel[1]*nHatX;

	    	//Now construct vector out of these

	    	//reset this circle's pos and vel
	    	//pos[0] = prevPos[0];
	    	//pos[1] = prevPos[1];
	    	float velX = v1New*nHatX + v1Perp*nHatY;
	    	float velY = v1New*nHatY - v1Perp*nHatX;

	    	//reset other circle's pos and vel
	    	//other.pos[0] = other.prevPos[0];
	    	//other.pos[1] = other.prevPos[1];
	    	float othervelX = v2New*nHatX + v2Perp*nHatY;
	    	float othervelY = v2New*nHatY - v2Perp*nHatX;

	    	float diff = (radius + other.radius) - distance(other);
	    	//float angle = getTheta(pos,other.pos);
	    	//get other circle out of this circle + update other
	    	//other.pos[0] = pos[0] - nHatX*(other.radius+radius)/2;
	    	//other.pos[1] = pos[1] - nHatY*(other.radius+radius)/2;
	    	if (!other.pinned) {
	    		other.pos[0] -= (nHatX * diff);// + gravity);
		    	other.pos[1] -= (nHatY * diff);// + gravity);
		    	other.prevPos[0] = other.pos[0] - othervelX * elasticity;
		    	other.prevPos[1] = other.pos[1] - othervelY * elasticity;
	    	}
	    	//update this velocity
	    	if (!pinned) {
	    		pos[0] += (nHatX * diff);// + gravity);
	    		pos[1] += (nHatY * diff);// + gravity);
	    		prevPos[0] = pos[0] - velX * elasticity;
	    		prevPos[1] = pos[1] - velY * elasticity;
	    	}
	    	
	    	other.update(gravity);
	    	this.update(gravity);
	    	//pos[0] += velX;
	    	//pos[1] += velY;
	    	//get other circle's diff/vel
//	    	print("before:"+pos[1]);
//	
//	    	print("after:"+pos[1]);
	    	//this.keepInBounds();
	    	//other.keepInBounds();
	    	//other.pos[0] += othervelX;
	    	//other.pos[1] += othervelY;
	    	//	       print("CIRCLE:");
	    	//	       print("new pos:"+pos[0]+","+pos[1]);
	    	//	       print("new vel:"+velX+","+velY);
	    	//	       print("other new pos:"+other.pos[0]+","+pos[1]);
	    	//	       print("other new vel:"+othervelX+","+othervelY);
	    }
	}
	public float distance(Circle other) {
		float dx = this.pos[0]-other.pos[0];
		float dy = this.pos[1]-other.pos[1];
		return (float)Math.sqrt(dx * dx + dy * dy);
	}
	public float distance(float[] pos) {
		float dx = this.pos[0]-pos[0];
		float dy = this.pos[1]-pos[1];
		return (float)Math.sqrt(dx * dx + dy * dy);
	}
	

}

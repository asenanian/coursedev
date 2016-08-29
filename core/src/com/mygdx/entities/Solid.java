package com.mygdx.entities;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mygdx.math.Vect3;

public interface Solid {//Anything that can be collided with. Actors and walls.
	public Vect3 getPosition();
	public void draw(ModelBatch modelBatch, Environment environment);
}
package com.mygdx.entities;

public abstract class Solid {
	protected boolean isSolid = true;
	protected String type;
	protected float x;
	protected float y;
	protected float z;
	protected float gravity = 0;//-300f;
	protected Solid(String type) {
		this.type = type;
	}
}

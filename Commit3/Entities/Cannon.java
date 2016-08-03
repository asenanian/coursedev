package com.mygdx.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Cannon extends Image {
	public static String imgInternalPath = "Verlet/GameObjects/cannon.png";
	public static float DEFAULT_POWER = 10f;
	public static float DEFAULT_WIDTH = 60f;
	public static float DEFAULT_HEIGHT = 40f;
	private float angle; // degrees?
	private float power;
	private boolean loaded;
	private int projIndex;

	public Cannon(float[] position, float angle, float power, float width, float height) {
		super(imgInternalPath, position, width, height);
		this.angle = angle;
		this.power = power;
		this.loaded = false;
	}

	public Cannon(float[] position, float angle) {
		this(position, angle, DEFAULT_POWER, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public void addProjectile(int index) {
		this.projIndex = index;
		this.loaded = true;
	}

	public ArrayList<Point> fire(ArrayList<Point> points) {
		float[] projPos = points.get(projIndex).getPos();
		float[] projPrevPos = new float[2];
		projPrevPos[0] = projPos[0] - power * (float) Math.cos(angle);
		projPrevPos[1] = projPos[1] - power * (float) Math.sin(angle);
		points.get(projIndex).setPrevPos(projPrevPos); // sets velocity to
														// (pos-prevPos)
		return points;
	}

	public boolean getLoaded() {
		return this.loaded;
	}

	public float getAngle() {
		return this.angle;
	}

	public int getProjIndex() {
		return this.projIndex;
	}
}

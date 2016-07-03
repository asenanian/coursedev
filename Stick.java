package com.mygdx.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Stick extends Spring {
	private boolean hidden;
	public Stick(ArrayList<Point> points, int p1ind, int p2ind, boolean hidden) {
		super(points, p1ind, p2ind, -1f); //last arg is k, which won't be
										//used because this is a Stick
		this.hidden = hidden;
	}
	@Override
	public void draw(ShapeRenderer shapeRenderer) {
		if (!this.hidden) {
			super.draw(shapeRenderer);
		}
	}
}

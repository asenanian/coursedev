package com.mygdx.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Stick extends Spring {
	private Color color;
	private boolean hidden;
	public Stick(ArrayList<Point> points, int p1ind, int p2ind, float w, boolean hidden) {
		super(points, p1ind, p2ind, w, hidden);
		this.color = Color.BLUE;
	}
	@Override
	public void draw(ShapeRenderer shapeRenderer) {
		if (!this.hidden) {
			super.draw(shapeRenderer);
		}
	}
}

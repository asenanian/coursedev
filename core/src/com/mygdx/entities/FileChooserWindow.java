package com.mygdx.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class FileChooserWindow extends Rect {
	public static float alpha = 0.3f;
	private int nCols;//number of text cols
	private int nRows;
	public FileChooserWindow(float[] rectBounds) {
		super(rectBounds[0],rectBounds[1],rectBounds[2],
				rectBounds[3],new Color(0.8f,0.8f,0.8f, alpha),true);
		this.nCols = 5;
		this.nRows = 5;
	}
	public FileChooserWindow(float x, float y, float w, float h) {
		this(new float[] {x,y,w,h});
	}
	public int getNCols() {
		return this.nCols;
	}
	public int getNRows() {
		return this.nRows;
	}
}

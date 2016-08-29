package com.mygdx.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Rect {
	protected float[] center;
	protected float[] bounds;
	protected float width;
	protected float height;
	protected float left;
	protected float right;
	protected float top;
	protected float bottom;
	protected Color color;
	protected Color outlineColor;
	protected boolean filled;
	protected float w;
	public Rect (float left, float bottom, float width, float height, Color color, boolean filled) {
		this.width = width;
		this.height = height;
		
		this.bounds = new float[4];
		this.bounds[0] = left;
		this.bounds[1] = bottom;
		this.bounds[2] = width;
		this.bounds[3] = height;
		
		this.center = new float[2];
		this.center[0] = left+width/2;
		this.center[1] = bottom+height/2;
		
		this.left = left;
		this.right = left+width;
		this.bottom = bottom;
		this.top = bottom+height;
		
		this.color = color;
		this.outlineColor = color;
		this.filled = filled;
		this.w = 1;
		
	}
	public Rect (float left, float bottom, float width, float height, boolean filled) {
		this(left,bottom,width,height,null,filled);
	}
	public Rect (float[] center, float width, float height, Color color, boolean filled) {
		//alternate constructor sets center
		this(center[0]-width/2,center[1]-height/2,width,height,color,filled);
	}
	public Rect (float[] center, float width, float height, boolean filled) {
		//alternate constructor sets center
		this(center[0]-width/2,center[1]-height/2,width,height,filled);
	}
	public Rect (Color color, boolean filled) {
		this(0,0,0,0,color,filled);
	}
	public boolean containsPos(float[] pos) {
		//center is from (0,0) at bottom left of screen
		if (pos[0] > left && pos[0] < right &&
				pos[1] > bottom && pos[1] < top) {
			return true;
		}
		return false;
	}
	public float[] getCenter() {
		return center;
	}
	public float getX() {
		return this.center[0];
	}
	public float getY() {
		return this.center[1];
	}
	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}
	public float getLeft() {
		return left;
	}
	public float getRight() {
		return right;
	}
	public float getBottom() {
		return bottom;
	}
	public float getTop() {
		return top;
	}
	
	public void setCenter(float[] center) {
		if (center.length != this.center.length) {
			print("TRIED TO SET RECT POS WITH BAD DIMENSIONS: "+this);
		} else {
			for (int i = 0; i < center.length; i++) {
				this.center[i] = center[i];
			}
			updateBounds();
		}
	}
	public void setCenter(float x, float y) {
		setCenter(new float[] {x,y});
	}
	public void setLeft(float left) {
		this.left = left;
		center[0] = left+width/2;
		updateBounds();
	}
	public void setRight(float right) {
		this.right = right;
		center[0] = right-width/2;
		updateBounds();
	}
	public void setBottom(float bottom) {
		this.bottom = bottom;
		center[1] = bottom+height/2;
		updateBounds();
	}
	public void setTop(float top) {
		this.top = top;
		center[1] = top-height/2;
		updateBounds();
	}
	public void setWidth(float w) {
		this.width = w;
		updateBounds();
	}
	public void setHeight(float h) {
		this.height = h;
		updateBounds();
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public void setOutlineColor(Color color) {
		this.outlineColor = color;
	}
	public void updateBounds() {
		left = center[0]-width/2;
		right = center[0]+width/2;
		bottom = center[1]-height/2;
		top = center[1]+height/2;
		bounds[0] = left;
		bounds[1] = bottom;
		bounds[2] = width;
		bounds[3] = height;
	}
	public void drawOutline(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(outlineColor);
		shapeRenderer.rectLine(left,top,right,top,w);
		shapeRenderer.rectLine(right,top,right,bottom,w);
		shapeRenderer.rectLine(right,bottom,left,bottom,w);
		shapeRenderer.rectLine(left,bottom,left,top,w);
	}
	public void drawFilled(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(color);
		shapeRenderer.rect(left,bottom,width,height);
	}
	public void draw(ShapeRenderer shapeRenderer) {
		if (color == null) return;
		shapeRenderer.set(ShapeType.Filled);
		if (filled) drawFilled(shapeRenderer);
		drawOutline(shapeRenderer);
	}
	
	public void print(Object obj) {
		System.out.println(obj);
	}
}

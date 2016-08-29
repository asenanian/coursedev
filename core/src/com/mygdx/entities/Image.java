package com.mygdx.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Image extends Rect implements MultiDraw {
	protected String internalPath;
	protected String name;
	protected Texture texture;
	public Image(String internalPath, float[] center, float width, float height, Color color, boolean filled) {
		super(center,width,height,color,filled); //color is for image frame
		this.internalPath = internalPath;
		this.name = this.internalPath.substring(this.internalPath.lastIndexOf("/"));
		this.texture = new Texture(this.internalPath);
	}
	public Image(String internalPath, float x, float y, float width, float height, Color color, boolean filled) {
		this(internalPath,new float[] {x,y},width,height,color,filled);
	}
	public Image(String internalPath, float[] center, float width, float height) {
		this(internalPath,center,width,height,Color.WHITE,false); //white outline/frame
	}
	public Image(String internalPath, float x, float y, float width, float height) {
		this(internalPath,new float[] {x,y},width,height);
	}
	public Image(String internalPath, float[] center) {
		this(internalPath,center,0,0);
	}
	public Image(String internalPath) {
		this(internalPath,new float[] {0,0});
	}
	public Image() {
		this(null);
	}
	public void setImage(String internalPath) {
		this.internalPath = internalPath;
		this.texture = new Texture(this.internalPath);
	}
	public String getName() {
		return this.name;
	}
	public String getInternalPath() {
		return this.internalPath;
	}
	public void drawSprites(SpriteBatch batch) {
		batch.draw(texture, left, bottom, width, height);
	}
	public void drawShapes(ShapeRenderer shapeRenderer) {
		this.draw(shapeRenderer);
	}
}

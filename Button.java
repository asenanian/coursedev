package com.mygdx.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class Button {
	private float[] pos;
	private float width;
	private float height;
	private String img;
	private Texture texture;
	private boolean clicked;
	private float alpha;
	public Button(float[] pos, float width, float height, String img) {
		this.pos = pos;
		this.width = width;
		this.height = height;
		this.img = img;
		this.texture = new Texture(img);
		this.clicked = false;
		this.alpha = 1f;
	}
	public float[] getPos() {
		return pos;
	}
	public boolean getClicked() {
		return clicked;
	}
	public void setClicked(boolean b) {
		clicked = b;
	}
	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}
	public void draw(SpriteBatch batch) {
		batch.draw(texture, pos[0], pos[1], width, height);
	}
	public void drawRect(ShapeRenderer shapeRenderer) {
		if (clicked) {
			alpha = 0.3f;
		}
		else {
			alpha = 1f;
		}
		shapeRenderer.setColor(new Color(0.3f,0.3f,0.3f, alpha));
		shapeRenderer.rect(pos[0], pos[1], width, height);
		
	}
	
}

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

public class Button extends Image implements MultiDraw {
	public static Color unclickedColor = Color.DARK_GRAY;
	public static Color clickedColor = Color.RED;
	private boolean clicked;
	public Button(String internalPath, float[] pos, float width, float height) {
		super(internalPath,pos,width,height,unclickedColor,true);
		this.clicked = false;
	}
	public Button(String internalPath, float x, float y, float width, float height) {
		this(internalPath,new float[] {x,y},width,height);
	}
	public boolean getClicked() {
		return clicked;
	}
	public void setClicked(boolean b) {
		clicked = b;
		changeColor();
	}
	public void toggleClicked() {
		clicked = !clicked;
		changeColor();
	}
	public void changeColor() {
		setColor(this.color==unclickedColor ? clickedColor : unclickedColor);
	}
	public void drawSprites(SpriteBatch batch) {
		super.drawSprites(batch);
	}
	public void drawShapes(ShapeRenderer shapeRenderer) {
		super.drawShapes(shapeRenderer);
	}
	
}

package com.mygdx.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface MultiDraw {
	public void drawSprites(SpriteBatch batch);
	public void drawShapes(ShapeRenderer shapeRenderer);
}

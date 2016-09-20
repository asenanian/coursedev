package com.mygdx.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface IJoint {
	public void draw(ArrayList<IGameObject> points, ShapeRenderer shapeRenderer);
}

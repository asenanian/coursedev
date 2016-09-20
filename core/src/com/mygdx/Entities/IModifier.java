package com.mygdx.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface IModifier {
	public void initialize(ArrayList<IGameObject> points);
	public void draw(ArrayList<IGameObject> points, ShapeRenderer shapeRenderer);	
	public void update(ArrayList<IGameObject> points);
	public boolean isLarge(ArrayList<IGameObject> points);
}

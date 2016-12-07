package com.mygdx.Entities.Joints;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.Entities.GameObjects.IGameObject;

public interface IJoint {
	public void draw(ArrayList<IGameObject> points, ShapeRenderer shapeRenderer);
	public void update(ArrayList<IGameObject> points);
}

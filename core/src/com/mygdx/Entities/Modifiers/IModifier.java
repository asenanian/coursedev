package com.mygdx.Entities.Modifiers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.Entities.GameObjects.IGameObject;

public interface IModifier {
	public void initialize();
	public void draw(ShapeRenderer shapeRenderer);	
	public void update();
	public void setGameObject(IGameObject gameObject);
}

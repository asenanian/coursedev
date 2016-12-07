package com.mygdx.GameWorld;

import java.io.FileNotFoundException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;

import com.mygdx.Renderer.GameRenderer;
import com.mygdx.managers.GameInputManager;
import com.mygdx.managers.MenuInputProcessor;

public class GameScreen implements Screen {
	
	private GameManager manager;
	private GameRenderer renderer;
	private float runTime;
	
	// Box 2d World
	World world;
	
	public GameScreen(){
		
		GameConstants.setConstants();
		
		Box2D.init();
		
		world = new World(new Vector2(0,-GameConstants.GRAVITY),true);
		try { manager = new GameManager(world); }
		catch(FileNotFoundException fe)
		{
			Gdx.app.log("FileNotFound", fe.getMessage());
		}
		renderer = new GameRenderer(manager);
		
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(new MenuInputProcessor(manager));
		multiplexer.addProcessor(new GameInputManager(manager,renderer));
		Gdx.input.setInputProcessor(multiplexer);
		
		renderer.initButtons();
		manager.setRenderer(renderer);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
	}

	@Override
	public void render(float delta) {
		runTime += delta;
		renderer.render(delta,runTime);
		manager.update(delta);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		world.dispose();
		renderer.dispose();
		// TODO Auto-generated method stub
		
	}

}

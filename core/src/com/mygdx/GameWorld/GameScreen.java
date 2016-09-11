package com.mygdx.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.managers.GameInputProcessor;
import com.mygdx.managers.MenuInputProcessor;

public class GameScreen implements Screen {
	
	private GameWorld world;
	private GameRenderer renderer;
	private float runTime;
	
	
	public GameScreen(){
		
		GameConstants.setConstants();
		
		world = new GameWorld();
		
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(new MenuInputProcessor(world));
		multiplexer.addProcessor(new GameInputProcessor(world));
		Gdx.input.setInputProcessor(multiplexer);
		
		 //allow for transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
		
		renderer = new GameRenderer(world);
		renderer.initButtons();
		
		world.setRenderer(renderer);
		world.loadUI();
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
	}

	@Override
	public void render(float delta) {
		runTime += delta;
		world.update(delta);
		renderer.render(delta,runTime);
	}

	@Override
	public void resize(int width, int height) {
		GameConstants.setConstants();
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
		// TODO Auto-generated method stub
		
	}

}

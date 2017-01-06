package com.mygdx.CourseDev;

import com.badlogic.gdx.Game;
import com.mygdx.GameWorld.GameScreen;
import com.mygdx.InputProcessing.AssetLoader;

public class CourseDev extends Game {
	
	@Override
	public void create(){
		AssetLoader.load();
		setScreen(new GameScreen());
	}
	
	@Override
	public void dispose(){
		super.dispose();
		AssetLoader.dispose();
	}
	
}

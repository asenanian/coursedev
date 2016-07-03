package com.mygdx.game;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.VertexAttributes.Usage;

//import com.badlogic.gdx.graphics.g3d.Environment;
//import com.badlogic.gdx.graphics.g3d.Material;
//import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
//import com.badlogic.gdx.graphics.g3d.ModelInstance;
//import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
//import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
//import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
//import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
//import com.mygdx.entities.Player;
//import com.mygdx.entities.Sphere;
import com.mygdx.managers.CameraController;
import com.mygdx.managers.GameInputProcessor;
//import com.mygdx.managers.GameInputProcessor;
import com.mygdx.managers.GameKeys;
import com.mygdx.managers.GameStateManager;

public class MyGdxGame extends ApplicationAdapter {
	public static int WIDTH, HEIGHT;
	public static PerspectiveCamera cam;
	//public static Environment environment;
	public ModelBatch modelBatch;
	public CameraController camController;
	public GameInputProcessor gameInputProcessor;
	public InputMultiplexer inputMultiplexer;
	private GameStateManager gsm;
	
	@Override
	public void create () {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		
		cam = new PerspectiveCamera(67, WIDTH, HEIGHT);
		cam.position.set(-300f, 20f, 0f);
		cam.lookAt(0,0,0);
		cam.near = 4f;
		cam.far = 500f;
		cam.update();
		
		modelBatch = new ModelBatch();
		
		gsm = new GameStateManager(); //sets game state to Play, makes player

		camController = new CameraController(cam);
		gameInputProcessor = new GameInputProcessor();
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(camController);
		inputMultiplexer.addProcessor(gameInputProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);

	}
	
	@Override
	public void resize (int width, int height) {

	}

	@Override
	public void render () {
		//clear screen to black
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glViewport(0, 0, WIDTH, HEIGHT);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        gsm.update(Gdx.graphics.getDeltaTime(), modelBatch);
        
        modelBatch.begin(cam);
        gsm.draw(modelBatch);
        modelBatch.end();
        
        camController.update();
        
		if (GameKeys.isPressed(GameKeys.SPACE)) {
			System.out.println("SPACE");
		}
		GameKeys.update();
		
		
	}
}

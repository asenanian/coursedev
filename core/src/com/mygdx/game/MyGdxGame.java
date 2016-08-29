package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.shared.MyPackets;
import com.mygdx.game.shared.stringPacket;
import com.mygdx.managers.GameInputProcessor;
import com.mygdx.managers.GameKeys;
import com.mygdx.managers.GameStateManager;
import com.mygdx.managers.ClientInterface;


import com.github.czyzby.websocket.AbstractWebSocketListener;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketHandler;
import com.github.czyzby.websocket.WebSocketHandler.Handler;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;
import com.github.czyzby.websocket.net.ExtendedNet;
import com.github.czyzby.websocket.serialization.impl.ManualSerializer;


public class MyGdxGame extends ApplicationAdapter {
	
	public static int WIDTH, HEIGHT, FLOOR, CEILING, LEFTWALL, RIGHTWALL;
	public static OrthographicCamera cam;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private GameInputProcessor gameInputProcessor;
	private InputMultiplexer inputMultiplexer;
	private GameStateManager gsm;
	private float[] mousePos;
	
	//server variables
	private ClientInterface connection;
	
	@Override
	public void create () {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		FLOOR = HEIGHT/4;
		CEILING = 3*HEIGHT/4;
		LEFTWALL = WIDTH/4;
		RIGHTWALL = 3*WIDTH/4;
		
		cam = new OrthographicCamera(WIDTH,HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2); //camera looks at this point
		cam.update();
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		
		// connect to server
		connection = new ClientInterface();
		connection.connectSocket();
		
		gsm = new GameStateManager(connection); //sets game state to Menu
		
		

		//camController = new CameraController(cam);
		gameInputProcessor = new GameInputProcessor();
		inputMultiplexer = new InputMultiplexer();
		//inputMultiplexer.addProcessor(camController);
		inputMultiplexer.addProcessor(gameInputProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
        
        //allow for transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        
        //connect to server
        

	}
	
	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void render () {
		//clear screen to black
		Gdx.gl.glClearColor(0.1f, 0, 0, 1);
		Gdx.gl.glViewport(0, 0, WIDTH, HEIGHT);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        gsm.update(Gdx.graphics.getDeltaTime());
        
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer.setProjectionMatrix(cam.combined);
        
        shapeRenderer.begin();
        gsm.drawShapes(shapeRenderer);
        shapeRenderer.end();
        
        //draw sprite layer over shapes layer
        batch.begin();
        gsm.drawSprites(batch);
        batch.end();
        
		if (GameKeys.isPressed(GameKeys.SPACE)) {
			System.out.println("SPACE");
		}
		GameKeys.update();

	}
	
	public void print(Object obj) {
		System.out.println(obj);
	}
	
	public void dispose() {
		connection.userDisconnect();
		connection.dispose();
		gsm.dispose();
	}
}

package com.mygdx.gamestates;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.entities.Player;
import com.mygdx.entities.Solid;
import com.mygdx.entities.Sphere;
import com.mygdx.entities.Wall;
import com.mygdx.managers.GameKeys;
import com.mygdx.managers.GameStateManager;

import com.mygdx.game.MyGdxGame;

public class PlayState extends GameState {
	
	private ModelBuilder modelBuilder;
	
	private int numSpheres;
	private int numWalls;
	private Sphere[] spheres;
	private Wall[] walls;
	private ArrayList<Solid> solids;
	
	private Environment environment;
	
	private int t = 0; //elapsed time
	
	public PlayState(GameStateManager gsm) {
		super(gsm);
	}
	
	public void init() {
		this.numSpheres = 20;
		this.numWalls = 6;
		modelBuilder = new ModelBuilder();
		wallsInit();
		spheresInit();
		solidsArrayInit();
		environment = new Environment();
	    //environment.set(new ColorAttribute(ColorAttribute.createAmbient(0.9f, 0.9f, 0.9f, 1f)));
		//environment.set(new ColorAttribute(ColorAttribute.createDiffuse(0.8f,0.8f,0.8f,1f)));
		//environment.set(new ColorAttribute(ColorAttribute.createSpecular(0.8f,0.8f,0.8f,1f)));
		//environment.set(new ColorAttribute(ColorAttribute.Emissive));
		//environment.add(new DirectionalLight().set(0.9f, 0.9f, 0.9f, -1f, -0.8f, -0.2f));
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.9f, 0.9f, 0.9f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, 0.5f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, -0.8f, 0.2f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, 0.5f, 0.8f));
	}

//	public void drawBackground(ModelBatch modelBatch, Environment environment) {
//		Texture spaceTexture = new Texture("space2.jpg");
//		Material spaceMaterial = new Material(TextureAttribute.createDiffuse(spaceTexture), 
//													ColorAttribute.createSpecular(1, 1, 1, 1),
//													FloatAttribute.createShininess(20f));
//		spaceMaterial.set(new ColorAttribute(ColorAttribute.AmbientLight));
//		spaceMaterial.set(new ColorAttribute(ColorAttribute.Diffuse));
//		spaceMaterial.set(new ColorAttribute(ColorAttribute.Emissive));
//		spaceMaterial.set(new ColorAttribute(ColorAttribute.Ambient));
//		final long attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
//		Model spaceModel = modelBuilder.createSphere(600f, 600f, 600f, 40, 40, spaceMaterial, attributes);
//		ModelInstance spaceInstance = new ModelInstance(spaceModel);
//		modelBatch.render(spaceInstance,environment);
//	}
	
	public void wallsInit() {
		//6 walls enclose a cube room
		walls = new Wall[numWalls];
		float[][] wallPositions = new float[numWalls][3];
		float[][] wallDims = new float[numWalls][3];
		Color[] wallColors = new Color[numWalls];
		float thickness = 10f;
		float edge = 200f;
		
		wallPositions[0] = new float[] {0,0,-edge/2}; //floor
		wallDims[0] = new float[] {edge, edge, thickness};
		wallColors[0] = null;//new Color(1f,1f,1f,1f);
		
		wallPositions[1] = new float[] {edge/2,0,0}; //right wall
		wallDims[1] = new float[] {thickness, edge, edge};
		wallColors[1] = null;//Color.WHITE.add(0,0,0,1);
		
		wallPositions[2] = new float[] {0,edge/2,0}; //forward
		wallDims[2] = new float[] {edge, thickness, edge};
		wallColors[2] = null;//Color.WHITE.add(0,0,0,1);
		
		wallPositions[3] = new float[] {-edge/2,0,0}; //left
		wallDims[3] = new float[] {thickness, edge, edge};
		wallColors[3] = null;//Color.WHITE.add(0,0,0,1);
		
		wallPositions[4] = new float[] {0,-edge/2,0}; //back
		wallDims[4] = new float[] {edge, thickness, edge};
		wallColors[4] = null;//Color.WHITE.add(0,0,0,1);
		
		wallPositions[5] = new float[] {0,0,edge/2}; //ceiling
		wallDims[5] = new float[] {edge, edge, thickness};
		wallColors[5] = null;//Color.WHITE.add(0,0,0,1);
		
		for (int i = 0; i < numWalls; i++) {
			walls[i] = new Wall(wallPositions[i], wallDims[i][0], wallDims[i][1], 
					wallDims[i][2], wallColors[i], modelBuilder);
		}
		
	}
	
	public void spheresInit() {
		spheres = new Sphere[numSpheres];
		float[][] spherePositions = new float[numSpheres][3];
		float[][] sphereVelocities = new float[numSpheres][3];
		float[] sphereMasses = new float[numSpheres];
		float[] sphereRadii = new float[numSpheres];
		
//		//init sphere 1
//		spherePositions[0] = new float[] {-60,0,10};
//		sphereVelocities[0] = new float[] {30,-30,0};
//		sphereMasses[0] = 10f;
//		sphereRadii[0] = 20f;
//
//		//init sphere 2
//		spherePositions[1] = new float[] {60,0,-10};
//		sphereVelocities[1] = new float[] {-200,40,0};
//		sphereMasses[1] = 10f;
//		sphereRadii[1] = 20f;
//		
//		//init sphere 3
//		spherePositions[2] = new float[] {-40,-40,7};
//		sphereVelocities[2] = new float[] {20,10,30};
//		sphereMasses[2] = 5f;
//		sphereRadii[2] = 10f;
//		
//		//init sphere 4
//		spherePositions[3] = new float[] {-80,-80,-10};
//		sphereVelocities[3] = new float[] {5,5,50};
//		sphereMasses[3] = 10f;
//		sphereRadii[3] = 20f;
//		
//		//init sphere 5
//		spherePositions[4] = new float[] {80,80,90};
//		sphereVelocities[4] = new float[] {-30,-30,-30};
//		sphereMasses[4] = 2.5f;
//		sphereRadii[4] = 5f;
//		
		for (int i = 0; i < numSpheres; i++) {
			if (sphereMasses[i] == 0) { 
				//sphere hasn't been assigned attributes yet
				sphereMasses[i] = (float)(Math.random())*20+2f;
				sphereRadii[i] = sphereMasses[i]*2;
				float bounds = 190-2*sphereRadii[i];
				spherePositions[i] = new float[] {(float)(Math.random()*2-1)*bounds,
												  (float)(Math.random()*2-1)*bounds,
												  (float)(Math.random()*2-1)*bounds};
				//velocities only go up
				sphereVelocities[i] = new float[] {(float)(Math.random()*2-1)*100,
												   (float)(Math.random()*2-1)*100,
												   (float)(Math.random()*1)*50};
			}
			spheres[i] = new Sphere(spherePositions[i],sphereVelocities[i], 
				sphereMasses[i], sphereRadii[i], modelBuilder);
			//get rid of spheres in weird places
			if (!spheres[i].inLegalPosition(spheres,walls)) {
				spheres[i] = null;
			}
		}
		
	}
	
	
	
	public void solidsArrayInit() {
		solids = new ArrayList<Solid>();
		for (Sphere sphere : spheres) {
			if (sphere != null) solids.add(sphere);
		}
		for (Wall wall : walls) {
			if (wall != null) solids.add(wall);
		}
	}
	
	public void update(float dt, ModelBatch modelBatch) {
		//System.out.println("PLAY STATE UPDATING");
		handleInput();
		for (Sphere sphere : spheres) {
			if (sphere != null) {
				sphere.update(dt);
				sphere.checkInOtherSpheres(spheres);
				sphere.draw(modelBatch, environment);
			}
		}
		handleCollisions(dt);
	}

	public void handleCollisions(float dt) {
		for (Sphere sphere : spheres) {
			if (sphere != null) {
				for (Solid other : solids) {
					if (sphere.equals(other)) continue;
					if (sphere.collided(other)) {
						sphere.processCollision(other);
					}
				}
			}
		}
	}
	public void draw(ModelBatch modelBatch) {
		//System.out.println("PLAY STATE DRAWING");
		//if (t == 0) drawBackground(modelBatch, environment);
		for (Sphere sphere : spheres) {
			if (sphere != null) {
				sphere.draw(modelBatch,environment);
			}
			
		}
		//System.out.println(walls.length);
		for (Wall wall : walls) {
			//if (wall == null) System.out.println(wall);
			wall.draw(modelBatch,environment);
		}
		t += 1; //elapsed time
	}
	public void handleInput() {
		System.out.println("space???");
		if (GameKeys.isPressed(GameKeys.SPACE)) {
			System.out.println("yep, space");
			init();
		}
//		for (Sphere sphere : spheres) {
//			sphere.setLeft(GameKeys.isDown(GameKeys.LEFT));
//			sphere.setRight(GameKeys.isDown(GameKeys.RIGHT));
//			sphere.setUp(GameKeys.isDown(GameKeys.UP));
//		}
	}
	public void dispose() {}
	
	
}

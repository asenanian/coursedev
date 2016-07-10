package com.mygdx.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.mygdx.math.Vect3;

public class Wall extends Solid {
	private float width;
	private float height;
	private float depth;
	private float thickness;
	private Vect3[] corners;
	private Color color;
	private Texture texture;
	private Model model;
	private ModelInstance instance;
	private String imgInternalPath = "Spheres/space3.png";
	
	public Wall(float[] position, float width, float height, float depth, 
			Color color, ModelBuilder modelBuilder) {
		super("wall");
		this.x = position[0];
		this.y = position[1];
		this.z = position[2];
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.thickness = Math.min(width, Math.min(height, depth));
		
		//account for overlap
		if (width != thickness) this.width += thickness;
		if (height != thickness) this.height += thickness;
		if (depth != thickness) this.depth += thickness;
		this.corners = makeCorners();
		this.color = color;
		this.texture = new Texture(Gdx.files.internal(imgInternalPath));
		this.model = buildWallModel(modelBuilder);
		instance = new ModelInstance(this.model);
		instance.transform.translate(new Vect3(x,y,z));
		instance.calculateTransforms();
	}
	
	public void draw(ModelBatch modelBatch, Environment environment) {
		modelBatch.render(instance,environment);
	}

	public float getWidth() {
		return this.width;
	}
	public float getHeight() {
		return this.height;
	}
	public float getDepth() {
		return this.depth;
	}
	public float getThickness() {
		return this.thickness;
	}
	
	public Vect3[] getCorners() {
		return corners;
	}
	
//	public Vect3[] getMiddlePoints() {
//		//{front,back} points in middle of wall
//		if (this.thickness == this.width) {
//			
//		} else if (this.thickness == this.height) {
//			
//		} else if (this.thickness == this.depth) {
//			
//		}
//	}
	
	private Vect3[] makeCorners() {
		/*  (axes)
		 *      z
		 * 		z
		 * 		z
		 * 		z
		 * 		0 yyyyyyyyyyy
		 * 	   x
		 *    x
		 *   x
		 *  x
		 */
		Vect3[] corners = new Vect3[8];
		for (int i = 0; i < 8; i++) {
			corners[i] = new Vect3(width/2,height/2,depth/2);
			if (thickness == depth) {
				//horizontal wall
				if (i < 4) corners[i].z *= -1;
				if (i == 1 || i == 3 || i == 5 || i == 7) corners[i].x *= -1;
				if (i == 2 || i == 3 || i == 6 || i == 7) corners[i].y *= -1;
			} else if (thickness == width) {
				//vertical wall along y axis
				if (i < 4) corners[i].x *= -1;
				if (i == 1 || i == 3 || i == 5 || i == 7) corners[i].z *= -1;
				if (i == 2 || i == 3 || i == 6 || i == 7) corners[i].y *= -1;
			} else if (thickness == height) {
				//vertical wall along x axis
				if (i < 4) corners[i].y *= -1;
				if (i == 1 || i == 3 || i == 5 || i == 7) corners[i].z *= -1;
				if (i == 2 || i == 3 || i == 6 || i == 7) corners[i].x *= -1;
			}
		}
		for (int i = 0; i < 8; i++) {
			corners[i].x += this.x;
			corners[i].y += this.y;
			corners[i].z += this.z;
		}
		return corners;
	}
	
	private Model buildWallModel(ModelBuilder modelBuilder) {
		final Material material;
		float transparency = 0.5f;
		if (color == null) {
			color = new Color(1f,1f,1f,1f);
			material = new Material(TextureAttribute.createDiffuse(texture));
										//ColorAttribute.createSpecular(color),
										//FloatAttribute.createShininess(20f));
		} else {
			//no texture
			material = new Material(ColorAttribute.createDiffuse(color));
		}
		
		material.set(new ColorAttribute(ColorAttribute.createAmbient(color)));
						//new ColorAttribute(ColorAttribute.createSpecular(color)));
						//new ColorAttribute(ColorAttribute.createReflection(color)));
						//FloatAttribute.createShininess(20f));
			
		//make transparent
		//if (color == null) color = new Color(1f,1f,1f,1f);
		//material.set(new ColorAttribute(ColorAttribute.Diffuse, color));
        material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, transparency));
        
		final long attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
		model = modelBuilder.createBox(width, height, depth, material, attributes);
		return model;
	}

	
}

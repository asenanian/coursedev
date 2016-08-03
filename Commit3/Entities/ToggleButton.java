package com.mygdx.entities;

import com.badlogic.gdx.graphics.Texture;

public class ToggleButton extends Button {
	private String internalPath1;
	private String internalPath2;
	//private String internalPath;
	public ToggleButton(String internalPath1, String internalPath2,
					float[] pos, float width, float height) {
		super(internalPath1,pos,width,height);
		this.internalPath1 = internalPath1;
		this.internalPath2 = internalPath2;
	}
	public ToggleButton(String internalPath1, String internalPath2,
			float x, float y, float width, float height) {
		this(internalPath1,internalPath2,new float[] {x,y},width,height);
	}
	public void toggleImage() {
		if (this.internalPath.equals(this.internalPath1)) {
			this.internalPath = this.internalPath2;
		} else {
			this.internalPath = this.internalPath1;
		}
		this.texture = new Texture(internalPath);
		this.setClicked(false);
	}

}

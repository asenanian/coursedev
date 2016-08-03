package com.mygdx.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.game.MyGdxGame;

public class Text extends Rect implements MultiDraw {
	private String string;
	private GlyphLayout layout;
	private BitmapFont font;
	public Text(String str, BitmapFont font, float centerX, float centerY) {
		super(Color.BLACK, false); //values will be set later
		setCenter(centerX,centerY);
		this.string = str;
		this.font = font;
		init();
	}
	public Text(String str, BitmapFont font, float[] center) {
		this(str,font,center[0],center[1]);
	}
	public Text(String str, BitmapFont font) {
		this(str,font,0,0);
	}
	public Text(String str) {
		this(str,new BitmapFont(),0,0);
	}
	public void init() {
		this.layout = new GlyphLayout();
		//print(this.font);
		this.layout.setText(this.font, this.string);
		this.setWidth(this.layout.width);
		this.setHeight(this.layout.height);
	}

	public void drawSprites(SpriteBatch batch) {
		//draw text centered at this.pos
		//print("DRAWING TEXT");
		//print("left = "+this.left+", top = "+this.top);
		this.font.draw(batch, this.layout, this.left, this.top);
	}
	public void drawShapes(ShapeRenderer shapeRenderer) {
		this.draw(shapeRenderer);
	}
	public String getName() {
		return this.string;
	}
	public BitmapFont getFont() {
		return this.font;
	}
}

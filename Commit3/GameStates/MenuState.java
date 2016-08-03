package com.mygdx.gamestates;

import java.awt.Font;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.entities.MultiDraw;
import com.mygdx.entities.Text;
import com.mygdx.game.MyGdxGame;
import com.mygdx.managers.GameStateManager;

public class MenuState extends GameState implements MultiDraw {
	private int numItems;
	private Text[] items;
	private Text title;
	private BitmapFont itemFont;
	private BitmapFont titleFont;
	
	public MenuState(GameStateManager gsm) {
		super(gsm);
	}
	
	@Override
	public void init() {
		numItems = 2;
		initFonts();
		initTitle();
		initItems();
	}

	private void initTitle() {
		title = new Text("MENU",titleFont,MyGdxGame.WIDTH/2,MyGdxGame.HEIGHT/2+100);
	}
	private void initItems() {
		items = new Text[numItems];
		items[0] = new Text("Play",itemFont,MyGdxGame.WIDTH/2,MyGdxGame.HEIGHT/2);
		items[1] = new Text("Quit",itemFont,MyGdxGame.WIDTH/2,MyGdxGame.HEIGHT/2-50);
	}
	
	private void initFonts() {
		titleFont = new BitmapFont();
		titleFont.setColor(0.1f,0.1f,1.0f,1f);
		titleFont.getData().setScale(1.9f);
		
		itemFont = new BitmapFont();
		itemFont.setColor(Color.RED);
	}
	
	@Override
	public void update(float dt) {
		//maybe draw a menu background?
		
		handleInput();

	}

	@Override
	public void drawSprites(SpriteBatch batch) {
		//draw title
		//print(title);
		title.drawSprites(batch);
		//draw text buttons
		for(int i = 0; i < numItems; i++) {
			items[i].drawSprites(batch);
		}

	}
	@Override
	public void drawShapes(ShapeRenderer shapeRenderer) {
//		shapeRenderer.set(ShapeType.Line);
//		shapeRenderer.setColor(Color.WHITE);
		title.drawShapes(shapeRenderer);
		//draw text buttons
		for(int i = 0; i < numItems; i++) {
			items[i].drawShapes(shapeRenderer);
		}
	}

	@Override
	public void handleInput() {
		int i = clickedOnItem();
		if (i == -1) return; //didn't click on item
		
		//item i was clicked...
		if (items[i].getName().equals("Play")) {
			//switch to PLAY state
			gsm.setState(GameStateManager.PLAY);
		} else if (items[i].getName().equals("Quit")) {
			Gdx.app.exit(); //quit game
		}
	}
	
	private int clickedOnItem() {
		//return index of item in items array (or -1 if false)
		if (Gdx.input.isTouched()) {
			float[] mousePos = new float[2];
			mousePos[0] = Gdx.input.getX();
			mousePos[1] = MyGdxGame.HEIGHT - Gdx.input.getY(); //height from bottom
			for (int i = 0; i < numItems; i++) {
				if (items[i].containsPos(mousePos)) {
					return i;
				}
			}
		}
		return -1;
	}

	public void print(Object obj) {
		System.out.println(obj);
	}


	@Override
	public void dispose() {
		print("disposing...");
	}
}

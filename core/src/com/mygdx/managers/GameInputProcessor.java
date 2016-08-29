package com.mygdx.managers;

import com.badlogic.gdx.Input.Keys;
import com.mygdx.entities.Point;
import com.mygdx.game.TestVerlet;
import com.badlogic.gdx.InputAdapter;

public class GameInputProcessor extends InputAdapter {
	
	@Override
	public boolean keyDown(int k) {
		if (k == Keys.UP) {
			GameKeys.setKey(GameKeys.UP, true);
		}
		if (k == Keys.LEFT) {
			GameKeys.setKey(GameKeys.LEFT, true);
		}
		if (k == Keys.DOWN) {
			GameKeys.setKey(GameKeys.DOWN, true);
		}
		if (k == Keys.RIGHT) {
			GameKeys.setKey(GameKeys.RIGHT, true);
		}
		if (k == Keys.ENTER) {
			GameKeys.setKey(GameKeys.ENTER, true);
		}
		if (k == Keys.SPACE) {
			GameKeys.setKey(GameKeys.SPACE, true);
		}
		if (k == Keys.ESCAPE) {
			GameKeys.setKey(GameKeys.ESCAPE, true);
		}
		if (k == Keys.SHIFT_LEFT || k == Keys.SHIFT_RIGHT) {
			GameKeys.setKey(GameKeys.SHIFT, true);
		}
		if (k == Keys.DEL) {
			GameKeys.setKey(GameKeys.DEL, true);
		}
		if (k == Keys.P) {
			GameKeys.setKey(GameKeys.P, true);
		}
		if (k == Keys.R) {
			GameKeys.setKey(GameKeys.R, true);
		}
		if (k == Keys.M) {
			GameKeys.setKey(GameKeys.M, true);
		}
		if (k == Keys.Z) {
			GameKeys.setKey(GameKeys.Z, true);
		}
		if (k == Keys.C) {
			GameKeys.setKey(GameKeys.C, true);
		}
		return true;
	}
	@Override
	public boolean keyUp(int k) {
		if (k == Keys.UP) {
			GameKeys.setKey(GameKeys.UP, false);
		}
		if (k == Keys.LEFT) {
			GameKeys.setKey(GameKeys.LEFT, false);
		}
		if (k == Keys.DOWN) {
			GameKeys.setKey(GameKeys.DOWN, false);
		}
		if (k == Keys.RIGHT) {
			GameKeys.setKey(GameKeys.RIGHT, false);
		}
		if (k == Keys.ENTER) {
			GameKeys.setKey(GameKeys.ENTER, false);
		}
		if (k == Keys.SPACE) {
			GameKeys.setKey(GameKeys.SPACE, false);
		}
		if (k == Keys.ESCAPE) {
			GameKeys.setKey(GameKeys.ESCAPE, false);
		}
		if (k == Keys.SHIFT_LEFT || k == Keys.SHIFT_RIGHT) {
			GameKeys.setKey(GameKeys.SHIFT, false);
		}
		if (k == Keys.DEL) {
			GameKeys.setKey(GameKeys.DEL, false);
		}
		if (k == Keys.P) {
			GameKeys.setKey(GameKeys.P, false);
		}
		if (k == Keys.R) {
			GameKeys.setKey(GameKeys.R, false);
		}
		if (k == Keys.M) {
			GameKeys.setKey(GameKeys.M, false);
		}
		if (k == Keys.Z) {
			GameKeys.setKey(GameKeys.Z, false);
		}
		if (k == Keys.C) {
			GameKeys.setKey(GameKeys.C, false);
		}
		
		return true;
	}
	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		GameKeys.setKey(GameKeys.MOUSEUP, true);
		GameKeys.setKey(GameKeys.MOUSEDOWN, false);
		return true; // return true to indicate the event was handled
	}
	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		GameKeys.setKey(GameKeys.MOUSEUP, false);
		GameKeys.setKey(GameKeys.MOUSEDOWN, true);
		return true; // return true to indicate the event was handled
	}
	
	
}

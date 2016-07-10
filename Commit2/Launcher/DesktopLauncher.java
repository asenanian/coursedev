package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Basic3DTest;
import com.mygdx.game.TestVerlet;
import com.mygdx.game.Verlet3D;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Physics Game";
		config.useGL30 = false;
		config.width = 800;//Verlet3D.WIDTH;
		config.height = 700;//Verlet3D.HEIGHT;
		config.depth = 800;//Verlet3D.DEPTH;
		//config.foregroundFPS = 10;
		//new LwjglApplication(new Verlet3D(), config);
		//new LwjglApplication(new TestVerlet(), config);
		new LwjglApplication(new MyGdxGame(), config);
	}
	
}
 
package com.mygdx.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import com.mygdx.game.MyGdxGame;
import com.github.czyzby.websocket.GwtWebSockets;


public class HtmlLauncher extends GwtApplication {
	
        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(960, 640);
        }

        @Override
        public ApplicationListener createApplicationListener () {
            GwtWebSockets.initiate();
        	return new MyGdxGame();
        }
}
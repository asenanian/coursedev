package com.mygdx.server;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.github.czyzby.websocket.serialization.impl.ManualSerializer;
import com.mygdx.game.shared.MyPackets;
import com.mygdx.game.shared.circlePacket;
import com.mygdx.game.shared.stringPacket;

@ServerEndpoint("/actions")
public class DeviceWebSocketServer {
	
	private DeviceSessionHandler sessionHandler = new DeviceSessionHandler();

    @OnOpen
    public void open(Session session) {
    	sessionHandler.addSession(session);
    }

    @OnClose
    public void close(Session session) {
        sessionHandler.removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(ByteBuffer message, Session session) {
    	if(!sessionHandler.handleMessage(message, session)){
    		System.out.println("Packet not recieved");
    	}
    }
}    
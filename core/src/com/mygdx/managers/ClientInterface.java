package com.mygdx.managers;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketHandler;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.WebSocketHandler.Handler;
import com.github.czyzby.websocket.net.ExtendedNet;
import com.github.czyzby.websocket.serialization.impl.ManualSerializer;
import com.mygdx.game.shared.MyPackets;
import com.mygdx.game.shared.actionPacket;
import com.mygdx.game.shared.circlePacket;
import com.mygdx.game.shared.stringPacket;

public class ClientInterface {
	
	private WebSocket socket;
	private String userName;
	private int id = 1;
	
	public ClientInterface(){}

	public void connectSocket(){
		try{
			socket = WebSockets.newSocket("ws://localhost:8020/websockets/actions");
			socket.addListener(getListener());
			//Implement manual serializer. Replaces JSON serializer.
			final ManualSerializer serializer = new ManualSerializer();
			socket.setSerializer(serializer);
			//Registering all expected packets
			MyPackets.register(serializer);
	        socket.connect();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
    private WebSocketListener getListener() {
    	final WebSocketHandler handler = new WebSocketHandler();
    	// registering string handler
    	handler.registerHandler(stringPacket.class, new Handler<stringPacket>(){
    		@Override
    		public boolean handle(final WebSocket socket, final stringPacket packet){
    	        System.out.println("Received packet: " + packet.getValue());
    			return true;
    		}
    		
    	});
    	// registering circle handler
    	handler.registerHandler(circlePacket.class, new Handler<circlePacket>(){
    		@Override
    		public boolean handle(final WebSocket socket, final circlePacket packet){
    	        //TODO add circles for each circle packet recieved.
    			return true;
    		}
    		
    	});
    	return handler;
    }
    
    public void userConnect(String value){
    	final actionPacket packet = new actionPacket("add",value);
    	socket.send(packet);
    }
    
    public void userDisconnect(){
    	final actionPacket packet = new actionPacket("remove",""+id);
    }
    
    public void sendPacket(String value){
    	final stringPacket packet = new stringPacket(value);
    	socket.send(packet);
    }
    
    public void dispose(){
    	WebSockets.closeGracefully(socket);
    }
}

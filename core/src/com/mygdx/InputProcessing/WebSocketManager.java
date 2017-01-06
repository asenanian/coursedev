package com.mygdx.InputProcessing;

import java.util.LinkedList;
import java.util.Queue;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketHandler;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.WebSocketHandler.Handler;
import com.github.czyzby.websocket.serialization.impl.ManualSerializer;
import com.mygdx.Entities.GameObjects.*;
import com.mygdx.XMLService.Beans.CircleBean;
import com.mygdx.game.shared.ActionPacket;
import com.mygdx.game.shared.CirclePacket;
import com.mygdx.game.shared.MyPackets;
import com.mygdx.game.shared.StringPacket;

public class WebSocketManager {
	
	private WebSocket socket;
	private String userName;
	private int id;
	private boolean readyToCreate = true;
	private Queue<Circle> circles = new LinkedList<Circle>();
	
	public WebSocketManager(){}

	public void connectSocket(){
		
		try{
			socket = WebSockets.newSocket("ws://localhost:8020/websockets/actions");
			socket.addListener(getListener());
			
			//Implement manual serializer. Replaces JSON serializer.
			final ManualSerializer serializer = new ManualSerializer();
			socket.setSerializer(serializer);
			
			//Registering all expected packets
			MyPackets.register(serializer);
			
			// connect to socket
	        socket.connect();
	        
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
    private WebSocketListener getListener() {
    	// WebSocketHandler is an implementation of WebSocketListener that uses the current serializer
    	// to create objects from received raw data.
    	final WebSocketHandler handler = new WebSocketHandler();
    	
    	// registering action handler
    	handler.registerHandler(ActionPacket.class, new Handler<ActionPacket>(){
    		@Override
    		public boolean handle(final WebSocket socket, final ActionPacket packet){
    	        if(packet.getAction() == "add"){
    	        	id = Integer.parseInt(packet.getValue());
    	        	sendMessage("Id registered as: " + id);
    	        }
    	        if(packet.getAction() == "restarting"){
    	        	//TODO notify user that a player has restarted
    	        }
    	        if(packet.getAction() == "readyToCreate"){
    	        	readyToCreate = true;
    	        }
    			return true;
    		}
    		
    	});
    	// registering string handler
    	handler.registerHandler(StringPacket.class, new Handler<StringPacket>(){
    		@Override
    		public boolean handle(final WebSocket socket, final StringPacket packet){
    	        System.out.println("Received packet: " + packet.getValue());
    			return true;
    		}
    		
    	});
    	// registering circle handler
    	handler.registerHandler(CirclePacket.class, new Handler<CirclePacket>(){
    		@Override
    		public boolean handle(final WebSocket socket, final CirclePacket packet){
    			sendMessage("Recieved packet " + packet.toString());
    			Circle circle = new Circle.Constructor(packet.getPos(),packet.getRadius(),packet.getPinned()).Construct();
    			try{
    				circles.offer(circle);
    				sendMessage("Processed circle.");
    			}
    			catch(Exception e){
    				sendMessage(e.getMessage());
    				sendMessage("Circle not processed.");
    			}
    			return true;
    		}    		
    	});
    	
    	return handler;
    }
    
    public Circle getCircles(){
    	if(!circles.isEmpty()){
    		return circles.poll();
    	}
    	else return null;
    }
    
    public void setUsername(String value){
    	this.userName = value;
    }
    
    public String getUsername(){
    	return userName;
    }
    
    public void userConnect(String value){
    	sendAction("add",value);
    }
    
    public void userDisconnect(){
    	sendAction("remove",""+id);
    }
    
    public void sendMessage(String value){
    	final StringPacket packet = new StringPacket(value);
    	socket.send(packet);
    }
    
    public void sendAction(String action, String value){
    	final ActionPacket packet = new ActionPacket(action, value);
    	socket.send(packet);
    }
    
    public void sendCircle(float position[], float radius, boolean pinned){
    	final CirclePacket packet = new CirclePacket(position,radius,pinned);
    	socket.send(packet);
    }
    
    public void sendCircle(CircleBean circleBean){
    	final CirclePacket packet = new CirclePacket(circleBean.getPos(),circleBean.getRadius(),circleBean.getPinned());
    	socket.send(packet);
    }
    
    public void dispose(){
    	WebSockets.closeGracefully(socket);
    }
    
    public boolean isReadyToCreate(){
    	return readyToCreate;
    }
    
}

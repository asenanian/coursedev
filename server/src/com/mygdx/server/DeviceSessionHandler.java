package com.mygdx.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.Session;

import com.github.czyzby.websocket.serialization.impl.ManualSerializer;
import com.mygdx.game.shared.MyPackets;
import com.mygdx.game.shared.actionPacket;
import com.mygdx.game.shared.circlePacket;
import com.mygdx.game.shared.stringPacket;

public class DeviceSessionHandler {
	private int deviceId = 0;
	public final ManualSerializer serializer = new ManualSerializer();
    private final Set<Session> sessions = new HashSet<Session>();
    private final Set<Device> devices = new HashSet<Device>();
    
    DeviceSessionHandler(){
    	MyPackets.register(serializer);
    }
    
    public void addSession(Session session) {
        sessions.add(session);
        for (Device device : devices) {
            sendToSession(session, createAddMessage(device));
        }
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    
    public List<Device> getDevices() {
        return new ArrayList<Device>(devices);
    }

    // saves added device in device list and notifies all connected devices of addition
    public void addDevice(Device device) {
        device.setId(deviceId);
        devices.add(device);
        deviceId++;
        sendToAllConnectedSessions(createAddMessage(device));
    }

    public void removeDevice(int id) {
        Device device = getDeviceById(id);
        if (device != null) {
            devices.remove(device);
            stringPacket removeMessage = new stringPacket("remove");
            sendToAllConnectedSessions(ByteBuffer.wrap(serializer.serialize(removeMessage)));
        }
    }

    private Device getDeviceById(int id) {
        for (Device device : devices) {
            if (device.getId() == id) {
                return device;
            }
        }
        return null;
    }

    // returns serialized message that a device was added
    private ByteBuffer createAddMessage(Device device) {
    	stringPacket addMessage = new stringPacket("add");
    	return ByteBuffer.wrap(serializer.serialize(addMessage));
    }

    private void sendToAllConnectedSessions(ByteBuffer message) {
        for (Session session : sessions) {
            sendToSession(session, message);
        }
    }

    private void sendToSession(Session session, ByteBuffer message) {
        try {
            session.getBasicRemote().sendBinary(message);
        } catch (IOException ex) {
            sessions.remove(session);
            Logger.getLogger(DeviceSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
     * Message handling
     */
    public boolean handleMessage(ByteBuffer message, Session session){
    	final Object request = serializer.deserialize(message.array());
        if(request instanceof actionPacket){
        	return handleActionPacket((actionPacket)request,session);
        }
        
        if(request instanceof circlePacket){
        	return handleCirclePacket((circlePacket)request,session);
        	//TODO broadcast creation of new circle
        }
        return false;
    }
    
    // action handler
    private boolean handleActionPacket(actionPacket message, Session session){
    	if("add".equals(message.getAction())){
            Device device = new Device();
            device.setName(message.getValue());
            addDevice(device);
            return true;
    	}
    	
    	if("remove".equals(message.getAction())){
    		int id = Integer.parseInt(message.getValue());
    		removeDevice(id);
    		return true;
    	}
    	return false;
    }
    
    // circle handler
    private boolean handleCirclePacket(circlePacket circle, Session session){
    	return true;
    }
}
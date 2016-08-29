package com.mygdx.game.shared;

import com.github.czyzby.websocket.serialization.Transferable;
import com.github.czyzby.websocket.serialization.impl.Deserializer;
import com.github.czyzby.websocket.serialization.impl.Serializer;

public class circlePacket implements Transferable<circlePacket> {
    private float position[];
    private float radius;
    private boolean pinned;

    public circlePacket() {
    }

    public circlePacket(final float position[], final float radius, final boolean pinned) {
        this.position = position;
        this.radius = radius;
        this.pinned = pinned;
    }

    @Override
    public void serialize(final Serializer serializer) {
        serializer.serializeFloatArray(position).serializeFloat(radius).serializeBoolean(pinned);
    }

    @Override
    public circlePacket deserialize(final Deserializer deserializer) {
        return new circlePacket(deserializer.deserializeFloatArray(), deserializer.deserializeFloat(),deserializer.deserializeBoolean());
    }
    
    public float[] getPos(){
    	return position;
    }
    
    public float getRadius(){
    	return radius;
    }
    
    public boolean getPinned(){
    	return pinned;
    }
    public void setPos(float position[]){
    	this.position = position;
    }
    
    public void setRadius(float radius){
    	this.radius = radius;
    }
    
    public void setPinned(boolean pinned){
    	this.pinned = pinned;
    }
}
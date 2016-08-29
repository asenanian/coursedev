package com.mygdx.game.shared;

import com.github.czyzby.websocket.serialization.Transferable;
import com.github.czyzby.websocket.serialization.impl.Deserializer;
import com.github.czyzby.websocket.serialization.impl.Serializer;

public class actionPacket implements Transferable<actionPacket> {
    private String action;
    private String value;
    
    public actionPacket() {
    }

    public actionPacket(final String action,final String value) {
        this.action = action;        
        this.value = value;
    }

    @Override
    public void serialize(final Serializer serializer) {
        serializer.serializeString(action).serializeString(value);
    }

    @Override
    public actionPacket deserialize(final Deserializer deserializer) {
        return new actionPacket(deserializer.deserializeString(),deserializer.deserializeString());
    }

    public String getValue() {
        return value;
    }
    
    public String getAction() {
        return action;
    }

    public void setValue(final String value) {
        this.value = value;
    }
    
    public void setAction(final String action) {
        this.action = action;
    }

}
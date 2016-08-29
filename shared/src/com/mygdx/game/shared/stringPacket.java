package com.mygdx.game.shared;

import com.github.czyzby.websocket.serialization.Transferable;
import com.github.czyzby.websocket.serialization.impl.Deserializer;
import com.github.czyzby.websocket.serialization.impl.Serializer;

public class stringPacket implements Transferable<stringPacket> {
    private String value;
    
    public stringPacket() {
    }

    public stringPacket(final String value) {
        this.value = value;        
    }

    @Override
    public void serialize(final Serializer serializer) {
        serializer.serializeString(value);
    }

    @Override
    public stringPacket deserialize(final Deserializer deserializer) {
        return new stringPacket(deserializer.deserializeString());
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

}
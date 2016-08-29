package com.mygdx.game.shared;

import com.github.czyzby.websocket.serialization.impl.ManualSerializer;

/** Utility class. Allows to easily register packets in the same order on both client and server.
 *
 * @author MJ */
public class MyPackets {
	
	private void MyPackets(){}
	
	public static void register(final ManualSerializer serializer){
		serializer.register(new stringPacket());
		serializer.register(new actionPacket());
	}
}

package com.mygdx.server;

import com.mygdx.game.shared.MyPackets;
import com.mygdx.game.shared.circlePacket;
import com.mygdx.game.shared.stringPacket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.github.czyzby.websocket.serialization.Transferable;
import com.github.czyzby.websocket.serialization.impl.ManualSerializer;

import org.glassfish.tyrus.server.Server;

public class ServerLauncher {

    public static void main(final String... args) throws Exception {
    	runServer();
    }
    
    public static void runServer() {
        Server server = new Server("localhost", 8020, "/websockets", DeviceWebSocketServer.class);
 
        try {
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please press a key to stop the server.");
            reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            server.stop();
        }
    }

}
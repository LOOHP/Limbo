package com.loohp.limbo.server;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.server.ClientConnection.ClientState;
import com.loohp.limbo.server.packets.PacketPlayOutKeepAlive;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class KeepAliveSender extends Thread {

    private final Random random;

    public KeepAliveSender() {
        random = new Random();
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (ClientConnection client : Limbo.getInstance().getServerConnection().getClients()) {
                    if (client.getClientState() != null && client.getClientState().equals(ClientState.PLAY)) {
                        try {
                            PacketPlayOutKeepAlive packet = new PacketPlayOutKeepAlive(random.nextLong());
                            client.setLastKeepAlivePayLoad(packet.getPayload());
                            client.sendPacket(packet);
                        } catch (IOException ignore) {
                        }
                    }
                }
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

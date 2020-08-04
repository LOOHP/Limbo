package com.loohp.limbo.Server;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.Server.ClientConnection.ClientState;
import com.loohp.limbo.Server.Packets.PacketPlayOutKeepAlive;
import com.loohp.limbo.Utils.DataTypeIO;

public class KeepAliveSender extends Thread {
	
	private Random random;
	
	public KeepAliveSender() {
		random = new Random();
		start();
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				for (ClientConnection client : Limbo.getInstance().getServerConnection().getClients()) {
					if (client.getClientState().equals(ClientState.PLAY)) {
						try {
							PacketPlayOutKeepAlive packet = new PacketPlayOutKeepAlive(random.nextLong());
							byte[] packetByte = packet.serializePacket();
							DataTypeIO.writeVarInt(client.output, packetByte.length);
							client.output.write(packetByte);
							client.setLastKeepAlivePayLoad(packet.getPayload());
						} catch (IOException ignore) {}
					}
				}
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

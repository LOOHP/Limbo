package com.loohp.limbo.network.protocol.packets;

import java.io.IOException;

public abstract class PacketOut extends Packet {
	
	public abstract byte[] serializePacket() throws IOException;

}

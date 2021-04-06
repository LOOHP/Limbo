package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutKeepAlive extends PacketOut {
	
	long payload;
	
	public PacketPlayOutKeepAlive(long payload) {
		this.payload = payload;
	}
	
	public long getPayload() {
		return payload;
	}
	
	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		output.writeLong(payload);
		
		return buffer.toByteArray();
	}

}

package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketStatusOutPong extends PacketOut {
	
	private long payload;
	
	public PacketStatusOutPong(long payload) {
		this.payload = payload;
	}

	public long getPayload() {
		return payload;
	}
	
	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getStatusOut().get(getClass()));
		output.writeLong(payload);
		
		return buffer.toByteArray();
	}
	
}

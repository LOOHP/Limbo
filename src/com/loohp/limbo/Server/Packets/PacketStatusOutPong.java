package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketStatusOutPong extends Packet {
	
	private long payload;
	
	public PacketStatusOutPong(long payload) {
		this.payload = payload;
	}

	public long getPayload() {
		return payload;
	}
	
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getStatusOut().get(getClass()));
		output.writeLong(payload);
		
		return buffer.toByteArray();
	}
	
}

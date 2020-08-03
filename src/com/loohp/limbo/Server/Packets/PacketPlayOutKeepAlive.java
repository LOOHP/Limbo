package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutKeepAlive extends Packet {
	
	long payload;
	
	public PacketPlayOutKeepAlive(long payload) {
		this.payload = payload;
	}
	
	public long getPayload() {
		return payload;
	}
	
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		output.writeLong(payload);
		
		return buffer.toByteArray();
	}

}

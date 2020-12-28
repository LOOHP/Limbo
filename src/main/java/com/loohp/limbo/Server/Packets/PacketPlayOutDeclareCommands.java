package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutDeclareCommands extends PacketOut {
	
	private byte[] data;
	
	public PacketPlayOutDeclareCommands(byte[] data) {
		this.data = data;
	}
	
	public byte[] getData() {
		return data;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		output.write(data);
		
		return buffer.toByteArray();
	}

}

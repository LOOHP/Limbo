package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;

public class PacketPlayOutDisconnect extends PacketOut {
	
	private String jsonReason;

	public PacketPlayOutDisconnect(String jsonReason) {
		this.jsonReason = jsonReason;
	}

	public String getJsonReason() {
		return jsonReason;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, jsonReason, StandardCharsets.UTF_8);
		
		return buffer.toByteArray();
	}

}

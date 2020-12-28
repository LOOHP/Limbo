package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.Utils.DataTypeIO;

public class PacketLoginOutDisconnect extends PacketOut {
	
	private String jsonReason;

	public PacketLoginOutDisconnect(String jsonReason) {
		this.jsonReason = jsonReason;
	}

	public String getJsonReason() {
		return jsonReason;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getLoginOut().get(getClass()));
		DataTypeIO.writeString(output, jsonReason, StandardCharsets.UTF_8);
		
		return buffer.toByteArray();
	}

}

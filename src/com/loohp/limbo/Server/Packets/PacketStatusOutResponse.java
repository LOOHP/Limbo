package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.Utils.DataTypeIO;

public class PacketStatusOutResponse extends Packet {
	
	private String json;
	
	public PacketStatusOutResponse(String json) {
		this.json = json;
	}
	
	public String getJson() {
		return json;
	}
	
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getStatusOut().get(getClass()));
		DataTypeIO.writeString(output, json, StandardCharsets.UTF_8);
		
		return buffer.toByteArray();
	}

}

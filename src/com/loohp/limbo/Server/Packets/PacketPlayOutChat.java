package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.loohp.limbo.Utils.DataTypeIO;

public class PacketPlayOutChat extends Packet {
	
	private String json;
	private int position;
	private UUID sender;
	
	public PacketPlayOutChat(String json, int position, UUID sender) {
		this.json = json;
		this.position = position;
		this.sender = sender;
	}

	public String getJson() {
		return json;
	}

	public int getPosition() {
		return position;
	}

	public UUID getSender() {
		return sender;
	}
	
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, json, StandardCharsets.UTF_8);
		output.writeByte(position);
		DataTypeIO.writeUUID(output, sender);
		
		return buffer.toByteArray();
	}

}

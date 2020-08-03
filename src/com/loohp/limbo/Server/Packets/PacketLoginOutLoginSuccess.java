package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.loohp.limbo.Utils.DataTypeIO;

public class PacketLoginOutLoginSuccess extends Packet {
	
	private UUID uuid;
	private String username;
	
	public PacketLoginOutLoginSuccess(UUID uuid, String username) {
		this.uuid = uuid;
		this.username = username;
	}
	
	public UUID getUuid() {
		return uuid;
	}

	public String getUsername() {
		return username;
	}
	
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getLoginOut().get(getClass()));
		DataTypeIO.writeUUID(output, uuid);
		DataTypeIO.writeString(output, username, StandardCharsets.UTF_8);
		
		return buffer.toByteArray();
	}

}

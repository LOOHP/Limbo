package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;

public class PacketPlayOutResourcePackSend extends PacketOut {
	
	private String url;
	private String hash;
	private boolean isForced;
	private boolean hasPromptMessage;
	private String promptMessage;

	public PacketPlayOutResourcePackSend(String url, String hash, boolean isForced, boolean hasPromptMessage, String promptMessage) {
		this.url = url;
		this.hash = hash;
		this.isForced = isForced;
		this.hasPromptMessage = hasPromptMessage;
		this.promptMessage = promptMessage;
	}

	public String getURL() {
		return url;
	}

	public String getHash() {
		return hash;
	}

	public boolean isForced() {
		return isForced;
	}

	public boolean hasPromptMessage() {
		return hasPromptMessage;
	}

	public String getPromptMessage() {
		return promptMessage;
	}
	
	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, url, StandardCharsets.UTF_8);
		DataTypeIO.writeString(output, hash, StandardCharsets.UTF_8);
		output.writeBoolean(isForced);
		output.writeBoolean(hasPromptMessage);
		if (hasPromptMessage) {
			DataTypeIO.writeString(output, promptMessage, StandardCharsets.UTF_8);
		}
		return buffer.toByteArray();
	}

}

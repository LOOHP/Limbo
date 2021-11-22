package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class PacketPlayOutResourcePackSend extends PacketOut {
	
	public static final int MAX_HASH_LENGTH = 40;
	
	private String url;
	private String hash;
	private boolean isForced;
	private boolean hasPromptMessage;
	private BaseComponent[] promptMessage;

	public PacketPlayOutResourcePackSend(String url, String hash, boolean isForced, boolean hasPromptMessage, BaseComponent[] promptMessage) {
		if (hash.length() > MAX_HASH_LENGTH) {
            throw new IllegalArgumentException("Hash is too long (max " + MAX_HASH_LENGTH + ", was " + hash.length() + ")");
        }
		this.url = url;
		this.hash = hash;
		this.isForced = isForced;
		this.hasPromptMessage = hasPromptMessage;
		if (hasPromptMessage && promptMessage == null) {
			throw new IllegalArgumentException("promptMessage cannot be null when hasPromptMessage is true");
		}
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

	public BaseComponent[] getPromptMessage() {
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
			DataTypeIO.writeString(output, ComponentSerializer.toString(promptMessage), StandardCharsets.UTF_8);
		}
		return buffer.toByteArray();
	}

}

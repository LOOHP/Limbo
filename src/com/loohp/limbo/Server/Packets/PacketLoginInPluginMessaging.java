package com.loohp.limbo.Server.Packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.Utils.DataTypeIO;
import com.loohp.limbo.Utils.NamespacedKey;

public class PacketLoginInPluginMessaging extends PacketIn {

	private int messageId;
	private NamespacedKey channel;
	private byte[] data;

	public PacketLoginInPluginMessaging(int messageId, NamespacedKey channel, byte[] data) {
		this.messageId = messageId;
		this.channel = channel;
		this.data = data;
	}
	
	public PacketLoginInPluginMessaging(DataInputStream in, int packetLength, int packetId) throws IOException {
		messageId = DataTypeIO.readVarInt(in);
		String rawChannel = DataTypeIO.readString(in);
		channel = new NamespacedKey(rawChannel);
		int dataLength = packetLength - DataTypeIO.getVarIntLength(packetId) - DataTypeIO.getVarIntLength(messageId) - DataTypeIO.getStringLength(rawChannel, StandardCharsets.UTF_8);
		data = new byte[dataLength];
		in.read(data);
	}
	
	public int getMessageId() {
		return messageId;
	}

	public NamespacedKey getChannel() {
		return channel;
	}

	public byte[] getData() {
		return data;
	}

}

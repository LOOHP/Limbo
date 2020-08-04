package com.loohp.limbo.Server.Packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.Utils.DataTypeIO;
import com.loohp.limbo.Utils.NamespacedKey;

public class PacketPlayInPluginMessaging extends PacketIn {

	private NamespacedKey channel;
	private byte[] data;

	public PacketPlayInPluginMessaging(NamespacedKey channel, byte[] data) {
		this.channel = channel;
		this.data = data;
	}
	
	public PacketPlayInPluginMessaging(DataInputStream in, int packetLength, int packetId) throws IOException {
		String rawChannel = DataTypeIO.readString(in);
		channel = new NamespacedKey(rawChannel);
		int dataLength = packetLength - DataTypeIO.getVarIntLength(packetId) - DataTypeIO.getStringLength(rawChannel, StandardCharsets.UTF_8);
		data = new byte[dataLength];
		in.read(data);
	}

	public NamespacedKey getChannel() {
		return channel;
	}

	public byte[] getData() {
		return data;
	}

}

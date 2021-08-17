package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketLoginInPluginMessaging extends PacketIn {

	private int messageId;
	private boolean successful;
	private byte[] data = null;

	public PacketLoginInPluginMessaging(int messageId, boolean successful, byte[] data) {
		this.messageId = messageId;
		this.data = data;
	}
	
	public PacketLoginInPluginMessaging(DataInputStream in, int packetLength, int packetId) throws IOException {
		messageId = DataTypeIO.readVarInt(in);
		successful = in.readBoolean();
		if (successful) {
			int dataLength = packetLength - DataTypeIO.getVarIntLength(packetId) - DataTypeIO.getVarIntLength(messageId) - 1;
			if (dataLength != 0) {
				data = new byte[dataLength];
				in.readFully(data);
			}
		}
	}
	
	public int getMessageId() {
		return messageId;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public byte[] getData() {
		return data;
	}

}

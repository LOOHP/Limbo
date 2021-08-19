package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.NamespacedKey;

public class PacketLoginOutPluginMessaging extends PacketOut {

	private int messageId;
	private NamespacedKey channel;
	private byte[] data;

	public PacketLoginOutPluginMessaging(int messageId, NamespacedKey channel) {
		this(messageId, channel, null);
	}

	public PacketLoginOutPluginMessaging(int messageId, NamespacedKey channel, byte[] data) {
		this.messageId = messageId;
		this.channel = channel;
		this.data = data;
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

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getLoginOut().get(getClass()));
		DataTypeIO.writeVarInt(output, messageId);
		DataTypeIO.writeString(output, channel.toString(), StandardCharsets.UTF_8);
		if (data != null) {
			output.write(data);
		}
		
		return buffer.toByteArray();
	}

}

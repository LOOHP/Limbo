package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.NamespacedKey;

public class PacketPlayOutPluginMessaging extends PacketOut {

	private NamespacedKey channel;
	private byte[] data;

	public PacketPlayOutPluginMessaging(NamespacedKey channel, byte[] data) {
		this.channel = channel;
		this.data = data;
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
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, channel.toString(), StandardCharsets.UTF_8);
		output.write(data);
		
		return buffer.toByteArray();
	}

}

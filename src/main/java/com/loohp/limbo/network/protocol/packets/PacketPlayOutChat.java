package com.loohp.limbo.network.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.loohp.limbo.utils.DataTypeIO;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class PacketPlayOutChat extends PacketOut {
	
	private Component message;
	private int position;
	private UUID sender;
	
	public PacketPlayOutChat(Component message, int position, UUID sender) {
		this.message = message;
		this.position = position;
		this.sender = sender;
	}

	public Component getMessage() {
		return message;
	}

	public int getPosition() {
		return position;
	}

	public UUID getSender() {
		return sender;
	}
	
	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(message), StandardCharsets.UTF_8);
		output.writeByte(position);
		DataTypeIO.writeUUID(output, sender);
		
		return buffer.toByteArray();
	}

}

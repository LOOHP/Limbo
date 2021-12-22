package com.loohp.limbo.network.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class PacketLoginOutDisconnect extends PacketOut {
	
	private Component reason;

	public PacketLoginOutDisconnect(Component reason) {
		this.reason = reason;
	}

	public Component getReason() {
		return reason;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getLoginOut().get(getClass()));
		DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(reason), StandardCharsets.UTF_8);
		
		return buffer.toByteArray();
	}

}

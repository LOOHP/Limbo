package com.loohp.limbo.network.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class ClientboundSetTitleTextPacket extends PacketOut {
	
	private Component titleText;

	public ClientboundSetTitleTextPacket(Component titleText) {
		this.titleText = titleText;
	}

	public Component getTitle() {
		return titleText;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(titleText), StandardCharsets.UTF_8);
		
		return buffer.toByteArray();
	}

}

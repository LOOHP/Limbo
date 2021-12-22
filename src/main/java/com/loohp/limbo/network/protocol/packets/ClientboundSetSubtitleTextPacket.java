package com.loohp.limbo.network.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class ClientboundSetSubtitleTextPacket extends PacketOut {
	
	private Component subTitle;

	public ClientboundSetSubtitleTextPacket(Component subTitle) {
		this.subTitle = subTitle;
	}

	public Component getSubTitle() {
		return subTitle;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(subTitle), StandardCharsets.UTF_8);
		
		return buffer.toByteArray();
	}

}

package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class PacketPlayOutSetTitleText extends PacketOut {
	
	private BaseComponent[] titleText;

	public PacketPlayOutSetTitleText(BaseComponent[] titleText) {
		this.titleText = titleText;
	}

	public BaseComponent[] getTitle() {
		return titleText;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, ComponentSerializer.toString(titleText), StandardCharsets.UTF_8);
		
		return buffer.toByteArray();
	}

}

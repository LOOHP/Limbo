package com.loohp.limbo.network.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class PacketPlayOutPlayerListHeaderFooter extends PacketOut{
	
	private Component header;
	private Component footer;
	
	public PacketPlayOutPlayerListHeaderFooter(Component header, Component footer) {
		this.header = header;
		this.footer = footer;
	}

	public Component getHeader() {
		return header;
	}

	public Component getFooter() {
		return footer;
	}
	
	
	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(header), StandardCharsets.UTF_8);
		DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(footer), StandardCharsets.UTF_8);
		return buffer.toByteArray();
	}

}

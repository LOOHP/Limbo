package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class PacketPlayOutPlayerListHeaderFooter extends PacketOut{
	
	private BaseComponent[] header;
	private BaseComponent[] footer;
	
	public PacketPlayOutPlayerListHeaderFooter(BaseComponent[] header, BaseComponent[] footer) {
		this.header = header;
		this.footer = footer;
	}

	public BaseComponent[] getHeader() {
		return header;
	}

	public BaseComponent[] getFooter() {
		return footer;
	}
	
	
	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, ComponentSerializer.toString(header), StandardCharsets.UTF_8);
		DataTypeIO.writeString(output, ComponentSerializer.toString(footer), StandardCharsets.UTF_8);
		return buffer.toByteArray();
	}

}

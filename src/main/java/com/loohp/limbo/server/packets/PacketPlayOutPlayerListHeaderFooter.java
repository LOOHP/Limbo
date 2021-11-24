package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;

public class PacketPlayOutPlayerListHeaderFooter extends PacketOut{
	
	private String header;
	private String footer;
	
	public PacketPlayOutPlayerListHeaderFooter(String header, String footer) {
		this.header = header;
		this.footer = footer;
	}

	public String getHeader() {
		return header;
	}

	public String getFooter() {
		return footer;
	}
	
	
	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, header, StandardCharsets.UTF_8);
		DataTypeIO.writeString(output, footer, StandardCharsets.UTF_8);
		return buffer.toByteArray();
	}

}

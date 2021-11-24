package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class PacketLoginOutDisconnect extends PacketOut {
	
	private BaseComponent[] reason;

	public PacketLoginOutDisconnect(BaseComponent[] reason) {
		this.reason = reason;
	}

	public BaseComponent[] getReason() {
		return reason;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getLoginOut().get(getClass()));
		DataTypeIO.writeString(output, ComponentSerializer.toString(reason), StandardCharsets.UTF_8);
		
		return buffer.toByteArray();
	}

}

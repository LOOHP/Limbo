package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.loohp.limbo.Utils.DataTypeIO;

public class PacketPlayOutShowPlayerSkins extends PacketOut {

	private int entityId;

	public PacketPlayOutShowPlayerSkins(int entityId) {
		this.entityId = entityId;
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	@Override
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		
		DataTypeIO.writeVarInt(output, entityId);
		output.writeByte(16);
		DataTypeIO.writeVarInt(output, 0);
		int bitmask = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40;
		output.writeByte(bitmask);
		output.writeByte(0xff);
		
		return buffer.toByteArray();
	}

}

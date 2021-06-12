package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.loohp.limbo.utils.DataTypeIO;

public class PacketPlayOutEntityDestroy extends PacketOut {
	
	private int entityId;
	
	public PacketPlayOutEntityDestroy(int entityId) {
		this.entityId = entityId;
	}

	public int getEntityId() {
		return entityId;
	}
	
	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeVarInt(output, entityId);
		
		return buffer.toByteArray();
	}

}

package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.loohp.limbo.Utils.DataTypeIO;
import com.loohp.limbo.World.BlockPosition;

public class PacketPlayOutSpawnPosition extends PacketOut {
	
	private BlockPosition position;
	
	public PacketPlayOutSpawnPosition(BlockPosition position) {
		this.position = position;
	}

	public BlockPosition getPosition() {
		return position;
	}
	
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeBlockPosition(output, position);
		
		return buffer.toByteArray();
	}

}

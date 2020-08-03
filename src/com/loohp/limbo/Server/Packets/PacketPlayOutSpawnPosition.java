package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.loohp.limbo.World.BlockPosition;

public class PacketPlayOutSpawnPosition extends Packet {
	
	private BlockPosition position;
	
	public PacketPlayOutSpawnPosition(BlockPosition position) {
		this.position = position;
	}

	public BlockPosition getPosition() {
		return position;
	}
	
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		output.writeLong(((x & 0x3FFFFFF) << 38) | ((z & 0x3FFFFFF) << 12) | (y & 0xFFF));
		
		return buffer.toByteArray();
	}

}

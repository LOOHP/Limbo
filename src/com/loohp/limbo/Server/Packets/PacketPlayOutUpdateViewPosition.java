package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.loohp.limbo.Utils.DataTypeIO;

public class PacketPlayOutUpdateViewPosition extends PacketOut {
	
	private int chunkX;
	private int chunkZ;
	
	public PacketPlayOutUpdateViewPosition(int chunkX, int chunkZ) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	public int getChunkX() {
		return chunkX;
	}

	public int getChunkZ() {
		return chunkZ;
	}
	
	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeVarInt(output, chunkX);
		DataTypeIO.writeVarInt(output, chunkZ);
		
		return buffer.toByteArray();
	}

}

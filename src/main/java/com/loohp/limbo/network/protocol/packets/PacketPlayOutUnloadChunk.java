package com.loohp.limbo.network.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutUnloadChunk extends PacketOut {
	
	private int chunkX;
	private int chunkZ;
	
	public PacketPlayOutUnloadChunk(int chunkX, int chunkZ) {
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
		output.writeInt(chunkX);
		output.writeInt(chunkZ);
		
		return buffer.toByteArray();
	}

}

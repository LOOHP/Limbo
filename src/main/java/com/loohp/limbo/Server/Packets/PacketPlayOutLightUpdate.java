package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import com.loohp.limbo.utils.DataTypeIO;

public class PacketPlayOutLightUpdate extends PacketOut {

	private int chunkX;
	private int chunkZ;
	private boolean trustEdges;
	private int skyLightBitMask;
	private int blockLightBitMask;
	private List<Byte[]> skylightArrays;
	private List<Byte[]> blocklightArrays;

	public PacketPlayOutLightUpdate(int chunkX, int chunkZ, boolean trustEdges, List<Byte[]> skylightArrays, List<Byte[]> blocklightArrays) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.trustEdges = trustEdges;
		this.skylightArrays = skylightArrays;
		this.blocklightArrays = blocklightArrays;
		
		skyLightBitMask = 0;
		for (int i = Math.min(17, skylightArrays.size() - 1); i >= 0; i--) {
			skyLightBitMask = skyLightBitMask >> 1;
			if (skylightArrays.get(i) != null) {
				skyLightBitMask |= 131072;
			}
		}
		
		blockLightBitMask = 0;
		for (int i = Math.min(17, blocklightArrays.size() - 1); i >= 0; i--) {
			blockLightBitMask = blockLightBitMask >> 1;
			if (blocklightArrays.get(i) != null) {
				blockLightBitMask |= 131072;
			}
		}
	}

	public int getChunkX() {
		return chunkX;
	}

	public int getChunkZ() {
		return chunkZ;
	}

	public boolean isTrustEdges() {
		return trustEdges;
	}

	public int getSkyLightBitMask() {
		return skyLightBitMask;
	}

	public int getBlockLightBitMask() {
		return blockLightBitMask;
	}

	public List<Byte[]> getSkylightArrays() {
		return skylightArrays;
	}

	public List<Byte[]> getBlocklightArrays() {
		return blocklightArrays;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeVarInt(output, chunkX);
		DataTypeIO.writeVarInt(output, chunkZ);
		output.writeBoolean(trustEdges);
		DataTypeIO.writeVarInt(output, skyLightBitMask);
		DataTypeIO.writeVarInt(output, blockLightBitMask);
		DataTypeIO.writeVarInt(output, ~skyLightBitMask & 262143);
		DataTypeIO.writeVarInt(output, ~blockLightBitMask & 262143);
		
		for (int i = skylightArrays.size() - 1; i >= 0; i--) {
			Byte[] array = skylightArrays.get(i);
			if (array != null) {
				DataTypeIO.writeVarInt(output, 2048);
				//System.out.println(Arrays.toString(ArrayUtils.toPrimitive(array)));
				for (int u = 0; u < array.length; u++) {
					output.writeByte(array[u]);
				}
			}
		}
		
		for (int i = blocklightArrays.size() - 1; i >= 0; i--) {
			Byte[] array = blocklightArrays.get(i);
			if (array != null) {
				DataTypeIO.writeVarInt(output, 2048);
				//System.out.println(Arrays.toString(ArrayUtils.toPrimitive(array)));
				for (int u = 0; u < array.length; u++) {
					output.writeByte(array[u]);
				}
			}
		}
		
		return buffer.toByteArray();
	}

}

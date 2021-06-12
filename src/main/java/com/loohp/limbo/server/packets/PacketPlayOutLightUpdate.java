package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;

import com.loohp.limbo.utils.DataTypeIO;

public class PacketPlayOutLightUpdate extends PacketOut {

	private int chunkX;
	private int chunkZ;
	private boolean trustEdges;
	private long[] skyLightBitMasks;
	private long[] blockLightBitMasks;
	private long[] skyLightBitMasksEmpty;
	private long[] blockLightBitMasksEmpty;
	private List<Byte[]> skylightArrays;
	private List<Byte[]> blocklightArrays;

	public PacketPlayOutLightUpdate(int chunkX, int chunkZ, boolean trustEdges, List<Byte[]> skylightArrays, List<Byte[]> blocklightArrays) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.trustEdges = trustEdges;
		this.skylightArrays = skylightArrays;
		this.blocklightArrays = blocklightArrays;
		
		BitSet skyLightBitSet = new BitSet();
		BitSet skyLightBitSetInverse = new BitSet();
		for (int i = Math.min(17, skylightArrays.size() - 1); i >= 0; i--) {
			skyLightBitSet.set(i, skylightArrays.get(i) != null);
			skyLightBitSetInverse.set(i, skylightArrays.get(i) == null);
		}
		skyLightBitMasks = skyLightBitSet.toLongArray();
		skyLightBitMasksEmpty = skyLightBitSetInverse.toLongArray();
		
		BitSet blockLightBitSet = new BitSet();
		BitSet blockLightBitSetInverse = new BitSet();
		for (int i = Math.min(17, blocklightArrays.size() - 1); i >= 0; i--) {
			blockLightBitSet.set(i, blocklightArrays.get(i) != null);
			blockLightBitSetInverse.set(i, blocklightArrays.get(i) == null);
		}
		blockLightBitMasks = blockLightBitSet.toLongArray();
		blockLightBitMasksEmpty = blockLightBitSetInverse.toLongArray();
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

	public long[] getSkyLightBitMasks() {
		return skyLightBitMasks;
	}

	public long[] getBlockLightBitMasks() {
		return blockLightBitMasks;
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
		DataTypeIO.writeVarInt(output, skyLightBitMasks.length);
		for (long l : skyLightBitMasks) {
			output.writeLong(l);
		}
		DataTypeIO.writeVarInt(output, blockLightBitMasks.length);
		for (long l : blockLightBitMasks) {
			output.writeLong(l);
		}
		DataTypeIO.writeVarInt(output, skyLightBitMasksEmpty.length);
		for (long l : skyLightBitMasksEmpty) {
			output.writeLong(l);
		}
		DataTypeIO.writeVarInt(output, blockLightBitMasksEmpty.length);
		for (long l : blockLightBitMasksEmpty) {
			output.writeLong(l);
		}
		
		DataTypeIO.writeVarInt(output, skylightArrays.stream().mapToInt(each -> each == null ? 0 : 1).sum());
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
		
		DataTypeIO.writeVarInt(output, blocklightArrays.stream().mapToInt(each -> each == null ? 0 : 1).sum());
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

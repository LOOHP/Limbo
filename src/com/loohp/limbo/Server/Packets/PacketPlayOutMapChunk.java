package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.loohp.limbo.Utils.ChunkDataUtils;
import com.loohp.limbo.Utils.DataTypeIO;
import com.loohp.limbo.Utils.GeneratedDataUtils;

import net.querz.mca.Chunk;
import net.querz.mca.Section;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class PacketPlayOutMapChunk extends PacketOut {

	private int chunkX;
	private int chunkZ;
	private Chunk chunk;

	public PacketPlayOutMapChunk(int chunkX, int chunkZ, Chunk chunk) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.chunk = chunk;
	}

	public Chunk getChunk() {
		return chunk;
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
		output.writeBoolean(true);
		output.writeBoolean(true);
		int bitmask = 0;
		for (int i = 0; i < 16; i++) {
			Section section = chunk.getSection(i);
			if (section != null) {
				bitmask = bitmask | (int) Math.pow(2, i);
			}
		}
		DataTypeIO.writeVarInt(output, bitmask);
		DataTypeIO.writeCompoundTag(output, chunk.getHeightMaps());
		//for (int i : chunk.getBiomes()) {
		//	output.writeInt(i);
		//}
		for (int i = 0; i < 1024; i++) {
			output.writeInt(127);
		}
		
		ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(dataBuffer);
		for (int i = 0; i < 16; i++) {
			Section section = chunk.getSection(i);
			if (section != null) {
				int counter = 0;
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						for (int y = 0; y < 16; y++) {
							CompoundTag tag = section.getBlockStateAt(x, y, z);
							if (tag != null && !tag.getString("Name").equals("minecraft:air")) {
								counter++;
							}
						}
					}
				}				
				dataOut.writeShort(counter);

				int newBits = 32 - Integer.numberOfLeadingZeros(section.getPalette().size() - 1);
				newBits = Math.max(newBits, 4);
				
				if (newBits <= 8) {
					if (newBits == 4) {
						dataOut.writeByte(4);
					} else {
						newBits = 8;
						ChunkDataUtils.adjustBlockStateBits(newBits, section, chunk.getDataVersion());
						dataOut.writeByte(8);
					}
					
					DataTypeIO.writeVarInt(dataOut, section.getPalette().size());
					//System.out.println(section.getPalette().size());
					Iterator<CompoundTag> itr1 = section.getPalette().iterator();
					//System.out.println("Nonnull -> " + i + " " + newBits);
					counter = 0;
					while (itr1.hasNext()) {
						CompoundTag tag = itr1.next();
						DataTypeIO.writeVarInt(dataOut, GeneratedDataUtils.getGlobalPaletteIDFromState(tag));
						//System.out.println(tag + " -> " + GeneratedDataUtils.getGlobalPaletteIDFromState(tag));
					}
				} else {
					dataOut.writeByte(14);
				}
				
				DataTypeIO.writeVarInt(dataOut, section.getBlockStates().length);
				for (int u = 0; u < section.getBlockStates().length; u++) {
					dataOut.writeLong(section.getBlockStates()[u]);
					//System.out.println(Arrays.toString(section.getBlockStates()));
				}
			}
		}
		byte[] data = dataBuffer.toByteArray();
		DataTypeIO.writeVarInt(output, data.length);
		output.write(data);
		
		ListTag<CompoundTag> tileEntities = chunk.getTileEntities();
		DataTypeIO.writeVarInt(output, tileEntities.size());
		for (CompoundTag each : tileEntities) {
			DataTypeIO.writeCompoundTag(output, each);
		}
		
		return buffer.toByteArray();
	}

}

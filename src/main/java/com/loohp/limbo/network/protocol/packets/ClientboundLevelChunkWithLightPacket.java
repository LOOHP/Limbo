/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loohp.limbo.network.protocol.packets;

import com.loohp.limbo.registry.Registry;
import com.loohp.limbo.utils.BitsUtils;
import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.world.Environment;
import com.loohp.limbo.world.GeneratedBlockDataMappings;
import net.kyori.adventure.key.Key;
import net.querz.mca.Chunk;
import net.querz.mca.Section;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class ClientboundLevelChunkWithLightPacket extends PacketOut {

	private int chunkX;
	private int chunkZ;
	private Chunk chunk;
	private Environment environment;
	private boolean trustEdges;
	private long[] skyLightBitMasks;
	private long[] blockLightBitMasks;
	private long[] skyLightBitMasksEmpty;
	private long[] blockLightBitMasksEmpty;
	private List<Byte[]> skylightArrays;
	private List<Byte[]> blocklightArrays;

	public ClientboundLevelChunkWithLightPacket(int chunkX, int chunkZ, Chunk chunk, Environment environment, boolean trustEdges, List<Byte[]> skylightArrays, List<Byte[]> blocklightArrays) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.chunk = chunk;
		this.environment = environment;
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

	public Chunk getChunk() {
		return chunk;
	}

	public int getChunkX() {
		return chunkX;
	}

	public int getChunkZ() {
		return chunkZ;
	}

	public Environment getEnvironment() {
		return environment;
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

		output.writeInt(chunkX);
		output.writeInt(chunkZ);
		DataTypeIO.writeCompoundTag(output, chunk.getHeightMaps());

		ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(dataBuffer);
		for (int i = 0; i < 16; i++) {
			Section section = chunk.getSection(i);
			if (section != null) {
				short counter = 0;
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
				//Limbo.getInstance().getConsole().sendMessage(i + " " + newBits);
				if (newBits <= 8) {
					dataOut.writeByte(newBits);

					DataTypeIO.writeVarInt(dataOut, section.getPalette().size());
					//Limbo.getInstance().getConsole().sendMessage(section.getPalette().size());
					//Limbo.getInstance().getConsole().sendMessage("Nonnull -> " + i + " " + newBits);
					for (CompoundTag tag : section.getPalette()) {
						DataTypeIO.writeVarInt(dataOut, GeneratedBlockDataMappings.getGlobalPaletteIDFromState(tag));
						//Limbo.getInstance().getConsole().sendMessage(tag + " -> " + GeneratedDataUtils.getGlobalPaletteIDFromState(tag));
					}

					BitSet bits = BitSet.valueOf(section.getBlockStates());
					int shift = 64 % newBits;
					int longsNeeded = (int) Math.ceil(4096 / (double) (64 / newBits));
					for (int u = 64; u <= bits.length(); u += 64) {
						BitsUtils.shiftAfter(bits, u - shift, shift);
					}

					long[] formattedLongs = bits.toLongArray();
					//Limbo.getInstance().getConsole().sendMessage(longsNeeded + "");

					DataTypeIO.writeVarInt(dataOut, longsNeeded);
					for (int u = 0; u < longsNeeded; u++) {
						if (u < formattedLongs.length) {
							dataOut.writeLong(formattedLongs[u]);
						} else {
							dataOut.writeLong(0);
						}
						//Limbo.getInstance().getConsole().sendMessage(Arrays.toString(section.getBlockStates()));
					}
				} else {
					try {
						dataOut.writeByte(16);
						section.getBlockStates();
						int longsNeeded = 1024;
						List<Integer> list = new LinkedList<>();
						for (int y = 0; y < 16; y++) {
							for (int z = 0; z < 16; z++) {
								for (int x = 0; x < 16; x++) {
									list.add(GeneratedBlockDataMappings.getGlobalPaletteIDFromState(section.getBlockStateAt(x, y, z)));
								}
							}
						}
						List<Long> globalLongs = new ArrayList<>();
						long currentLong = 0;
						int pos = 0;
						int u = 0;
						while (pos < longsNeeded) {
							if (u == 3) {
								globalLongs.add(currentLong);
								currentLong = 0;
								u = 0;
								pos++;
							} else {
								u++;
							}
							int id = list.isEmpty() ? 0 : list.remove(0);
							currentLong = currentLong << 16;
							currentLong |= id;
						}
						DataTypeIO.writeVarInt(dataOut, longsNeeded);
						for (int j = 0; j < longsNeeded; j++) {
							if (j < globalLongs.size()) {
								dataOut.writeLong(globalLongs.get(j));
							} else {
								dataOut.writeLong(0);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				dataOut.writeShort(0);
				dataOut.writeByte(0);
				DataTypeIO.writeVarInt(dataOut, 0);
				DataTypeIO.writeVarInt(dataOut, 0);
			}
			int biome;
			if (environment.equals(Environment.END)) {
				biome = 9; //the_end
			} else if (environment.equals(Environment.NETHER)) {
				biome = 8; //nether_waste
			} else if (environment.equals(Environment.NORMAL)) {
				biome = 1; //plains
			} else {
				biome = 1; //plains
			}
			dataOut.writeByte(0);
			DataTypeIO.writeVarInt(dataOut, biome);
			DataTypeIO.writeVarInt(dataOut, 0);
		}

		byte[] data = dataBuffer.toByteArray();
		DataTypeIO.writeVarInt(output, data.length);
		output.write(data);

		ListTag<CompoundTag> tileEntities = chunk.getTileEntities();
		DataTypeIO.writeVarInt(output, tileEntities.size());
		for (CompoundTag each : tileEntities) {
			int x = each.getInt("x") % 16;
			int y = each.getInt("y");
			int z = each.getInt("z") % 16;
			output.writeByte(((x & 15) << 4) | (z & 15));
			output.writeShort(y);
			Integer id = Registry.BLOCK_ENTITY_TYPE.getId(Key.key(chunk.getBlockStateAt(x, y, z).getString("Name")));
			DataTypeIO.writeVarInt(output, id == null ? -1 : id);
			DataTypeIO.writeCompoundTag(output, each);
		}

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

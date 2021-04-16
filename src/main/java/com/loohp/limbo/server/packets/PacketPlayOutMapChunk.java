package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.BitsUtils;
import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.world.Environment;
import com.loohp.limbo.world.GeneratedBlockDataMappings;
import net.querz.mca.Chunk;
import net.querz.mca.Section;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class PacketPlayOutMapChunk extends PacketOut {

    private final int chunkX;
    private final int chunkZ;
    private final Chunk chunk;
    private final Environment environment;

    public PacketPlayOutMapChunk(int chunkX, int chunkZ, Chunk chunk, Environment environment) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.chunk = chunk;
        this.environment = environment;
    }

    @Deprecated
    public PacketPlayOutMapChunk(int chunkX, int chunkZ, Chunk chunk) {
        this(chunkZ, chunkZ, chunk, Environment.NORMAL);
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

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));

        output.writeInt(chunkX);
        output.writeInt(chunkZ);
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

        DataTypeIO.writeVarInt(output, 1024);
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
        for (int i = 0; i < 1024; i++) {
            DataTypeIO.writeVarInt(output, biome);
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
                //Limbo.getInstance().getConsole().sendMessage(i + " " + newBits);
                if (newBits <= 8) {
					/*
					if (newBits == 4) {
						dataOut.writeByte(4);
					} else {
						newBits = 8;
						ChunkDataUtils.adjustBlockStateBits(newBits, section, chunk.getDataVersion());
						dataOut.writeByte(8);
					}
					*/
                    dataOut.writeByte(newBits);

                    DataTypeIO.writeVarInt(dataOut, section.getPalette().size());
                    //Limbo.getInstance().getConsole().sendMessage(section.getPalette().size());
                    Iterator<CompoundTag> itr1 = section.getPalette().iterator();
                    //Limbo.getInstance().getConsole().sendMessage("Nonnull -> " + i + " " + newBits);
                    while (itr1.hasNext()) {
                        CompoundTag tag = itr1.next();
                        DataTypeIO.writeVarInt(dataOut, GeneratedBlockDataMappings.getGlobalPaletteIDFromState(tag));
                        //Limbo.getInstance().getConsole().sendMessage(tag + " -> " + GeneratedDataUtils.getGlobalPaletteIDFromState(tag));
                    }

                    BitSet bits = BitSet.valueOf(section.getBlockStates());
                    int shift = 64 % newBits;
                    int longsNeeded = (int) Math.ceil(4096 / (double) (64 / newBits));
                    for (int u = 64; u <= bits.length(); u += 64) {
                        bits = BitsUtils.shiftAfter(bits, u - shift, shift);
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
                        dataOut.writeByte(15);
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
                            currentLong = currentLong << 15;
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

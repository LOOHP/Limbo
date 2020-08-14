package com.loohp.limbo.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.loohp.limbo.Utils.SchematicConvertionUtils;

import net.querz.mca.Chunk;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;


public class Schematic {
	
	public static World toWorld(String name, Environment environment, CompoundTag nbt) {
		short width = nbt.getShort("Width");
		short length = nbt.getShort("Length");
		short height = nbt.getShort("Height");
		byte[] blocks = nbt.getByteArray("BlockData");
		CompoundTag palette = nbt.getCompoundTag("Palette");
		ListTag<CompoundTag> blockEntities = nbt.getListTag("BlockEntities").asTypedList(CompoundTag.class);
		Map<Integer, String> mapping = new HashMap<>();
		for (String key : palette.keySet()) {
			mapping.put(palette.getInt(key), key);
		}
		
		World world = new World(name, width, length, environment);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					int blockIndex = x + z * width + y * width * length;
					world.setBlock(x, y, z, mapping.get(blocks[blockIndex] < 0 ? blocks[blockIndex] + 256 : blocks[blockIndex]));
					Chunk chunk = world.getChunkAtWorldPos(x, z);
					
					Iterator<CompoundTag> itr = blockEntities.iterator();
					while (itr.hasNext()) {
						CompoundTag tag = itr.next();
						int[] pos = tag.getIntArray("Pos");
						
						if (pos[0] == x && pos[1] == y && pos[2] == z) {
							ListTag<CompoundTag> newTag = chunk.getTileEntities();
							newTag.add(SchematicConvertionUtils.toTileEntityTag(tag));
							chunk.setTileEntities(newTag);
							itr.remove();
							break;
						}
					}
				}
			}
		}
		
		for (Chunk[] chunkarray : world.getChunks()) {
			for (Chunk chunk : chunkarray) {
				if (chunk != null) {
					CompoundTag heightMap = new CompoundTag();
					heightMap.putLongArray("MOTION_BLOCKING", new long[] {1371773531765642314L,1389823183635651148L,1371738278539598925L,1389823183635388492L,1353688558756731469L,1389823114781694027L,1317765589597723213L,1371773531899860042L,1389823183635651149L,1371773462911685197L,1389823183635650636L,1353688626805119565L,1371773531900123211L,1335639250618849869L,1371738278674077258L,1389823114781694028L,1353723811310638154L,1371738278674077259L,1335674228429068364L,1335674228429067338L,1335674228698027594L,1317624576693539402L,1335709481520370249L,1299610178184057417L,1335638906349064264L,1299574993811968586L,1299574924958011464L,1299610178184056904L,1299574924958011464L,1299610109330100296L,1299574924958011464L,1299574924823793736L,1299574924958011465L,1281525273222484040L,1299574924958011464L,1281525273222484040L,9548107335L});
					chunk.setHeightMaps(heightMap);
					chunk.setBiomes(new int[256]);
					//chunk.cleanupPalettesAndBlockStates();
				}
			}
		}
		
		return world;
	}
}
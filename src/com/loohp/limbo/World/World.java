package com.loohp.limbo.World;

import com.loohp.limbo.Utils.SchematicConvertionUtils;

import net.querz.mca.Chunk;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class World {

	private String name;
	private Chunk[][] chunks;

	public World(String name, int width, int length) {
		this.name = name;
		this.chunks = new Chunk[(width >> 4) + 1][(length >> 4) + 1];
		
		for (int x = 0; x < chunks.length; x++) {
			for (int z = 0; z < chunks[x].length; z++) {
				chunks[x][z] = Chunk.newChunk();
				Chunk chunk = chunks[x][z];
				chunk.cleanupPalettesAndBlockStates();
				CompoundTag heightMap = new CompoundTag();
				heightMap.putLongArray("MOTION_BLOCKING", new long[] {1371773531765642314L,1389823183635651148L,1371738278539598925L,1389823183635388492L,1353688558756731469L,1389823114781694027L,1317765589597723213L,1371773531899860042L,1389823183635651149L,1371773462911685197L,1389823183635650636L,1353688626805119565L,1371773531900123211L,1335639250618849869L,1371738278674077258L,1389823114781694028L,1353723811310638154L,1371738278674077259L,1335674228429068364L,1335674228429067338L,1335674228698027594L,1317624576693539402L,1335709481520370249L,1299610178184057417L,1335638906349064264L,1299574993811968586L,1299574924958011464L,1299610178184056904L,1299574924958011464L,1299610109330100296L,1299574924958011464L,1299574924823793736L,1299574924958011465L,1281525273222484040L,1299574924958011464L,1281525273222484040L,9548107335L});
				chunk.setHeightMaps(heightMap);
				chunk.setBiomes(new int[256]);
				chunk.setTileEntities(new ListTag<CompoundTag>(CompoundTag.class));
			}
		}
	}

	public void setBlock(int x, int y, int z, String blockdata) {
		Chunk chunk = this.chunks[(x >> 4)][(z >> 4)];
		if (chunk == null) {
			chunk = Chunk.newChunk();
			this.chunks[(x >> 4)][(z >> 4)] = chunk;
		}
		CompoundTag block = SchematicConvertionUtils.toBlockTag(blockdata);
		chunk.setBlockStateAt(x, y, z, block, false);
	}

	public Chunk[][] getChunks() {
		return this.chunks;
	}

	public Chunk getChunkAtWorldPos(int x, int z) {
		return this.chunks[(x >> 4)][(z >> 4)];
	}

	public String getName() {
		return name;
	}
}

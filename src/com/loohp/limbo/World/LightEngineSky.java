package com.loohp.limbo.World;

import java.util.ArrayList;
import java.util.List;

public class LightEngineSky extends LightEngine {
	
	private World world;
	private byte[][][] skyLightArray;
	
	public LightEngineSky(World world) {
		skyLightArray = new byte[world.getChunkWidth() * 16][16 * 18][world.getChunkLength() * 16];
		/*
		for (byte[][] arrayarray : skyLightArray) {
			for (byte[] array : arrayarray) {
				Arrays.fill(array, (byte) 0); 
			}
		}
		*/
		this.world = world;
		updateWorld();
	}
	
	public void updateWorld() {
		skyLightArray = new byte[world.getChunkWidth() * 16][16 * 18][world.getChunkLength() * 16];
		for (int x = 0; x < world.getWidth(); x++) {
			for (int z = 0; z < world.getLength(); z++) {
				updateColumn(x, z);
			}
		}
	}
	
	private void updateColumn(int x, int z) {
		for (int y = 272; y >= 256; y--) {
			propergate(15, x, y, z);
		}
		for (int y = 255; y >= 0; y--) {
			BlockState block = world.getBlock(x, y, z);
			//System.out.println("X:" + x + " Y: " + y + " Z: " + z + " Block: " + block.getType().toString());
			if (!block.getType().toString().equals("minecraft:air")) {
				break;
			}
			propergate(15, x, y, z);
		}
	}
	
	private void propergate(int level, int x, int y, int z) {
		try {
			if (skyLightArray[x][y + 16][z] < level) {
				skyLightArray[x][y + 16][z] = (byte) level;
				if (level > 1) {
					propergate(level - 1, x + 1, y, z);
					propergate(level - 1, x - 1, y, z);
					propergate(level - 1, x, y + 1, z);
					propergate(level - 1, x, y, z + 1);
					propergate(level - 1, x, y, z - 1);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {}
	}
	
	public List<Byte[]> getSkyLightBitMask(int chunkX, int chunkZ) {
		List<Byte[]> subchunks = new ArrayList<>(18);
		int startX = chunkX * 16;
		int endingX = startX + 16;
		int startZ = chunkZ * 16;
		int endingZ = startZ + 16;
		
		for (int sub = 17; sub >= 0; sub--) {
			List<Byte> array = new ArrayList<>();
			for (int y = sub * 16; y < (sub * 16) + 16; y++) {
				for (int z = startZ; z < endingZ; z++) {
					for (int x = startX; x < endingX; x += 2) {
						int bit = skyLightArray[x][y][z];
						bit = bit << 4;
						bit |= skyLightArray[x + 1][y][z];
						array.add((byte) bit);
					}
				}
			}
			subchunks.add(array.toArray(new Byte[2048]));
		}
		
		return subchunks;
	}

}

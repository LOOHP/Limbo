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

package com.loohp.limbo.world;

import java.util.ArrayList;
import java.util.List;

public class LightEngineBlock extends LightEngine {
	
	private World world;
	private byte[][][] blockLightArray;
	
	public LightEngineBlock(World world) {
		blockLightArray = new byte[world.getChunkWidth() * 16][16 * 18][world.getChunkLength() * 16];
		this.world = world;
		updateWorld();
	}
	
	public void updateWorld() {
		blockLightArray = new byte[world.getChunkWidth() * 16][16 * 18][world.getChunkLength() * 16];
		for (int x = 0; x < world.getWidth(); x++) {
			for (int y = 0; y < 256; y++) {
				for (int z = 0; z < world.getLength(); z++) {
					updateBlock(x, y, z);
				}
			}
		}
	}
	
	private void updateBlock(int x, int y, int z) {
		BlockState block = world.getBlock(x, y, z);
		int lightLevel = getBlockLight(block);
		if (lightLevel > 0) {
			propergate(lightLevel, x, y, z);
		}
	}
	
	private void propergate(int level, int x, int y, int z) {
		try {
			if (blockLightArray[x][y + 16][z] < level) {
				blockLightArray[x][y + 16][z] = (byte) level;
				if (level > 1) {
					try {propergate(level - 1, x + 1, y, z);} catch (ArrayIndexOutOfBoundsException e) {}
					try {propergate(level - 1, x - 1, y, z);} catch (ArrayIndexOutOfBoundsException e) {}
					try {propergate(level - 1, x, y + 1, z);} catch (ArrayIndexOutOfBoundsException e) {}
					try {propergate(level - 1, x, y - 1, z);} catch (ArrayIndexOutOfBoundsException e) {}
					try {propergate(level - 1, x, y, z + 1);} catch (ArrayIndexOutOfBoundsException e) {}
					try {propergate(level - 1, x, y, z - 1);} catch (ArrayIndexOutOfBoundsException e) {}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {}
	}
	
	public List<Byte[]> getBlockLightBitMask(int chunkX, int chunkZ) {
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
						int bit = blockLightArray[x][y][z];
						bit = bit << 4;
						bit |= blockLightArray[x + 1][y][z];
						array.add((byte) bit);
					}
				}
			}
			subchunks.add(array.toArray(new Byte[2048]));
		}
		
		return subchunks;
	}

}

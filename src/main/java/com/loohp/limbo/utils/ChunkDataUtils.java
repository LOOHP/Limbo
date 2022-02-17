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

package com.loohp.limbo.utils;

import net.querz.mca.Section;

public class ChunkDataUtils {
	
	public static void adjustBlockStateBits(int newBits, Section section, int dataVersion) {
		//increases or decreases the amount of bits used per BlockState
		//based on the size of the palette.

		long[] blockStates = section.getBlockStates();
		long[] newBlockStates;

		if (dataVersion < 2527) {
			newBlockStates = newBits == blockStates.length / 64 ? blockStates : new long[newBits * 64];
		} else {
			int newLength = (int) Math.ceil(4096D / (64D / newBits));
			newBlockStates = newBits == blockStates.length / 64 ? blockStates : new long[newLength];
		}
		for (int i = 0; i < 4096; i++) {
			section.setPaletteIndex(i, section.getPaletteIndex(i), newBlockStates);
		}
		section.setBlockStates(newBlockStates);
	}

}

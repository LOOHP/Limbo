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

import net.kyori.adventure.key.Key;
import net.querz.nbt.tag.CompoundTag;

public class SchematicConversionUtils {
	
	public static CompoundTag toTileEntityTag(CompoundTag tag) {
		int[] pos = tag.getIntArray("Pos");
		tag.remove("Pos");
		tag.remove("Id");
		tag.putInt("x", pos[0]);
		tag.putInt("y", pos[1]);
		tag.putInt("z", pos[2]);
		return tag;
	}
	
	public static CompoundTag toBlockTag(String input) {
		int index = input.indexOf("[");
		CompoundTag tag = new CompoundTag();
		if (index < 0) {
			tag.putString("Name", Key.key(input).toString());
			return tag;
		}
		
		tag.putString("Name", Key.key(input.substring(0, index)).toString());
		
		String[] states = input.substring(index + 1, input.lastIndexOf("]")).replace(" ", "").split(",");
		
		CompoundTag properties = new CompoundTag();
		for (String state : states) {
			String key = state.substring(0, state.indexOf("="));
			String value = state.substring(state.indexOf("=") + 1);
			properties.putString(key, value);
		}
		
		tag.put("Properties", properties);

		return tag;
	}

}

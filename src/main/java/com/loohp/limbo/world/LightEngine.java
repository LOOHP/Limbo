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

import java.util.HashMap;
import java.util.Map;

public abstract class LightEngine {
	
	private static Map<String, Byte> blockLightLevelMapping = new HashMap<>();
	
	static {
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:torch", (byte) 15);
		blockLightLevelMapping.put("minecraft:sea_lantern", (byte) 15);
		blockLightLevelMapping.put("minecraft:end_rod", (byte) 15);
		blockLightLevelMapping.put("minecraft:fire", (byte) 15);
		blockLightLevelMapping.put("minecraft:lava", (byte) 15);
		blockLightLevelMapping.put("minecraft:lantern", (byte) 15);
		blockLightLevelMapping.put("minecraft:soul_lantern", (byte) 10);
		blockLightLevelMapping.put("minecraft:glowstone", (byte) 15);
		blockLightLevelMapping.put("minecraft:campfire", (byte) 15);
		blockLightLevelMapping.put("minecraft:soul_campfire", (byte) 10);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
		
	}
	
	public static int getBlockLight(BlockState block) {
		return blockLightLevelMapping.getOrDefault(block.getType().toString(), (byte) 0);
	}

}

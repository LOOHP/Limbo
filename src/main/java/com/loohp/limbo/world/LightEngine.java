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

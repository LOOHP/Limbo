package com.loohp.limbo.world;

import java.util.HashMap;
import java.util.Map;

public abstract class LightEngine {

    private static final Map<String, Byte> blockLightLevelMapping = new HashMap<>();

    static {
        blockLightLevelMapping.put("minecraft:beacon", (byte) 15);
        blockLightLevelMapping.put("minecraft:torch", (byte) 15);
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

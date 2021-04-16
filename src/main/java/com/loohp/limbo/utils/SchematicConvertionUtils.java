package com.loohp.limbo.utils;

import net.querz.nbt.tag.CompoundTag;

public class SchematicConvertionUtils {

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
            tag.putString("Name", new NamespacedKey(input).toString());
            return tag;
        }

        tag.putString("Name", new NamespacedKey(input.substring(0, index)).toString());

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

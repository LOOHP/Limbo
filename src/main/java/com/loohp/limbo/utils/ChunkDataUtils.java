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

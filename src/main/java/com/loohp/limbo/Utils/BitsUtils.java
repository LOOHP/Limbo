package com.loohp.limbo.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class BitsUtils {

	public static BitSet shiftAfter(BitSet bitset, int from, int shift) {
		BitSet subset = bitset.get(from, bitset.length());
		for (int i = 0; i < subset.length(); i++) {
			bitset.set(from + shift + i, subset.get(i));
		}
		if (shift > 0) {
			for (int i = 0; i < shift; i++) {
				bitset.set(from + i, false);
			}
		}
		return bitset;
	}
	
	public static String toLongString(BitSet bitset) {
        List<String> list = new ArrayList<>();
        for (long l : bitset.toLongArray()) {
        	list.add(Long.toBinaryString(l));
        }
        return Arrays.toString(list.toArray());
    }

}

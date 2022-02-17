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

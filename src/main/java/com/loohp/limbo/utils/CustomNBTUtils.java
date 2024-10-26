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

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CustomNBTUtils {
	
	public static CompoundTag getCompoundTagFromJson(JSONObject json) {
		CompoundTag tag = new CompoundTag();
		
		for (Object obj : json.keySet()) {
			String key = (String) obj;
			Object rawValue = json.get(key);

			if (rawValue instanceof JSONObject) {
				tag.put(key, getCompoundTagFromJson((JSONObject) rawValue));
			} else if (rawValue instanceof JSONArray) {
				tag.put(key, getListTagFromJson((JSONArray) rawValue));
			} else if (rawValue instanceof Boolean) {
				tag.putBoolean(key, (boolean) rawValue);
			} else if (rawValue instanceof Long) {
				tag.putLong(key, (long) rawValue);
			} else if (rawValue instanceof Double) {
				tag.putDouble(key, (double) rawValue);
			} else if (rawValue instanceof String) {
				tag.putString(key, (String) rawValue);
			}
		}
		
		return tag;
	}
	
	public static ListTag<?> getListTagFromJson(JSONArray json) {
		if (json.isEmpty()) {
			return ListTag.createUnchecked(null);
		}
		ListTag<?> listTag = ListTag.createUnchecked(null);
		for (Object rawValue : json) {
			if (rawValue instanceof JSONObject) {
				listTag.addUnchecked(getCompoundTagFromJson((JSONObject) rawValue));
			} else if (rawValue instanceof JSONArray) {
				listTag.addUnchecked(getListTagFromJson((JSONArray) rawValue));
			} else if (rawValue instanceof Boolean) {
				listTag.addBoolean((boolean) rawValue);
			} else if (rawValue instanceof Long) {
				listTag.addLong((long) rawValue);
			} else if (rawValue instanceof Double) {
				listTag.addDouble((double) rawValue);
			} else if (rawValue instanceof String) {
				listTag.addString((String) rawValue);
			}
		}
		return listTag;
	}

}

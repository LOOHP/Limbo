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

import net.kyori.adventure.key.Key;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class BlockState {
	
	private CompoundTag tag;
	
	public BlockState(CompoundTag tag) {
		this.tag = tag;
	}
	
	public CompoundTag toCompoundTag() {
		return tag;
	}
	
	public Key getType() {
		return Key.key(tag.getString("Name"));
	}
	
	public void setType(Key Key) {
		tag.putString("Name", Key.toString());
	}
	
	public Map<String, String> getProperties() {
		Map<String, String> mapping = new HashMap<>();
		for (Entry<String, Tag<?>> entry : tag.getCompoundTag("Properties")) {
			String key = entry.getKey();
			String value = ((StringTag) entry.getValue()).getValue();
			mapping.put(key, value);
		}
		return mapping;
	}
	
	public String getProperty(String key) {
		Tag<?> value = tag.getCompoundTag("Properties").get(key);
		return value == null ? null : ((StringTag) value).getValue();
	}
	
	public void setProperties(Map<String, String> mapping) {
		CompoundTag properties = new CompoundTag();
		for (Entry<String, String> entry : mapping.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			properties.putString(key, value);
		}
		tag.put("Properties", properties);
	}
	
	public <T> void setProperty(String key, T value) {
		CompoundTag properties = tag.getCompoundTag("Properties");
		properties.putString(key, ((T) value).toString());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockState that = (BlockState) o;
		return Objects.equals(tag, that.tag);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tag);
	}
}

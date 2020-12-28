package com.loohp.limbo.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.loohp.limbo.Utils.NamespacedKey;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

public class BlockState {
	
	private CompoundTag tag;
	
	public BlockState(CompoundTag tag) {
		this.tag = tag;
	}
	
	public CompoundTag toCompoundTag() {
		return tag;
	}
	
	public NamespacedKey getType() {
		return new NamespacedKey(tag.getString("Name"));
	}
	
	public void setType(NamespacedKey namespacedKey) {
		tag.putString("Name", namespacedKey.toString());
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BlockState other = (BlockState) obj;
		if (tag == null) {
			if (other.tag != null) {
				return false;
			}
		} else if (!tag.equals(other.tag)) {
			return false;
		}
		return true;
	}

}

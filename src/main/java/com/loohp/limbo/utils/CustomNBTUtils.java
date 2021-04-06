package com.loohp.limbo.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.LongArrayTag;
import net.querz.nbt.tag.LongTag;
import net.querz.nbt.tag.ShortTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

@SuppressWarnings("rawtypes")
public class CustomNBTUtils {
	
	public enum TagClass {
		CompoundTagClass(CompoundTag.class),
		ByteTagClass(ByteTag.class),
		ShortTagClass(ShortTag.class),
		IntTagClass(IntTag.class),
		LongTagClass(LongTag.class),
		FloatTagClass(FloatTag.class),
		DoubleTagClass(DoubleTag.class),
		ByteArrayTagClass(ByteArrayTag.class),
		IntArrayTagClass(IntArrayTag.class),
		LongArrayTagClass(LongArrayTag.class),
		StringTagClass(StringTag.class),
		ListTagClass(ListTag.class);
		
		Class<? extends Tag> clazz;
		
		TagClass(Class<? extends Tag> clazz) {
			this.clazz = clazz;
		}
		
		public Class<? extends Tag> getTagClass() {
			return clazz;
		}
	}
	
	public static Class<? extends Tag> getClassFromName(String name) {
		for (TagClass clazz : TagClass.values()) {
			if (clazz.getTagClass().getSimpleName().equals(name)) {
				return clazz.getTagClass();
			}
		}
		return null;
	}
	
	public static CompoundTag getCompoundTagFromJson(JSONObject json) {
		CompoundTag tag = new CompoundTag();
		
		for (Object obj : json.keySet()) {
			String key = (String) obj;
			JSONObject inside = (JSONObject) json.get(key);
			String type = (String) inside.get("type");
			
			switch (type) {
			case "ByteTag":
				tag.putByte(key, (byte) (long) inside.get("value"));
				break;
			case "ShortTag":
				tag.putShort(key, (short) (long) inside.get("value"));
				break;
			case "IntTag":
				tag.putInt(key, (int) (long) inside.get("value"));
				break;
			case "LongTag":
				tag.putLong(key, (long) inside.get("value"));
				break;
			case "FloatTag":
				tag.putFloat(key, inside.get("value") instanceof Long ? (float) (long) inside.get("value") : (float) (double) inside.get("value"));
				break;
			case "DoubleTag":
				tag.putDouble(key, inside.get("value") instanceof Long ? (double) (long) inside.get("value") : (double) inside.get("value"));
				break;
			case "ByteArrayTag":
				tag.putByteArray(key, CustomArrayUtils.longArrayToByteArray((long[]) inside.get("value")));
				break;
			case "IntArrayTag":
				tag.putIntArray(key, CustomArrayUtils.longArrayToIntArray((long[]) inside.get("value")));
				break;
			case "LongArrayTag":
				tag.putLongArray(key, (long[]) inside.get("value"));
				break;
			case "StringTag":
				tag.putString(key, (String) inside.get("value"));
				break;
			case "CompoundTag":
				tag.put(key, getCompoundTagFromJson((JSONObject) inside.get("value")));
				break;
			case "ListTag":
				tag.put(key, getListTagFromJson((JSONObject) inside.get("value")));
				break;
			}
		}
		
		return tag;
	}
	
	public static ListTag<?> getListTagFromJson(JSONObject json) {
		String type = (String) json.get("type");
		JSONArray array = (JSONArray) json.get("list");
		
		ListTag<?> listTag = ListTag.createUnchecked(getClassFromName(type));
		
		switch (type) {
		case "ByteTag":
			for (Object obj : array) {
				listTag.addByte((byte) (long) obj);
			}
			break;
		case "ShortTag":
			for (Object obj : array) {
				listTag.addShort((short) (long) obj);
			}
			break;
		case "IntTag":
			for (Object obj : array) {
				listTag.addInt((int) (long) obj);
			}
			break;
		case "LongTag":
			for (Object obj : array) {
				listTag.addLong((long) obj);
			}
			break;
		case "FloatTag":
			for (Object obj : array) {
				listTag.addFloat(obj instanceof Long ? (float) (long) obj : (float) (double) obj);
			}
			break;
		case "DoubleTag":
			for (Object obj : array) {
				listTag.addDouble(obj instanceof Long ? (double) (long) obj : (double) obj);
			}
			break;
		case "ByteArrayTag":
			for (Object obj : array) {
				listTag.addByteArray(CustomArrayUtils.longArrayToByteArray((long[]) obj));
			}
			break;
		case "IntArrayTag":
			for (Object obj : array) {
				listTag.addIntArray(CustomArrayUtils.longArrayToIntArray((long[]) obj));
			}
			break;
		case "LongArrayTag":
			for (Object obj : array) {
				listTag.addLongArray((long[]) obj);
			}
			break;
		case "StringTag":
			for (Object obj : array) {
				listTag.addString((String) obj);
			}
			break;
		case "CompoundTag":
			for (Object obj : array) {
				listTag.asCompoundTagList().add(getCompoundTagFromJson((JSONObject) obj));
			}
			break;
		case "ListTag":
			for (Object obj : array) {
				listTag.asListTagList().add(getListTagFromJson((JSONObject) obj));
			}
			break;
		}
		
		return listTag;
	}

}

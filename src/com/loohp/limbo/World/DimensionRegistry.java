package com.loohp.limbo.World;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class DimensionRegistry {
	
	public static CompoundTag defaultTag;
	
	static {
		resetTag();
	}
	
	public static void resetTag() {
		CompoundTag overworld = new CompoundTag();
		overworld.putString("name", "minecraft:overworld");
		overworld.putByte("natural", (byte) 1);
		overworld.putFloat("ambient_light", 0.4F);
		overworld.putByte("has_ceiling", (byte) 0);
		overworld.putByte("has_skylight", (byte) 1);
		overworld.putLong("fixed_time", 0);
		overworld.putByte("shrunk", (byte) 0);
		overworld.putByte("ultrawarm", (byte) 0);
		overworld.putByte("has_raids", (byte) 1);
		overworld.putByte("respawn_anchor_works", (byte) 0);
		overworld.putByte("bed_works", (byte) 1);
		overworld.putByte("piglin_safe", (byte) 0);
		overworld.putInt("logical_height", 256);
		overworld.putString("infiniburn", "");
		
		CompoundTag nether = new CompoundTag();
		nether.putString("name", "minecraft:the_nether");
		nether.putByte("natural", (byte) 0);
		nether.putFloat("ambient_light", 0.3F);
		nether.putByte("has_ceiling", (byte) 1);
		nether.putByte("has_skylight", (byte) 0);
		nether.putLong("fixed_time", 0);
		nether.putByte("shrunk", (byte) 1);
		nether.putByte("ultrawarm", (byte) 1);
		nether.putByte("has_raids", (byte) 0);
		nether.putByte("respawn_anchor_works", (byte) 1);
		nether.putByte("bed_works", (byte) 0);
		nether.putByte("piglin_safe", (byte) 1);
		nether.putInt("logical_height", 128);
		nether.putString("infiniburn", "");
		
		CompoundTag the_end = new CompoundTag();
		the_end.putString("name", "minecraft:the_end");
		the_end.putByte("natural", (byte) 0);
		the_end.putFloat("ambient_light", 0.3F);
		the_end.putByte("has_ceiling", (byte) 0);
		the_end.putByte("has_skylight", (byte) 0);
		the_end.putLong("fixed_time", 0);
		the_end.putByte("shrunk", (byte) 0);
		the_end.putByte("ultrawarm", (byte) 0);
		the_end.putByte("has_raids", (byte) 0);
		the_end.putByte("respawn_anchor_works", (byte) 0);
		the_end.putByte("bed_works", (byte) 0);
		the_end.putByte("piglin_safe", (byte) 1);
		the_end.putInt("logical_height", 256);
		the_end.putString("infiniburn", "");
		
		ListTag<CompoundTag> listtag = new ListTag<CompoundTag>(CompoundTag.class);
		listtag.add(overworld);
		listtag.add(nether);
		listtag.add(the_end);
		
		CompoundTag dimensionTag = new CompoundTag();
		dimensionTag.put("dimension", listtag);
		
		defaultTag = dimensionTag;
	}
	
	public static CompoundTag getCodec() {		
		return defaultTag;
	}

}

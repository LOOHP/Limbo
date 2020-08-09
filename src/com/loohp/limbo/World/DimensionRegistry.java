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
		overworld.putFloat("ambient_light", 0.0F);
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
		overworld.putString("infiniburn", "minecraft:infiniburn_overworld");
		
		CompoundTag overworld_caves = new CompoundTag();
		overworld_caves.putString("name", "minecraft:overworld_caves");
		overworld_caves.putByte("natural", (byte) 1);
		overworld_caves.putFloat("ambient_light", 0.0F);
		overworld_caves.putByte("has_ceiling", (byte) 1);
		overworld_caves.putByte("has_skylight", (byte) 1);
		overworld_caves.putLong("fixed_time", 0);
		overworld_caves.putByte("shrunk", (byte) 0);
		overworld_caves.putByte("ultrawarm", (byte) 0);
		overworld_caves.putByte("has_raids", (byte) 1);
		overworld_caves.putByte("respawn_anchor_works", (byte) 0);
		overworld_caves.putByte("bed_works", (byte) 1);
		overworld_caves.putByte("piglin_safe", (byte) 0);
		overworld_caves.putInt("logical_height", 256);
		overworld_caves.putString("infiniburn", "minecraft:infiniburn_overworld");
		
		CompoundTag nether = new CompoundTag();
		nether.putString("name", "minecraft:the_nether");
		nether.putByte("natural", (byte) 0);
		nether.putFloat("ambient_light", 0.1F);
		nether.putByte("has_ceiling", (byte) 1);
		nether.putByte("has_skylight", (byte) 0);
		nether.putLong("fixed_time", 18000);
		nether.putByte("shrunk", (byte) 1);
		nether.putByte("ultrawarm", (byte) 1);
		nether.putByte("has_raids", (byte) 0);
		nether.putByte("respawn_anchor_works", (byte) 1);
		nether.putByte("bed_works", (byte) 0);
		nether.putByte("piglin_safe", (byte) 1);
		nether.putInt("logical_height", 128);
		nether.putString("infiniburn", "minecraft:infiniburn_nether");
		
		CompoundTag the_end = new CompoundTag();
		the_end.putString("name", "minecraft:the_end");
		the_end.putByte("natural", (byte) 0);
		the_end.putFloat("ambient_light", 0.0F);
		the_end.putByte("has_ceiling", (byte) 0);
		the_end.putByte("has_skylight", (byte) 0);
		the_end.putLong("fixed_time", 6000);
		the_end.putByte("shrunk", (byte) 0);
		the_end.putByte("ultrawarm", (byte) 0);
		the_end.putByte("has_raids", (byte) 1);
		the_end.putByte("respawn_anchor_works", (byte) 0);
		the_end.putByte("bed_works", (byte) 0);
		the_end.putByte("piglin_safe", (byte) 0);
		the_end.putInt("logical_height", 256);
		the_end.putString("infiniburn", "minecraft:infiniburn_end");
		
		ListTag<CompoundTag> listtag = new ListTag<CompoundTag>(CompoundTag.class);
		listtag.add(overworld);
		listtag.add(overworld_caves);
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

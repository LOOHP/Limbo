package com.loohp.limbo.World;

import com.loohp.limbo.Utils.NamespacedKey;

public class Environment {
	
	public static final Environment NORMAL = new Environment(new NamespacedKey("minecraft:overworld"));
	public static final Environment NETHER = new Environment(new NamespacedKey("minecraft:the_nether"));
	public static final Environment END = new Environment(new NamespacedKey("minecraft:the_end"));
	
	public static Environment fromNamespacedKey(NamespacedKey key) {
		if (key.equals(NORMAL.getNamespacedKey())) {
			return NORMAL;
		} else if (key.equals(NETHER.getNamespacedKey())) {
			return NETHER;
		} else if (key.equals(END.getNamespacedKey())) {
			return END;
		}
		return null;
	}
	
	public static Environment createCustom(NamespacedKey key) {
		return new Environment(key);
	}
	
	//=========================
	
	private NamespacedKey key;
	
	private Environment(NamespacedKey key) {
		this.key = key;
	}
	
	public NamespacedKey getNamespacedKey() {
		return key;
	}
}
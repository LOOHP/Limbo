package com.loohp.limbo.World;

import java.util.HashSet;
import java.util.Set;

import com.loohp.limbo.Utils.NamespacedKey;

public class Environment {

	public static final Environment NORMAL = new Environment(new NamespacedKey("minecraft:overworld"), true);
	public static final Environment NETHER = new Environment(new NamespacedKey("minecraft:the_nether"), false);
	public static final Environment END = new Environment(new NamespacedKey("minecraft:the_end"), false);
	
	public static final Set<Environment> REGISTERED_ENVIRONMENTS = new HashSet<>();

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
	
	@Deprecated
	public static Environment createCustom(NamespacedKey key) {
		return createCustom(key, true);
	}
	
	public static Environment createCustom(NamespacedKey key, boolean hasSkyLight) {
		if (REGISTERED_ENVIRONMENTS.stream().anyMatch(each -> each.getNamespacedKey().equals(key))) {
			throw new IllegalArgumentException("An Environment is already created with this NamespacedKey");
		}
		return new Environment(key, hasSkyLight);
	}
	
	public static Environment getCustom(NamespacedKey key) {
		return REGISTERED_ENVIRONMENTS.stream().filter(each -> each.getNamespacedKey().equals(key)).findFirst().orElse(null); 
	}
	
	//=========================
	
	private NamespacedKey key;
	private boolean hasSkyLight;
	
	private Environment(NamespacedKey key, boolean hasSkyLight) {
		this.key = key;
		this.hasSkyLight = hasSkyLight;
	}
	
	public NamespacedKey getNamespacedKey() {
		return key;
	}
	
	public boolean hasSkyLight() {
		return hasSkyLight;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (hasSkyLight ? 1231 : 1237);
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		Environment other = (Environment) obj;
		if (hasSkyLight != other.hasSkyLight) {
			return false;
		}
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		return true;
	}
}
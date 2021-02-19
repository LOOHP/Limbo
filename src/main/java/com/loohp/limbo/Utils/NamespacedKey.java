package com.loohp.limbo.Utils;

public class NamespacedKey {
	
	public static final String MINECRAFT_KEY = "minecraft";

	private String namespace;
	private String key;

	public NamespacedKey(String namespacedKey) {
		int index = namespacedKey.indexOf(":");
		if (index >= 0) {
			this.namespace = namespacedKey.substring(0, index);
			this.key = namespacedKey.substring(index + 1);
		} else {
			this.namespace = "minecraft";
			this.key = namespacedKey;
		}
	}

	public NamespacedKey(String namespace, String key) {
		this.namespace = namespace;
		this.key = key;
	}
	
	public static NamespacedKey minecraft(String key) {
		return new NamespacedKey(MINECRAFT_KEY, key);
	}

	public String getNamespace() {
		return namespace;
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return namespace + ":" + key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NamespacedKey other = (NamespacedKey) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}

}

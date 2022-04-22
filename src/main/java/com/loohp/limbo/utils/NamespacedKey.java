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
			this.namespace = MINECRAFT_KEY;
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

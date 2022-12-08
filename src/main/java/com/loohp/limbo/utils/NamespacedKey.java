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

import com.loohp.limbo.plugins.LimboPlugin;
import net.kyori.adventure.key.Key;

import java.util.Objects;

public class NamespacedKey {
	
	public static final String MINECRAFT_KEY = "minecraft";

	public static NamespacedKey minecraft(String key) {
		return new NamespacedKey(MINECRAFT_KEY, key);
	}

	public static NamespacedKey fromKey(Key key) {
		return new NamespacedKey(key.namespace(), key.value());
	}

	private final String namespace;
	private final String key;

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

	public NamespacedKey(LimboPlugin plugin, String key) {
		this(plugin.getName().toLowerCase().replace(" ", "_"), key);
	}

	public NamespacedKey(String namespace, String key) {
		this.namespace = namespace;
		this.key = key;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getKey() {
		return key;
	}

	public Key toKey() {
		return Key.key(namespace, key);
	}

	@Override
	public String toString() {
		return namespace + ":" + key;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NamespacedKey that = (NamespacedKey) o;
		return namespace.equals(that.namespace) && key.equals(that.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(namespace, key);
	}
}

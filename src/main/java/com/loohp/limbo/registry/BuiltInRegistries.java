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

package com.loohp.limbo.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.loohp.limbo.Limbo;
import net.kyori.adventure.key.Key;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class BuiltInRegistries {
	
	public static final BlockEntityRegistry BLOCK_ENTITY_TYPE;
	public static final ItemRegistry ITEM_REGISTRY;
	public static final MenuRegistry MENU_REGISTRY;
	public static final DataComponentTypeRegistry DATA_COMPONENT_TYPE;
	
	static {
		String name = "reports/registries.json";
        
        Map<Key, Integer> blockEntityType = new HashMap<>();
		Key defaultItemKey = null;
		BiMap<Key, Integer> itemIds = HashBiMap.create();
		Map<Key, Integer> menuIds = new HashMap<>();
		BiMap<Key, Integer> dataComponentTypeIds = HashBiMap.create();

		InputStream inputStream = Limbo.class.getClassLoader().getResourceAsStream(name);
		if (inputStream == null) {
			throw new RuntimeException("Failed to load " + name + " from jar!");
		}
		try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
			JSONObject json = (JSONObject) new JSONParser().parse(reader);
			
			JSONObject blockEntityJson = (JSONObject) ((JSONObject) json.get("minecraft:block_entity_type")).get("entries");
			for (Object obj : blockEntityJson.keySet()) {
				String key = obj.toString();
				int id = ((Number) ((JSONObject) blockEntityJson.get(key)).get("protocol_id")).intValue();
				blockEntityType.put(Key.key(key), id);
			}

			JSONObject itemJson = (JSONObject) json.get("minecraft:item");
			defaultItemKey = Key.key((String) itemJson.get("default"));
			JSONObject itemEntriesJson = (JSONObject) itemJson.get("entries");
			for (Object obj : itemEntriesJson.keySet()) {
				String key = obj.toString();
				int id = ((Number) ((JSONObject) itemEntriesJson.get(key)).get("protocol_id")).intValue();
				itemIds.put(Key.key(key), id);
			}

			JSONObject menuEntriesJson = (JSONObject) ((JSONObject) json.get("minecraft:menu")).get("entries");
			for (Object obj : menuEntriesJson.keySet()) {
				String key = obj.toString();
				int id = ((Number) ((JSONObject) menuEntriesJson.get(key)).get("protocol_id")).intValue();
				menuIds.put(Key.key(key), id);
			}

			JSONObject dataComponentTypeEntriesJson = (JSONObject) ((JSONObject) json.get("minecraft:data_component_type")).get("entries");
			for (Object obj : dataComponentTypeEntriesJson.keySet()) {
				String key = obj.toString();
				int id = ((Number) ((JSONObject) dataComponentTypeEntriesJson.get(key)).get("protocol_id")).intValue();
				dataComponentTypeIds.put(Key.key(key), id);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
        BLOCK_ENTITY_TYPE = new BlockEntityRegistry(blockEntityType);
		ITEM_REGISTRY = new ItemRegistry(defaultItemKey, itemIds);
		MENU_REGISTRY = new MenuRegistry(menuIds);
		DATA_COMPONENT_TYPE = new DataComponentTypeRegistry(dataComponentTypeIds);
	}

	public abstract int getId(Key key);
	
    public static class BlockEntityRegistry extends BuiltInRegistries {

    	private Map<Key, Integer> blockEntityType;
    	
    	private BlockEntityRegistry(Map<Key, Integer> blockEntityType) {
    		this.blockEntityType = blockEntityType;
    	}

		@Override
    	public int getId(Key key) {
    		Integer exact = blockEntityType.get(key);
    		if (exact != null) {
    			return exact;
    		}
    		List<String> toTest = new LinkedList<>();
    		toTest.add(key.value());
    		if (key.value().contains("head")) {
    			toTest.add("skull");
    		}
    		for (Entry<Key, Integer> entry : blockEntityType.entrySet()) {
    			Key Key = entry.getKey();
    			for (String each : toTest) {
    				if (Key.namespace().equals(key.namespace()) && (each.contains(Key.value()) || Key.value().contains(each))) {
        				return entry.getValue();
        			}
    			}
    		}
    		return -1;
    	}
    }

	public static class ItemRegistry extends BuiltInRegistries {

		private final Key defaultKey;
		private final BiMap<Key, Integer> itemIds;

		private ItemRegistry(Key defaultKey, BiMap<Key, Integer> itemIds) {
			this.defaultKey = defaultKey;
			this.itemIds = itemIds;
		}

		public Key getDefaultKey() {
			return defaultKey;
		}

		@Override
		public int getId(Key key) {
			Integer id = itemIds.get(key);
			if (id != null) {
				return id;
			}
			if (defaultKey == null) {
				return 0;
			}
			return itemIds.getOrDefault(defaultKey, 0);
		}

		public Key fromId(int id) {
			return itemIds.inverse().getOrDefault(id, defaultKey);
		}
	}

	public static class MenuRegistry extends BuiltInRegistries {

		private final Map<Key, Integer> menuIds;

		private MenuRegistry(Map<Key, Integer> menuIds) {
			this.menuIds = menuIds;
		}

		@Override
		public int getId(Key key) {
			return menuIds.getOrDefault(key, -1);
		}
	}

	public static class DataComponentTypeRegistry extends BuiltInRegistries {

		private final BiMap<Key, Integer> dataComponentTypeIds;

		private DataComponentTypeRegistry(BiMap<Key, Integer> dataComponentTypeIds) {
			this.dataComponentTypeIds = dataComponentTypeIds;
		}

		@Override
		public int getId(Key key) {
			return dataComponentTypeIds.getOrDefault(key, -1);
		}

		public Key fromId(int id) {
			return dataComponentTypeIds.inverse().get(id);
		}
	}

}

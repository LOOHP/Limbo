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

import com.loohp.limbo.Limbo;
import net.kyori.adventure.key.Key;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Registry {
	
	public static final BlockEntityRegistry BLOCK_ENTITY_TYPE;
	
	static {
		String name = "registries.json";
        File file = new File(Limbo.getInstance().getInternalDataFolder(), name);
        if (!file.exists()) {
        	try (InputStream in = Limbo.class.getClassLoader().getResourceAsStream(name)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        
        Map<Key, Integer> blockEntityType = new HashMap<>();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			JSONObject json = (JSONObject) new JSONParser().parse(reader);
			
			JSONObject blockEntityJson = (JSONObject) ((JSONObject) json.get("minecraft:block_entity_type")).get("entries");
			for (Object obj : blockEntityJson.keySet()) {
				String key = obj.toString();
				int id = (int) (long) ((JSONObject) blockEntityJson.get(key)).get("protocol_id");
				blockEntityType.put(Key.key(key), id);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
        BLOCK_ENTITY_TYPE = new BlockEntityRegistry(blockEntityType);
	}
	
    public static class BlockEntityRegistry {
    	
    	private Map<Key, Integer> blockEntityType;
    	
    	private BlockEntityRegistry(Map<Key, Integer> blockEntityType) {
    		this.blockEntityType = blockEntityType;
    	}
    	
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

}

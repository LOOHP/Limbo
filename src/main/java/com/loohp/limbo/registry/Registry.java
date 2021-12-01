package com.loohp.limbo.registry;

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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.utils.NamespacedKey;

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
        
        Map<NamespacedKey, Integer> blockEntityType = new HashMap<>();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			JSONObject json = (JSONObject) new JSONParser().parse(reader);
			
			JSONObject blockEntityJson = (JSONObject) ((JSONObject) json.get("minecraft:block_entity_type")).get("entries");
			for (Object obj : blockEntityJson.keySet()) {
				String key = obj.toString();
				int id = (int) (long) ((JSONObject) blockEntityJson.get(key)).get("protocol_id");
				blockEntityType.put(new NamespacedKey(key), id);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
        BLOCK_ENTITY_TYPE = new BlockEntityRegistry(blockEntityType);
	}
	
    public static class BlockEntityRegistry {
    	
    	private Map<NamespacedKey, Integer> blockEntityType;
    	
    	private BlockEntityRegistry(Map<NamespacedKey, Integer> blockEntityType) {
    		this.blockEntityType = blockEntityType;
    	}
    	
    	public int getId(NamespacedKey key) {
    		Integer exact = blockEntityType.get(key);
    		if (exact != null) {
    			return exact;
    		}
    		List<String> toTest = new LinkedList<>();
    		toTest.add(key.getKey());
    		if (key.getKey().contains("head")) {
    			toTest.add("skull");
    		}
    		for (Entry<NamespacedKey, Integer> entry : blockEntityType.entrySet()) {
    			NamespacedKey namespacedKey = entry.getKey();
    			for (String each : toTest) {
    				if (namespacedKey.getNamespace().equals(key.getNamespace()) && (each.contains(namespacedKey.getKey()) || namespacedKey.getKey().contains(each))) {
        				return entry.getValue();
        			}
    			}
    		}
    		return -1;
    	}
    }

}

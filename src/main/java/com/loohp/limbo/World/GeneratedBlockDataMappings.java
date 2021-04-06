package com.loohp.limbo.world;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.loohp.limbo.Limbo;

import net.querz.nbt.tag.CompoundTag;

public class GeneratedBlockDataMappings {
	
	private static JSONObject globalPalette = new JSONObject();
	
	static {
		String block = "blocks.json";
        File file = new File(Limbo.getInstance().getInternalDataFolder(), block);
        if (!file.exists()) {
        	try (InputStream in = Limbo.class.getClassLoader().getResourceAsStream(block)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        
        try {
        	globalPalette = (JSONObject) new JSONParser().parse(new FileReader(file));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("unchecked")
	public static int getGlobalPaletteIDFromState(CompoundTag tag) {
		String blockname = tag.getString("Name");
		
		JSONObject data = (JSONObject) globalPalette.get(blockname);
		Object obj = data.get("properties");
		if (obj == null) {
			return (int) (long) ((JSONObject) ((JSONArray) data.get("states")).get(0)).get("id");
		}
		
		//JSONObject properties = (JSONObject) obj;
		
		if (tag.containsKey("Properties")) {
			CompoundTag blockProp = tag.get("Properties", CompoundTag.class);
			Map<String, String> blockstate = new HashMap<>();
			for (String key : blockProp.keySet()) {
				blockstate.put(key, blockProp.getString(key));
			}
			
			for (Object entry : (JSONArray) data.get("states")) {
				JSONObject jsonobj = (JSONObject) entry;
				if (((JSONObject) jsonobj.get("properties")).keySet().stream().allMatch(key -> blockstate.get(key).equals((String) (((JSONObject) jsonobj.get("properties")).get(key))))) {
					return (int) (long) jsonobj.get("id");
				}
			}
		}
		
		for (Object entry : (JSONArray) data.get("states")) {
			if (((JSONObject) entry).containsKey("default") && ((boolean) ((JSONObject) entry).get("default"))) {
				return (int) (long) ((JSONObject) entry).get("id");
			}
		}
		return 0;
	}

}

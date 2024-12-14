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

package com.loohp.limbo.world;

import com.loohp.limbo.Limbo;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GeneratedBlockDataMappings {
	
	private static JSONObject globalPalette = new JSONObject();
	
	static {
		String block = "reports/blocks.json";
		InputStream inputStream = Limbo.class.getClassLoader().getResourceAsStream(block);
		if (inputStream == null) {
			throw new RuntimeException("Failed to load " + block + " from jar!");
		}
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
        	globalPalette = (JSONObject) new JSONParser().parse(reader);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("unchecked")
	public static int getGlobalPaletteIDFromState(CompoundTag tag) {
		try {
			String blockname = tag.getString("Name");

			JSONObject data = (JSONObject) globalPalette.get(blockname);
			Object obj = data.get("properties");
			if (obj == null) {
				return ((Number) ((JSONObject) ((JSONArray) data.get("states")).get(0)).get("id")).intValue();
			}

			if (tag.containsKey("Properties")) {
				CompoundTag blockProp = tag.get("Properties", CompoundTag.class);
				Map<String, String> blockstate = new HashMap<>();
				for (String key : blockProp.keySet()) {
					blockstate.put(key, blockProp.getString(key));
				}

				for (Object entry : (JSONArray) data.get("states")) {
					JSONObject jsonobj = (JSONObject) entry;
					if (((JSONObject) jsonobj.get("properties")).keySet().stream().allMatch(key -> Objects.equals(blockstate.get(key), ((JSONObject) jsonobj.get("properties")).get(key)))) {
						return ((Number) jsonobj.get("id")).intValue();
					}
				}
			}

			for (Object entry : (JSONArray) data.get("states")) {
				if (((JSONObject) entry).containsKey("default") && ((boolean) ((JSONObject) entry).get("default"))) {
					return ((Number) ((JSONObject) entry).get("id")).intValue();
				}
			}

			throw new IllegalStateException();
		} catch (Throwable e) {
			String snbt;
            try {
				snbt = SNBTUtil.toSNBT(tag);
            } catch (IOException e1) {
				snbt = tag.valueToString();
            }
			new IllegalStateException("Unable to get global palette id for " + snbt + " (Is this scheme created in the same Minecraft version as Limbo?)", e).printStackTrace();
        }
		return 0;
	}

}

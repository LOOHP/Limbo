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
import com.loohp.limbo.utils.CustomNBTUtils;
import net.querz.nbt.tag.CompoundTag;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class DimensionRegistry {
	
	private CompoundTag defaultTag;
	private CompoundTag codec;
	
	public DimensionRegistry() {
		this.defaultTag = new CompoundTag();
		String name = "dimension_registry.json";

		InputStream inputStream = Limbo.class.getClassLoader().getResourceAsStream(name);
		if (inputStream == null) {
			throw new RuntimeException("Failed to load " + name + " from jar!");
		}
		try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
			JSONObject json = (JSONObject) new JSONParser().parse(reader);
			CompoundTag tag = CustomNBTUtils.getCompoundTagFromJson((JSONObject) json.get("value"));
			defaultTag = tag;
			codec = defaultTag.clone();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void resetCodec() {
		codec = defaultTag.clone();
	}
	
	public CompoundTag getCodec() {		
		return codec;
	}

}

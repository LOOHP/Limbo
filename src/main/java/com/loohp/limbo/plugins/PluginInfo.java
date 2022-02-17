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

package com.loohp.limbo.plugins;

import com.loohp.limbo.file.FileConfiguration;

public class PluginInfo {
	
	private String name;
	private String description;
	private String author;
	private String version;
	private String main;
	
	public PluginInfo(FileConfiguration file) {
		name = file.get("name", String.class);
		description = file.get("description", String.class) == null ? "" : file.get("description", String.class);
		author = file.get("author", String.class);
		version = file.get("version", String.class);
		main = file.get("main", String.class);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public String getAuthor() {
		return author;
	}

	public String getVersion() {
		return version;
	}

	public String getMainClass() {
		return main;
	}

}

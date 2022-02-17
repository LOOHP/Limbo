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

public enum GameMode {
	
	SURVIVAL(0, "survival"),
	CREATIVE(1, "creative"),
	ADVENTURE(2, "adventure"),
	SPECTATOR(3, "spectator");
	
	int id;
	String name;
	
	GameMode(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public static GameMode fromId(int id) {
		for (GameMode mode : GameMode.values()) {
			if (mode.getId() == id) {
				return mode;
			}
		}
		return null;
	}
	
	public static GameMode fromName(String name) {
		for (GameMode mode : GameMode.values()) {
			if (mode.getName().equalsIgnoreCase(name)) {
				return mode;
			}
		}
		return null;
	}

}

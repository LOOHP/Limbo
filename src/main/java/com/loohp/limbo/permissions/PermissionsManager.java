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

package com.loohp.limbo.permissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loohp.limbo.Console;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.file.FileConfiguration;
import com.loohp.limbo.player.Player;

public class PermissionsManager {
	
	private Map<String, List<String>> users;
	private Map<String, List<String>> permissions;
	
	public PermissionsManager() {
		users = new HashMap<>();
		permissions = new HashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	public void loadDefaultPermissionFile(File file) throws IOException {
		FileConfiguration config = new FileConfiguration(file);
		permissions.put("default", new ArrayList<>());
		try {
			for (Object obj : config.get("groups", Map.class).keySet()) {
				String key = (String) obj;
				List<String> nodes = new ArrayList<>();
				nodes.addAll(config.get("groups." + key, List.class));
				permissions.put(key, nodes);
			}
		} catch (Exception e) {}
		try {
			for (Object obj : config.get("players", Map.class).keySet()) {
				String key = (String) obj;
				List<String> groups = new ArrayList<>();
				groups.addAll(config.get("players." + key, List.class));
				users.put(key, groups);
			}
		} catch (Exception e) {}
	}
	
	public boolean hasPermission(CommandSender sender, String permission) {
		if (sender instanceof Console) {
			return true;
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			if (users.get(player.getName()) != null && users.get(player.getName()).stream().anyMatch(each -> permissions.get(each).stream().anyMatch(node -> node.equalsIgnoreCase(permission)))) {
				return true;
			} else {
				return permissions.get("default").stream().anyMatch(node -> node.equalsIgnoreCase(permission));
			}
		}
		return false;
	}

	public Map<String, List<String>> getUsers() {
		return users;
	}

	public Map<String, List<String>> getPermissions() {
		return permissions;
	}

}

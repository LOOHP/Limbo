package com.loohp.limbo.Permissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loohp.limbo.Console;
import com.loohp.limbo.Commands.CommandSender;
import com.loohp.limbo.File.FileConfiguration;
import com.loohp.limbo.Player.Player;

public class PermissionsManager {
	
	private Map<String, List<String>> users;
	private Map<String, List<String>> permissions;
	
	public PermissionsManager() {
		users = new HashMap<>();
		permissions = new HashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	public void loadDefaultPermissionFile(File file) throws FileNotFoundException {
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

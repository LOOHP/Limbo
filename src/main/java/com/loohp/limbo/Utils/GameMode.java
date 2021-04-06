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

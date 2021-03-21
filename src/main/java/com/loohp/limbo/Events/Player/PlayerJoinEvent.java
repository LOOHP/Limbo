package com.loohp.limbo.Events.Player;

import com.loohp.limbo.Location.Location;
import com.loohp.limbo.Player.Player;

public class PlayerJoinEvent extends PlayerEvent {

	private Location spawnLocation;

	public PlayerJoinEvent(Player player, Location spawnLoc) {
		super(player);
		spawnLocation = spawnLoc;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public void setSpawnLocation(Location spawnLocation) {
		this.spawnLocation = spawnLocation;
	}
}

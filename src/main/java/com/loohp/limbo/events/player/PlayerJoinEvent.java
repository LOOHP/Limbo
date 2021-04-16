package com.loohp.limbo.events.player;

import com.loohp.limbo.location.Location;
import com.loohp.limbo.player.Player;

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

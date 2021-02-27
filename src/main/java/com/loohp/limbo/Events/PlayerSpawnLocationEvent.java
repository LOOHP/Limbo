package com.loohp.limbo.Events;

import com.loohp.limbo.Location.Location;
import com.loohp.limbo.Player.Player;

public class PlayerSpawnLocationEvent extends PlayerEvent {
    private Location location;

    public PlayerSpawnLocationEvent(Player player, Location loc) {
        super(player);
        location = loc;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

package com.loohp.limbo.events.player;

import com.loohp.limbo.location.Location;
import com.loohp.limbo.player.Player;

public class PlayerTeleportEvent extends PlayerMoveEvent {

    public PlayerTeleportEvent(Player player, Location from, Location to) {
        super(player, from, to);
    }

}

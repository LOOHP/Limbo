package com.loohp.limbo.Events;

import com.loohp.limbo.Location.Location;
import com.loohp.limbo.Player.Player;

public class PlayerTeleportEvent extends PlayerMoveEvent {

	public PlayerTeleportEvent(Player player, Location from, Location to) {
		super(player, from, to);
	}

}

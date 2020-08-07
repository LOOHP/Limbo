package com.loohp.limbo.Events;

import com.loohp.limbo.Player.Player;

public class PlayerJoinEvent extends PlayerEvent {
	
	public PlayerJoinEvent(Player player) {
		this.player = player;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

}

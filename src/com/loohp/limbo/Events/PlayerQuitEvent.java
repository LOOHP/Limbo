package com.loohp.limbo.Events;

import com.loohp.limbo.Player.Player;

public class PlayerQuitEvent extends PlayerEvent {
	
	public PlayerQuitEvent(Player player) {
		this.player = player;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

}

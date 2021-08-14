package com.loohp.limbo.events.player;

import com.loohp.limbo.player.Player;

public class PlayerEvent {
	
	private final Player player;
	
	public PlayerEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}

}

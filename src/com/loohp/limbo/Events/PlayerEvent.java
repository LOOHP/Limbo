package com.loohp.limbo.Events;

import com.loohp.limbo.Player.Player;

public class PlayerEvent extends Event {
	
	private Player player;
	
	public PlayerEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}

}

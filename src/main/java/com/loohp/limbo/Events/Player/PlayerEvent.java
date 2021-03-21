package com.loohp.limbo.Events.Player;

import com.loohp.limbo.Events.Event;
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

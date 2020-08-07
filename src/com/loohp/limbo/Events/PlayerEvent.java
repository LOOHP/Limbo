package com.loohp.limbo.Events;

import com.loohp.limbo.Player.Player;

public abstract class PlayerEvent extends Event {
	
	protected Player player;
	
	public abstract Player getPlayer();

}

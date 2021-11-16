package com.loohp.limbo.events.player;

import com.loohp.limbo.player.Player;
import com.loohp.limbo.server.packets.PacketPlayInResourcePackStatus.EnumResourcePackStatus;

public class PlayerResourcePackStatusEvent extends PlayerEvent {
	
	private Player player;
	private EnumResourcePackStatus status;
	
	public PlayerResourcePackStatusEvent(Player player, EnumResourcePackStatus status) {
		super(player);	
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public EnumResourcePackStatus getStatus() {
		return status;
	}
}

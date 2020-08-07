package com.loohp.limbo.Events;

import com.loohp.limbo.Player.Player;

public class PlayerChatEvent extends PlayerEvent implements Cancellable {

	private String prefix;
	private String message;
	private boolean cancelled;

	public PlayerChatEvent(Player player, String prefix, String message, boolean cancelled) {
		this.player = player;
		this.prefix = prefix;
		this.message = message;
		this.cancelled = cancelled;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

}

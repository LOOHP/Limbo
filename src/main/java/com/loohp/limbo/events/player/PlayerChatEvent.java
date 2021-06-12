package com.loohp.limbo.events.player;

import com.loohp.limbo.events.Cancellable;
import com.loohp.limbo.player.Player;

public class PlayerChatEvent extends PlayerEvent implements Cancellable {

	private String format;
	private String message;
	private boolean cancelled;

	public PlayerChatEvent(Player player, String format, String message, boolean cancelled) {
		super(player);
		this.format = format;
		this.message = message;
		this.cancelled = cancelled;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
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

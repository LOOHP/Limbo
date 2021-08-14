package com.loohp.limbo.events.player;

import com.loohp.limbo.events.api.Event;
import com.loohp.limbo.events.api.EventFactory;
import com.loohp.limbo.player.Player;

public class PlayerChatEvent extends PlayerEvent {

	/**
	 * This callback will be invoked every time the server receives a chat message
	 *
	 * Cancelling this event will prevent the chat message from being sent to all connected clients
	 */
	public static final Event<PlayerChatEventCallback> PLAYER_CHAT_EVENT = EventFactory.createArrayBacked(PlayerChatEventCallback.class, (_1, cancel) -> cancel, callbacks -> (event, _isCancelled) -> {
		boolean isCancelled = _isCancelled;
		for (PlayerChatEventCallback callback : callbacks) {
			isCancelled = callback.onPlayerChat(event, isCancelled);
		}
		return isCancelled;
	});

	public interface PlayerChatEventCallback {
		/**
		 * Callback for the {@link PlayerChatEvent}
		 * This will initiate the event as non-cancelled
		 * @param event the chat event
		 * @return true to cancel the event, otherwise return false
		 */
		default boolean onPlayerChat(PlayerChatEvent event) {
			return this.onPlayerChat(event, false);
		}

		/**
		 * Callback for the {@link PlayerChatEvent}
		 * @param event the chat event
		 * @param isCancelled whether the event was cancelled before reaching this callback
		 * @return true to cancel the event, otherwise return false
		 */
		boolean onPlayerChat(PlayerChatEvent event, boolean isCancelled);
	}

	private String format;
	private String message;

	public PlayerChatEvent(Player player, String format, String message) {
		super(player);
		this.format = format;
		this.message = message;
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

}

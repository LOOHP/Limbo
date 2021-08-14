package com.loohp.limbo.events.player;

import com.loohp.limbo.events.api.Event;
import com.loohp.limbo.events.api.EventFactory;
import com.loohp.limbo.server.ClientConnection;
import net.md_5.bungee.api.chat.BaseComponent;

public class PlayerLoginEvent {

	/**
	 * Called when a player logs into the server (protocol-wise right before the Login Success Packet would be sent)
	 *
	 * Cancelling this event will prevent the player from joining the server
	 */
	public static Event<PlayerLoginEventCallback> PLAYER_LOGIN_EVENT = EventFactory.createArrayBacked(PlayerLoginEventCallback.class, (_1, cancel) -> cancel, callbacks -> (event, _isCancelled) -> {
		boolean isCancelled = _isCancelled;
		for (PlayerLoginEventCallback callback : callbacks) {
			isCancelled = callback.onPlayerLoginEvent(event, isCancelled);
		}
		return isCancelled;
	});

	public interface PlayerLoginEventCallback {
		/**
		 * Callback for the {@link PlayerLoginEvent}
		 * This will initiate the event as non-cancelled
		 * @param event the chat event
		 * @return true to cancel the event, otherwise return false
		 */
		default boolean onPlayerLoginEvent(PlayerLoginEvent event) {
			return this.onPlayerLoginEvent(event, false);
		}

		/**
		 * Callback for the {@link PlayerLoginEvent}
		 * @param event the login event
		 * @param isCancelled whether the event was cancelled before reaching this callback
		 * @return true to cancel the event, otherwise return false
		 */
		boolean onPlayerLoginEvent(PlayerLoginEvent event, boolean isCancelled);
	}
	
	private final ClientConnection connection;
	private BaseComponent[] cancelReason;
	
	public PlayerLoginEvent(ClientConnection connection, BaseComponent... cancelReason) {
		this.connection = connection;
		this.cancelReason = cancelReason;
	}

	public ClientConnection getConnection() {
		return connection;
	}		

	public BaseComponent[] getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(BaseComponent... cancelReason) {
		this.cancelReason = cancelReason;
	}

}

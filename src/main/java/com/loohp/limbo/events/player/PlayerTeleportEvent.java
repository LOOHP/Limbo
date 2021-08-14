package com.loohp.limbo.events.player;

import com.loohp.limbo.events.api.Event;
import com.loohp.limbo.events.api.EventFactory;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.player.Player;

public class PlayerTeleportEvent extends PlayerMoveEvent {

	/**
	 * Called when the player is about to teleport
	 *
	 * Cancelling this event will prevent the player from being teleported
	 */
	public static final Event<PlayerTeleportEventCallback> PLAYER_TELEPORT_EVENT = EventFactory.createArrayBacked(PlayerTeleportEventCallback.class, (_1, cancel) -> cancel, callbacks -> (event, _isCancelled) -> {
		boolean isCancelled = _isCancelled;
		for (PlayerTeleportEventCallback callback : callbacks) {
			isCancelled = callback.onPlayerTeleport(event, isCancelled);
		}
		return isCancelled;
	});

	public interface PlayerTeleportEventCallback {
		/**
		 * Callback for {@link PlayerTeleportEvent}
		 * This will initiate the event as non-cancelled
		 * @return true to cancel the event, otherwise return false
		 */
		default boolean onPlayerTeleport(PlayerTeleportEvent event) {
			return this.onPlayerTeleport(event, false);
		}

		/**
		 * Callback for {@link PlayerTeleportEvent}
		 * @param isCancelled whether the event was cancelled before reaching this callback
		 * @return true to cancel the event, otherwise return false
		 */
		boolean onPlayerTeleport(PlayerTeleportEvent event, boolean isCancelled);
	}

	public PlayerTeleportEvent(Player player, Location from, Location to) {
		super(player, from, to);
	}

}

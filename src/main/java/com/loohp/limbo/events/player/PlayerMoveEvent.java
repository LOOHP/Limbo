package com.loohp.limbo.events.player;

import com.loohp.limbo.events.api.Event;
import com.loohp.limbo.events.api.EventFactory;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.player.Player;

/**
 * Holds information for player movement events
 */
public class PlayerMoveEvent extends PlayerEvent {

    /**
     * Called when the player sends a movement packet
     *
     * Cancelling this event will cause the player to stay in place
     */
    public static final Event<PlayerMoveEventCallback> PLAYER_MOVE_EVENT = EventFactory.createArrayBacked(PlayerMoveEventCallback.class, (_1, cancel) -> cancel, callbacks -> (event, _isCancelled) -> {
        boolean isCancelled = _isCancelled;
        for (PlayerMoveEventCallback callback : callbacks) {
            isCancelled = callback.onPlayerMove(event, isCancelled);
        }
        return isCancelled;
    });

    public interface PlayerMoveEventCallback {
        /**
         * Callback for the {@link PlayerMoveEvent}
         * This will initiate the event as non-cancelled
         * @param event the move event
         * @return true to cancel the event, otherwise return false
         */
        default boolean onPlayerMove(PlayerMoveEvent event) {
            return this.onPlayerMove(event, false);
        }

        /**
         * Callback for the {@link PlayerMoveEvent}
         * @param event the move event
         * @param isCancelled whether the event was cancelled before reaching this callback
         * @return true to cancel the event, otherwise return false
         */
        boolean onPlayerMove(PlayerMoveEvent event, boolean isCancelled);
    }

    private Location from;
    private Location to;

    public PlayerMoveEvent(Player player, Location from, Location to) {
        super(player);
        this.from = from;
        this.to = to;
    }

    /**
     * Gets the location this player moved from
     *
     * @return Location the player moved from
     */
    public Location getFrom() {
        return from;
    }

    /**
     * Sets the location to mark as where the player moved from
     *
     * @param from New location to mark as the players previous location
     */
    public void setFrom(Location from) {
        this.from = from;
    }

    /**
     * Gets the location this player moved to
     *
     * @return Location the player moved to
     */
    public Location getTo() {
        return to;
    }

    /**
     * Sets the location that this player will move to
     *
     * @param to New Location this player will move to
     */
    public void setTo(Location to) {
        this.to = to;
    }
}

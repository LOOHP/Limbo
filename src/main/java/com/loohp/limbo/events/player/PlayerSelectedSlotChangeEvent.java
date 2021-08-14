package com.loohp.limbo.events.player;

import com.loohp.limbo.events.api.Event;
import com.loohp.limbo.events.api.EventFactory;
import com.loohp.limbo.player.Player;

public class PlayerSelectedSlotChangeEvent extends PlayerEvent {

    /**
     * Called when the client sends a Held Item Change Packet
     *
     * Cancelling this event will force the player to keep the same slot selected
     */
    public static final Event<PlayerSelectedSlotChangeEventCallback> PLAYER_SELECTED_SLOT_CHANGE_EVENT = EventFactory.createArrayBacked(PlayerSelectedSlotChangeEventCallback.class, (_1, cancel) -> cancel, callbacks -> (event, _isCancelled) -> {
        boolean isCancelled = _isCancelled;
        for (PlayerSelectedSlotChangeEventCallback callback : callbacks) {
            isCancelled = callback.onPlayerSelectedSlotChange(event, isCancelled);
        }
        return isCancelled;
    });

    public interface PlayerSelectedSlotChangeEventCallback {

        /**
         * Callback for the {@link PlayerSelectedSlotChangeEvent}
         * This will initiate the event as non-cancelled
         * @return true to cancel the event, otherwise return false
         */
        default boolean onPlayerSelectedSlotChange(PlayerSelectedSlotChangeEvent event) {
            return this.onPlayerSelectedSlotChange(event, false);
        }

        /**
         * Callback for the {@link PlayerSelectedSlotChangeEvent}
         * @param isCancelled whether the event was cancelled before reaching this callback
         * @return true to cancel the event, otherwise return false
         */
        boolean onPlayerSelectedSlotChange(PlayerSelectedSlotChangeEvent event, boolean isCancelled);
    }

    private byte slot;

    public PlayerSelectedSlotChangeEvent(Player player, byte slot) {
        super(player);
        this.slot = slot;
    }

    public byte getSlot() {
        return slot;
    }

    public void setSlot(byte slot) {
        this.slot = slot;
    }
}

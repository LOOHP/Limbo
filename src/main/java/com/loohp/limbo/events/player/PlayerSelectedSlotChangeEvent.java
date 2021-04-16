package com.loohp.limbo.events.player;

import com.loohp.limbo.events.Cancellable;
import com.loohp.limbo.player.Player;

public class PlayerSelectedSlotChangeEvent extends PlayerEvent implements Cancellable {

    private boolean cancel = false;
    private byte slot;

    public PlayerSelectedSlotChangeEvent(Player player, byte slot) {
        super(player);
        this.slot = slot;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancel = cancelled;
    }

    public byte getSlot() {
        return slot;
    }

    public void setSlot(byte slot) {
        this.slot = slot;
    }
}

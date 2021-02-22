package com.loohp.limbo.Events;

import com.loohp.limbo.Player.Player;

public class PlayerSelectedSlotChangeEvent extends PlayerEvent implements Cancellable {

    private boolean cancel = false;
    private byte slot;

    public PlayerSelectedSlotChangeEvent(Player player, byte slot) {
        super(player);
        this.slot = slot;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancel = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    public byte getSlot() {
        return slot;
    }

    public void setSlot(byte slot) {
        this.slot = slot;
    }
}

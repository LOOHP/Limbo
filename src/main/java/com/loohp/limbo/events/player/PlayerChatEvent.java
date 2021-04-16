package com.loohp.limbo.events.player;

import com.loohp.limbo.events.Cancellable;
import com.loohp.limbo.player.Player;

public class PlayerChatEvent extends PlayerEvent implements Cancellable {

    private String prefix;
    private String message;
    private boolean cancelled;

    public PlayerChatEvent(Player player, String prefix, String message, boolean cancelled) {
        super(player);
        this.prefix = prefix;
        this.message = message;
        this.cancelled = cancelled;
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
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}

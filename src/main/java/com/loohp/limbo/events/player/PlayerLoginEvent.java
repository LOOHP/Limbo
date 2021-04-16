package com.loohp.limbo.events.player;

import com.loohp.limbo.events.Cancellable;
import com.loohp.limbo.events.Event;
import com.loohp.limbo.server.ClientConnection;
import net.md_5.bungee.api.chat.BaseComponent;

public class PlayerLoginEvent extends Event implements Cancellable {

    private final ClientConnection connection;
    private boolean cancelled;
    private BaseComponent[] cancelReason;

    public PlayerLoginEvent(ClientConnection connection, boolean cancelled, BaseComponent... cancelReason) {
        this.connection = connection;
        this.cancelled = cancelled;
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}

package com.loohp.limbo.events;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}

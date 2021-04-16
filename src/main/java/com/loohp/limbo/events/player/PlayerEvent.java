package com.loohp.limbo.events.player;

import com.loohp.limbo.events.Event;
import com.loohp.limbo.player.Player;

public class PlayerEvent extends Event {

    private final Player player;

    public PlayerEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}

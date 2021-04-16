package com.loohp.limbo.events.player;

import com.loohp.limbo.player.Player;

public class PlayerQuitEvent extends PlayerEvent {

    public PlayerQuitEvent(Player player) {
        super(player);
    }

}

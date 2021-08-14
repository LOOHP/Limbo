package com.loohp.limbo.events.player;

import com.loohp.limbo.events.api.Event;
import com.loohp.limbo.events.api.EventFactory;
import com.loohp.limbo.player.Player;

public interface PlayerQuitEvent {

	/**
	 * Called whenever a player leaves the server
	 */
	Event<PlayerQuitEvent> PLAYER_QUIT_EVENT = EventFactory.createArrayBacked(PlayerQuitEvent.class, _player -> {}, callbacks -> player -> {
		for (PlayerQuitEvent callback : callbacks) {
			callback.onPlayerQuit(player);
		}
	});
	
	void onPlayerQuit(Player player);

}

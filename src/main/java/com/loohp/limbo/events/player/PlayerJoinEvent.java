package com.loohp.limbo.events.player;

import com.loohp.limbo.events.api.Event;
import com.loohp.limbo.events.api.EventFactory;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.player.Player;

public interface PlayerJoinEvent {

	/**
	 * Called whenever a player joins the server
	 *
	 * This event can be used to change the spawn location of the player
	 */
	Event<PlayerJoinEvent> PLAYER_JOIN_EVENT = EventFactory.createArrayBacked(PlayerJoinEvent.class, (_1, location) -> location, callbacks -> (player, _spawnLocation) -> {
		Location spawnLocation = _spawnLocation;
		for (PlayerJoinEvent callback : callbacks) {
			spawnLocation = callback.onPlayerJoin(player, spawnLocation);
		}
		return spawnLocation;
	});

	Location onPlayerJoin(Player player, Location spawnLocation);
}

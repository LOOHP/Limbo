/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loohp.limbo.events.player;

import com.loohp.limbo.events.Cancellable;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.player.Player;

/**
 * Holds information for player movement events
 */
public class PlayerMoveEvent extends PlayerEvent implements Cancellable {

    private boolean cancel = false;
    private Location from;
    private Location to;

    public PlayerMoveEvent(Player player, Location from, Location to) {
        super(player);
        this.from = from;
        this.to = to;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     * <p>
     * If a move or teleport event is cancelled, the player will be moved or
     * teleported back to the Location as defined by getFrom(). This will not
     * fire an event
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     * <p>
     * If a move or teleport event is cancelled, the player will be moved or
     * teleported back to the Location as defined by getFrom(). This will not
     * fire an event
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Gets the location this player moved from
     *
     * @return Location the player moved from
     */
    public Location getFrom() {
        return from;
    }

    /**
     * Sets the location to mark as where the player moved from
     *
     * @param from New location to mark as the players previous location
     */
    public void setFrom(Location from) {
        this.from = from;
    }

    /**
     * Gets the location this player moved to
     *
     * @return Location the player moved to
     */
    public Location getTo() {
        return to;
    }

    /**
     * Sets the location that this player will move to
     *
     * @param to New Location this player will move to
     */
    public void setTo(Location to) {
        this.to = to;
    }
}

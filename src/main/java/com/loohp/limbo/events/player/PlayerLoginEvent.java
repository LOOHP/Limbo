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
import com.loohp.limbo.events.Event;
import com.loohp.limbo.network.ClientConnection;
import net.md_5.bungee.api.chat.BaseComponent;

public class PlayerLoginEvent extends Event implements Cancellable {
	
	private ClientConnection connection;
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
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

}

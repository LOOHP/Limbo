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

package com.loohp.limbo.network.protocol.packets;

import com.loohp.limbo.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class ServerboundResourcePackPacket extends PacketIn {
	
	public enum Action {

		SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED, DOWNLOADED, INVALID_URL, FAILED_RELOAD, DISCARDED;

		public boolean isTerminal() {
			return this != ACCEPTED && this != DOWNLOADED;
		}

	}

	private final UUID id;
	private final Action action;
	
	public ServerboundResourcePackPacket(UUID id, Action action) {
		this.id = id;
		this.action = action;
	}
	
	public ServerboundResourcePackPacket(DataInputStream in) throws IOException {
		this(DataTypeIO.readUUID(in), Action.values()[DataTypeIO.readVarInt(in)]);
	}

	public UUID getId() {
		return id;
	}

	public Action getAction() {
		return action;
	}

}

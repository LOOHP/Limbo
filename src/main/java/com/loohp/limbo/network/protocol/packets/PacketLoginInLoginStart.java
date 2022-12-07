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

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import com.loohp.limbo.utils.DataTypeIO;

public class PacketLoginInLoginStart extends PacketIn {
	
	private String username;
	private Optional<UUID> uuid;
	
	public PacketLoginInLoginStart(String username) {
		this.username = username;
	}
	
	public PacketLoginInLoginStart(DataInputStream in) throws IOException {
		this.username = DataTypeIO.readString(in, StandardCharsets.UTF_8);
		if (in.readBoolean()) {
			this.uuid = Optional.of(DataTypeIO.readUUID(in));
		} else {
			this.uuid = Optional.empty();
		}
	}

	public String getUsername() {
		return username;
	}

	public boolean hasUniqueId() {
		return uuid.isPresent();
	}

	public UUID getUniqueId() {
		return uuid.orElse(null);
	}
}

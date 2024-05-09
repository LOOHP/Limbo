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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PacketLoginOutLoginSuccess extends PacketOut {
	
	private final UUID uuid;
	private final String username;
	private final boolean strictErrorHandling;
	
	public PacketLoginOutLoginSuccess(UUID uuid, String username, boolean strictErrorHandling) {
		this.uuid = uuid;
		this.username = username;
		this.strictErrorHandling = strictErrorHandling;
	}
	
	public UUID getUuid() {
		return uuid;
	}

	public String getUsername() {
		return username;
	}

	public boolean isStrictErrorHandling() {
		return strictErrorHandling;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getLoginOut().get(getClass()));
		DataTypeIO.writeUUID(output, uuid);
		DataTypeIO.writeString(output, username, StandardCharsets.UTF_8);
		DataTypeIO.writeVarInt(output, 0);
		output.writeBoolean(strictErrorHandling);
		
		return buffer.toByteArray();
	}

}

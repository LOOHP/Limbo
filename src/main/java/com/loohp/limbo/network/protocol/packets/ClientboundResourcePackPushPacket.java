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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ClientboundResourcePackPushPacket extends PacketOut {
	
	public static final int MAX_HASH_LENGTH = 40;

	private final UUID id;
	private final String url;
	private final String hash;
	private final boolean required;
	private final Component prompt;

	public ClientboundResourcePackPushPacket(UUID id, String url, String hash, boolean required, Component promptMessage) {
		if (hash.length() > MAX_HASH_LENGTH) {
            throw new IllegalArgumentException("Hash is too long (max " + MAX_HASH_LENGTH + ", was " + hash.length() + ")");
        }
		this.id = id;
		this.url = url;
		this.hash = hash;
		this.required = required;
		this.prompt = promptMessage;
	}

	public UUID getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getHash() {
		return hash;
	}

	public boolean isRequired() {
		return required;
	}

	public Component getPrompt() {
		return prompt;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeUUID(output, id);
		DataTypeIO.writeString(output, url, StandardCharsets.UTF_8);
		DataTypeIO.writeString(output, hash, StandardCharsets.UTF_8);
		output.writeBoolean(required);
		if (prompt == null) {
			output.writeBoolean(false);
		} else {
			output.writeBoolean(true);
			DataTypeIO.writeComponent(output, prompt);
		}

		return buffer.toByteArray();
	}

}

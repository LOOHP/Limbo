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

public class PacketPlayOutResourcePackSend extends PacketOut {
	
	public static final int MAX_HASH_LENGTH = 40;
	
	private String url;
	private String hash;
	private boolean isForced;
	private boolean hasPromptMessage;
	private Component promptMessage;

	public PacketPlayOutResourcePackSend(String url, String hash, boolean isForced, boolean hasPromptMessage, Component promptMessage) {
		if (hash.length() > MAX_HASH_LENGTH) {
            throw new IllegalArgumentException("Hash is too long (max " + MAX_HASH_LENGTH + ", was " + hash.length() + ")");
        }
		this.url = url;
		this.hash = hash;
		this.isForced = isForced;
		this.hasPromptMessage = hasPromptMessage;
		if (hasPromptMessage && promptMessage == null) {
			throw new IllegalArgumentException("promptMessage cannot be null when hasPromptMessage is true");
		}
		this.promptMessage = promptMessage;
	}

	public String getURL() {
		return url;
	}

	public String getHash() {
		return hash;
	}

	public boolean isForced() {
		return isForced;
	}

	public boolean hasPromptMessage() {
		return hasPromptMessage;
	}

	public Component getPromptMessage() {
		return promptMessage;
	}
	
	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, url, StandardCharsets.UTF_8);
		DataTypeIO.writeString(output, hash, StandardCharsets.UTF_8);
		output.writeBoolean(isForced);
		output.writeBoolean(hasPromptMessage);
		if (hasPromptMessage) {
			DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(promptMessage), StandardCharsets.UTF_8);
		}
		return buffer.toByteArray();
	}

}

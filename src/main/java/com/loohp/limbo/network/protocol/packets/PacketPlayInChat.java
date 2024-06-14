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
import com.loohp.limbo.utils.LastSeenMessages;
import com.loohp.limbo.utils.MessageSignature;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class PacketPlayInChat extends PacketIn {
	
	private final String message;
	private final Instant time;
	private final long salt;
	private final MessageSignature signature;
	private final LastSeenMessages.b lastSeenMessages;

	public PacketPlayInChat(String message, Instant time, long salt, MessageSignature signature, LastSeenMessages.b lastSeenMessages) {
		this.message = message;
		this.time = time;
		this.salt = salt;
		this.signature = signature;
		this.lastSeenMessages = lastSeenMessages;
	}

	public PacketPlayInChat(DataInputStream in) throws IOException {
		this(DataTypeIO.readString(in, StandardCharsets.UTF_8), Instant.ofEpochMilli(in.readLong()), in.readLong(), in.readBoolean() ? MessageSignature.read(in) : null, new LastSeenMessages.b(in));
	}

	public String getMessage() {
		return message;
	}

	public Instant getTime() {
		return time;
	}

	public MessageSignature getSignature() {
		return signature;
	}

	public long getSalt() {
		return salt;
	}

	public LastSeenMessages.b getLastSeenMessages() {
		return lastSeenMessages;
	}
}

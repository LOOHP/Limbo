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
import java.time.Instant;

import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.NetworkEncryptionUtils;
import com.loohp.limbo.utils.NetworkEncryptionUtils.SignatureData;

public class PacketPlayInChat extends PacketIn {
	
	private String message;
	private Instant time;
	private NetworkEncryptionUtils.SignatureData signature;
	private boolean previewed;

	public PacketPlayInChat(String message, Instant time, SignatureData signature, boolean previewed) {
		this.message = message;
		this.time = time;
		this.signature = signature;
		this.previewed = previewed;
	}

	public PacketPlayInChat(DataInputStream in) throws IOException {
		this(DataTypeIO.readString(in, StandardCharsets.UTF_8), Instant.ofEpochMilli(in.readLong()), new NetworkEncryptionUtils.SignatureData(in), in.readBoolean());
	}

	public String getMessage() {
		return message;
	}

	public Instant getTime() {
		return time;
	}

	public SignatureData getSignature() {
		return signature;
	}

	public boolean isPreviewed() {
		return previewed;
	}

}

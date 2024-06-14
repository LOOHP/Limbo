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
import java.util.Optional;

public class PacketLoginInPluginMessaging extends PacketIn {

	private final int messageId;
	private final boolean successful;
	private final Optional<byte[]> data;

	public PacketLoginInPluginMessaging(int messageId, boolean successful, byte[] data) {
		this.messageId = messageId;
		this.successful = successful;
		this.data = successful ? Optional.of(data) : Optional.empty();
	}
	
	public PacketLoginInPluginMessaging(DataInputStream in, int packetLength, int packetId) throws IOException {
		messageId = DataTypeIO.readVarInt(in);
		successful = in.readBoolean();
		if (successful) {
			int dataLength = packetLength - DataTypeIO.getVarIntLength(packetId) - DataTypeIO.getVarIntLength(messageId) - 1;
			if (dataLength != 0) {
				byte[] data = new byte[dataLength];
				in.readFully(data);
				this.data = Optional.of(data);
			} else {
				this.data = Optional.of(new byte[0]);
			}
		} else {
			data = Optional.empty();
		}
	}
	
	public int getMessageId() {
		return messageId;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public Optional<byte[]> getData() {
		return data;
	}

}

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

import com.loohp.limbo.utils.DataTypeIO;

public class PacketPlayInPluginMessaging extends PacketIn {

	private String channel;
	private byte[] data;

	public PacketPlayInPluginMessaging(String channel, byte[] data) {
		this.channel = channel;
		this.data = data;
	}
	
	public PacketPlayInPluginMessaging(DataInputStream in, int packetLength, int packetId) throws IOException {
		channel = DataTypeIO.readString(in, StandardCharsets.UTF_8);
		int dataLength = packetLength - DataTypeIO.getVarIntLength(packetId) - DataTypeIO.getStringLength(channel, StandardCharsets.UTF_8);
		data = new byte[dataLength];
		in.readFully(data);
	}

	public String getChannel() {
		return channel;
	}

	public byte[] getData() {
		return data;
	}

}

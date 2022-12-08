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
import net.kyori.adventure.key.Key;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketLoginOutPluginMessaging extends PacketOut {

	private int messageId;
	private Key channel;
	private byte[] data;

	public PacketLoginOutPluginMessaging(int messageId, Key channel) {
		this(messageId, channel, null);
	}

	public PacketLoginOutPluginMessaging(int messageId, Key channel, byte[] data) {
		this.messageId = messageId;
		this.channel = channel;
		this.data = data;
	}
	
	public int getMessageId() {
		return messageId;
	}

	public Key getChannel() {
		return channel;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getLoginOut().get(getClass()));
		DataTypeIO.writeVarInt(output, messageId);
		DataTypeIO.writeString(output, channel.toString(), StandardCharsets.UTF_8);
		if (data != null) {
			output.write(data);
		}
		
		return buffer.toByteArray();
	}

}

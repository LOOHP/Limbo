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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.loohp.limbo.utils.DataTypeIO;

import com.loohp.limbo.utils.NetworkEncryptionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class ClientboundPlayerChatPacket extends PacketOut {

	private Component signedContent;
	private Optional<Component> unsignedContent;
	private int position;
	private UUID sender;
	private Instant time;
	private NetworkEncryptionUtils.SignatureData saltSignature;

	public ClientboundPlayerChatPacket(Component signedContent, Optional<Component> unsignedContent, int position, UUID sender, Instant time, NetworkEncryptionUtils.SignatureData saltSignature) {
		this.signedContent = signedContent;
		this.unsignedContent = unsignedContent;
		this.position = position;
		this.sender = sender;
		this.time = time;
		this.saltSignature = saltSignature;
	}

	public Component getSignedContent() {
		return signedContent;
	}

	public Optional<Component> getUnsignedContent() {
		return unsignedContent;
	}

	public int getPosition() {
		return position;
	}

	public UUID getSender() {
		return sender;
	}

	public Instant getTime() {
		return time;
	}

	public NetworkEncryptionUtils.SignatureData getSaltSignature() {
		return saltSignature;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(signedContent), StandardCharsets.UTF_8);
		if (unsignedContent.isPresent()) {
			output.writeBoolean(true);
			DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(unsignedContent.get()), StandardCharsets.UTF_8);
		} else {
			output.writeBoolean(false);
		}
		DataTypeIO.writeVarInt(output, position);
		DataTypeIO.writeUUID(output, sender);
		output.writeLong(time.toEpochMilli());
		NetworkEncryptionUtils.SignatureData.write(output, saltSignature);
		
		return buffer.toByteArray();
	}

}

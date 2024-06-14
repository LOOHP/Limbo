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

import com.loohp.limbo.registry.PacketRegistry;
import com.loohp.limbo.utils.DataTypeIO;
import net.kyori.adventure.text.Component;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientboundSystemChatPacket extends PacketOut {

	private final Component message;
	private final boolean overlay;

	public ClientboundSystemChatPacket(Component message, boolean overlay) {
		this.message = message;
		this.overlay = overlay;
	}

	public Component getMessage() {
		return message;
	}

	public boolean isOverlay() {
		return overlay;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(PacketRegistry.getPacketId(getClass()));
		DataTypeIO.writeComponent(output, message);
		output.writeBoolean(overlay);
		
		return buffer.toByteArray();
	}

}

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

public class PacketPlayOutPlayerAbilities extends PacketOut {
	
	public enum PlayerAbilityFlags {
		INVULNERABLE(0x01),
		FLY(0x02),
		ALLOW_FLYING(0x04),
		CREATIVE(0x08);
		
		private final int bitvalue;
		
		PlayerAbilityFlags(int bitvalue) {
			this.bitvalue = bitvalue;
		}
		
		public int getValue() {
			return bitvalue;
		}
	}

	private PlayerAbilityFlags[] flags;
	private float flySpeed;
	private float fieldOfField;

	public PacketPlayOutPlayerAbilities(float flySpeed, float fieldOfField, PlayerAbilityFlags... flags) {
		this.flags = flags;
		this.flySpeed = flySpeed;
		this.fieldOfField = fieldOfField;
	}

	public PlayerAbilityFlags[] getFlags() {
		return flags;
	}

	public float getFlySpeed() {
		return flySpeed;
	}

	public float getFieldOfField() {
		return fieldOfField;
	}
	
	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		int value = 0;
		for (PlayerAbilityFlags flag : flags) {
			value = value | flag.getValue();
		}
		
		output.writeByte(value);
		output.writeFloat(flySpeed);
		output.writeFloat(fieldOfField);
		
		return buffer.toByteArray();
	}

}

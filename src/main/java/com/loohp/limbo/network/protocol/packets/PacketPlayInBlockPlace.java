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

import com.loohp.limbo.inventory.EquipmentSlot;
import com.loohp.limbo.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInBlockPlace extends PacketIn {

	private EquipmentSlot hand;
	private int sequence;

	public PacketPlayInBlockPlace(EquipmentSlot hand, int sequence) {
		this.hand = hand;
		this.sequence = sequence;
	}

	public PacketPlayInBlockPlace(DataInputStream in) throws IOException {
		this(EquipmentSlot.values()[DataTypeIO.readVarInt(in)], DataTypeIO.readVarInt(in));
	}

	public EquipmentSlot getHand() {
		return hand;
	}

	public int getSequence() {
		return sequence;
	}
}

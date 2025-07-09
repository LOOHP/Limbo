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

import com.loohp.limbo.inventory.ItemStack;
import com.loohp.limbo.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInSetCreativeSlot extends PacketIn {

	private final int slotNumber;
	private final ItemStack itemStack;

	public PacketPlayInSetCreativeSlot(int slotNumber, ItemStack itemStack) {
		this.slotNumber = slotNumber;
		this.itemStack = itemStack;
	}

	public PacketPlayInSetCreativeSlot(DataInputStream in) throws IOException {
		this(in.readShort(), DataTypeIO.readUntrustedItemStack(in));
	}

	public int getSlotNumber() {
		return slotNumber;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}
}

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

import com.loohp.limbo.inventory.InventoryClickType;
import com.loohp.limbo.inventory.ItemStack;
import com.loohp.limbo.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PacketPlayInWindowClick extends PacketIn {

	private int containerId;
	private int stateId;
	private int slotNum;
	private int buttonNum;
	private InventoryClickType clickType;
	private Map<Integer, ItemStack> changedSlots;
	private ItemStack carriedItem;

	public PacketPlayInWindowClick(int containerId, int stateId, int slotNum, int buttonNum, InventoryClickType clickType, Map<Integer, ItemStack> changedSlots, ItemStack carriedItem) {
		this.containerId = containerId;
		this.stateId = stateId;
		this.slotNum = slotNum;
		this.buttonNum = buttonNum;
		this.clickType = clickType;
		this.changedSlots = changedSlots;
		this.carriedItem = carriedItem;
	}

	public PacketPlayInWindowClick(DataInputStream in) throws IOException {
		this.containerId = in.readByte();
		this.stateId = DataTypeIO.readVarInt(in);
		this.slotNum = in.readShort();
		this.buttonNum = in.readByte();
		this.clickType = InventoryClickType.values()[DataTypeIO.readVarInt(in)];
		Map<Integer, ItemStack> changedSlots = new HashMap<>();
		int size = DataTypeIO.readVarInt(in);
		for (int i = 0; i < size; i++) {
			int slot = in.readShort();
			ItemStack itemStack = DataTypeIO.readItemStack(in);
			changedSlots.put(slot, itemStack);
		}
		this.changedSlots = Collections.unmodifiableMap(changedSlots);
		this.carriedItem = DataTypeIO.readItemStack(in);
	}

	public int getContainerId() {
		return containerId;
	}

	public int getStateId() {
		return stateId;
	}

	public int getSlotNum() {
		return slotNum;
	}

	public int getButtonNum() {
		return buttonNum;
	}

	public InventoryClickType getClickType() {
		return clickType;
	}

	public Map<Integer, ItemStack> getChangedSlots() {
		return changedSlots;
	}

	public ItemStack getCarriedItem() {
		return carriedItem;
	}
}

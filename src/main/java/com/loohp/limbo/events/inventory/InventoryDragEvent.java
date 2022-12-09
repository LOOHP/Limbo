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

package com.loohp.limbo.events.inventory;

import com.loohp.limbo.events.Cancellable;
import com.loohp.limbo.inventory.DragType;
import com.loohp.limbo.inventory.InventoryView;
import com.loohp.limbo.inventory.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InventoryDragEvent extends InventoryEvent implements Cancellable {

	private boolean cancelled;
	private final DragType type;
	private final Map<Integer, ItemStack> addedItems;
	private final Set<Integer> containerSlots;
	private final ItemStack oldCarried;
	private ItemStack newCarried;

	public InventoryDragEvent(InventoryView view, ItemStack newCarried, ItemStack oldCarried, boolean right, Map<Integer, ItemStack> slots) {
		super(view, view.getInventory(view.convertSlot(slots.keySet().iterator().next())));
		this.type = right ? DragType.SINGLE : DragType.EVEN;
		this.newCarried = newCarried;
		this.oldCarried = oldCarried;
		this.addedItems = Collections.unmodifiableMap(slots);
		Set<Integer> containerSlots = new HashSet<>();
		for (Integer slot : slots.keySet()) {
			containerSlots.add(view.convertSlot(slot));
		}
		this.containerSlots = Collections.unmodifiableSet(containerSlots);
		this.cancelled = false;
	}

	public Map<Integer, ItemStack> getNewItems() {
		return addedItems;
	}

	public Set<Integer> getRawSlots() {
		return addedItems.keySet();
	}

	public Set<Integer> getInventorySlots() {
		return containerSlots;
	}

	public ItemStack get() {
		return newCarried;
	}

	public ItemStack getCarriedItem() {
		return newCarried;
	}

	public void setCarriedItem(ItemStack newCursor) {
		this.newCarried = newCursor;
	}

	public ItemStack getOldCarriedItem() {
		return oldCarried.clone();
	}

	public DragType getType() {
		return type;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}

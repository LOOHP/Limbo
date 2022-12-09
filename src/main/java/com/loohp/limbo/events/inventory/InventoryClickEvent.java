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
import com.loohp.limbo.inventory.ClickType;
import com.loohp.limbo.inventory.InventoryAction;
import com.loohp.limbo.inventory.InventoryType;
import com.loohp.limbo.inventory.InventoryView;
import com.loohp.limbo.inventory.ItemStack;

public class InventoryClickEvent extends InventoryEvent implements Cancellable {

	private boolean cancelled;
	private final ClickType click;
	private final InventoryAction action;
	private InventoryType.SlotType type;
	private int whichSlot;
	private int rawSlot;
	private ItemStack current;
	private int hotbarKey;

	public InventoryClickEvent(InventoryView view, InventoryType.SlotType type, int rawSlot, ClickType click, InventoryAction action) {
		super(view, view.getInventory(rawSlot));
		this.type = type;
		this.rawSlot = rawSlot;
		this.whichSlot = view.convertSlot(rawSlot);
		this.click = click;
		this.action = action;
		this.current = null;
		this.hotbarKey = -1;
		this.cancelled = false;
	}

	public InventoryClickEvent(InventoryView view, InventoryType.SlotType type, int rawSlot, ClickType click, InventoryAction action, int hotbarKey) {
		this(view, type, rawSlot, click, action);
		this.hotbarKey = hotbarKey;
	}

	public ClickType getClick() {
		return click;
	}

	public InventoryAction getAction() {
		return action;
	}

	public InventoryType.SlotType getType() {
		return type;
	}

	public int getWhichSlot() {
		return whichSlot;
	}

	public int getRawSlot() {
		return rawSlot;
	}

	public int getHotbarKey() {
		return hotbarKey;
	}

	public ItemStack getCarriedItem() {
		return getView().getCarriedItem();
	}

	@Deprecated
	public void setCarriedItem(ItemStack stack) {
		getView().setCarriedItem(stack);
	}

	public ItemStack getCurrentItem() {
		if (type == InventoryType.SlotType.OUTSIDE) {
			return current;
		}
		return getView().getItem(rawSlot);
	}

	public void setCurrentItem(ItemStack stack) {
		if (type == InventoryType.SlotType.OUTSIDE) {
			current = stack;
		} else {
			getView().setItem(rawSlot, stack);
		}
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

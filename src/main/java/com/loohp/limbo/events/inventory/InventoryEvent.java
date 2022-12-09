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

import com.loohp.limbo.events.Event;
import com.loohp.limbo.inventory.Inventory;
import com.loohp.limbo.inventory.InventoryView;
import com.loohp.limbo.player.Player;

public class InventoryEvent extends Event {

	private final InventoryView inventoryView;
	private final Inventory clickedInventory;
	
	public InventoryEvent(InventoryView inventoryView, Inventory clickedInventory) {
		this.inventoryView = inventoryView;
		this.clickedInventory = clickedInventory;
	}
	
	public Player getPlayer() {
		return inventoryView.getPlayer();
	}

	public InventoryView getView() {
		return inventoryView;
	}

	public Inventory getClickedInventory() {
		return clickedInventory;
	}
}

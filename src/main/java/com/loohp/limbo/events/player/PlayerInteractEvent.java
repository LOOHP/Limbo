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

package com.loohp.limbo.events.player;

import com.loohp.limbo.events.Cancellable;
import com.loohp.limbo.inventory.EquipmentSlot;
import com.loohp.limbo.inventory.ItemStack;
import com.loohp.limbo.location.BlockFace;
import com.loohp.limbo.player.Player;
import com.loohp.limbo.world.BlockState;

public class PlayerInteractEvent extends PlayerEvent implements Cancellable {

	public enum Action {

		LEFT_CLICK_AIR,
		LEFT_CLICK_BLOCK,
		PHYSICAL,
		RIGHT_CLICK_AIR,
		RIGHT_CLICK_BLOCK;

	}

	private boolean cancelled = false;
	private final Action action;
	private final ItemStack item;
	private final BlockState clickedBlock;
	private final BlockFace clickedFace;
	private final EquipmentSlot hand;

	public PlayerInteractEvent(Player player, Action action, ItemStack item, BlockState clickedBlock, BlockFace clickedFace, EquipmentSlot hand) {
		super(player);
		this.action = action;
		this.item = item;
		this.clickedBlock = clickedBlock;
		this.clickedFace = clickedFace;
		this.hand = hand;
	}

	public Action getAction() {
		return action;
	}

	public ItemStack getItem() {
		return item;
	}

	public BlockState getClickedBlock() {
		return clickedBlock;
	}

	public BlockFace getClickedFace() {
		return clickedFace;
	}

	public EquipmentSlot getHand() {
		return hand;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

}

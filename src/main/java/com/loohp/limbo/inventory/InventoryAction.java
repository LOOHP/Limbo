/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2026. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2026. Contributors
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

package com.loohp.limbo.inventory;

/**
 * An estimation of what the result will be.
 */
public enum InventoryAction {

    /**
     * Nothing will happen from the click.
     * <p>
     * There may be cases where nothing will happen and this is value is not
     * provided, but it is guaranteed that this value is accurate when given.
     */
    NOTHING,
    /**
     * All of the items on the clicked slot are moved to the cursor.
     */
    PICKUP_ALL,
    /**
     * Some of the items on the clicked slot are moved to the cursor.
     */
    PICKUP_SOME,
    /**
     * Half of the items on the clicked slot are moved to the cursor.
     */
    PICKUP_HALF,
    /**
     * One of the items on the clicked slot are moved to the cursor.
     */
    PICKUP_ONE,
    /**
     * All of the items on the cursor are moved to the clicked slot.
     */
    PLACE_ALL,
    /**
     * Some of the items from the cursor are moved to the clicked slot
     * (usually up to the max stack size).
     */
    PLACE_SOME,
    /**
     * A single item from the cursor is moved to the clicked slot.
     */
    PLACE_ONE,
    /**
     * The clicked item and the cursor are exchanged.
     */
    SWAP_WITH_CURSOR,
    /**
     * The entire cursor item is dropped.
     */
    DROP_ALL_CURSOR,
    /**
     * One item is dropped from the cursor.
     */
    DROP_ONE_CURSOR,
    /**
     * The entire clicked slot is dropped.
     */
    DROP_ALL_SLOT,
    /**
     * One item is dropped from the clicked slot.
     */
    DROP_ONE_SLOT,
    /**
     * The item is moved to the opposite inventory if a space is found.
     */
    MOVE_TO_OTHER_INVENTORY,
    /**
     * The clicked item is moved to the hotbar, and the item currently there
     * is re-added to the player's inventory.
     *
     * The hotbar includes the player's off hand.
     */
    HOTBAR_MOVE_AND_READD,
    /**
     * The clicked slot and the picked hotbar slot are swapped.
     *
     * The hotbar includes the player's off hand.
     */
    HOTBAR_SWAP,
    /**
     * A max-size stack of the clicked item is put on the cursor.
     */
    CLONE_STACK,
    /**
     * The inventory is searched for the same material, and they are put on
     * the cursor up to {@link ItemStack#getMaxStackSize()}.
     */
    COLLECT_TO_CURSOR,
    /**
     * An unrecognized ClickType.
     */
    UNKNOWN,
    ;
}

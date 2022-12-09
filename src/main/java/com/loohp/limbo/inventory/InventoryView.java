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

package com.loohp.limbo.inventory;

import com.loohp.limbo.network.protocol.packets.PacketPlayOutSetSlot;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutWindowData;
import com.loohp.limbo.player.Player;
import com.loohp.limbo.player.PlayerInventory;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryView {

    public static final int OUTSIDE = -999;

    private final Player player;
    private Component title;
    private Inventory topInventory;
    private final Inventory bottomInventory;
    private final Map<Property, Integer> properties;
    private ItemStack carriedItem;

    private final Unsafe unsafe;

    public InventoryView(Player player, Component title, Inventory topInventory, Inventory bottomInventory) {
        this.player = player;
        this.title = title;
        this.topInventory = topInventory;
        this.bottomInventory = bottomInventory;
        this.properties = new ConcurrentHashMap<>();
        this.carriedItem = null;

        this.unsafe = new Unsafe(this);
    }

    public ItemStack getCarriedItem() {
        return carriedItem;
    }

    public void setCarriedItem(ItemStack carriedItem) {
        this.carriedItem = carriedItem;
    }

    public InventoryType getType() {
        return topInventory == null ? bottomInventory.getType() : topInventory.getType();
    }

    public Player getPlayer() {
        return player;
    }

    public Component getTitle() {
        return title;
    }

    public Inventory getTopInventory() {
        return topInventory;
    }

    public Inventory getBottomInventory() {
        return bottomInventory;
    }

    public Map<Property, Integer> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     * Gets the inventory corresponding to the given raw slot ID.
     *
     * If the slot ID is {@link #OUTSIDE} null will be returned, otherwise
     * behaviour for illegal and negative slot IDs is undefined.
     *
     * May be used with {@link #convertSlot(int)} to directly index an
     * underlying inventory.
     *
     * @param rawSlot The raw slot ID.
     * @return corresponding inventory, or null
     */
    public Inventory getInventory(int rawSlot) {
        // Slot may be -1 if not properly detected due to client bug
        // e.g. dropping an item into part of the enchantment list section of an enchanting table
        if (rawSlot == OUTSIDE || rawSlot == -1) {
            return null;
        }
        if (rawSlot < 0) {
            throw new IllegalArgumentException("Negative, non outside slot " + rawSlot);
        }
        if (rawSlot >= countSlots()) {
            throw new IllegalArgumentException("Slot " + rawSlot + " greater than inventory slot count");
        }

        if (rawSlot < topInventory.getSize()) {
            return getTopInventory();
        } else {
            return getBottomInventory();
        }
    }

    /**
     * Converts a raw slot ID into its local slot ID into whichever of the two
     * inventories the slot points to.
     * <p>
     * If the raw slot refers to the upper inventory, it will be returned
     * unchanged and thus be suitable for getTopInventory().getItem(); if it
     * refers to the lower inventory, the output will differ from the input
     * and be suitable for getBottomInventory().getItem().
     *
     * @param rawSlot The raw slot ID.
     * @return The converted slot ID.
     */
    public int convertSlot(int rawSlot) {
        int numInTop = topInventory == null ? 0 : topInventory.getSize();
        // Index from the top inventory as having slots from [0,size]
        if (rawSlot < numInTop) {
            return rawSlot;
        }

        // Move down the slot index by the top size
        int slot = rawSlot - numInTop;

        // Player crafting slots are indexed differently. The matrix is caught by the first return.
        // Creative mode is the same, except that you can't see the crafting slots (but the IDs are still used)
        if (getType() == InventoryType.CRAFTING || getType() == InventoryType.CREATIVE) {
            /*
             * Raw Slots:
             *
             * 5             1  2     0
             * 6             3  4
             * 7
             * 8           45
             * 9  10 11 12 13 14 15 16 17
             * 18 19 20 21 22 23 24 25 26
             * 27 28 29 30 31 32 33 34 35
             * 36 37 38 39 40 41 42 43 44
             */

            /*
             * Converted Slots:
             *
             * 39             1  2     0
             * 38             3  4
             * 37
             * 36          40
             * 9  10 11 12 13 14 15 16 17
             * 18 19 20 21 22 23 24 25 26
             * 27 28 29 30 31 32 33 34 35
             * 0  1  2  3  4  5  6  7  8
             */

            if (slot < 4) {
                // Send [5,8] to [39,36]
                return 39 - slot;
            } else if (slot > 39) {
                // Slot lives in the extra slot section
                return slot;
            } else {
                // Reset index so 9 -> 0
                slot -= 4;
            }
        }

        // 27 = 36 - 9
        if (slot >= 27) {
            // Put into hotbar section
            slot -= 27;
        } else {
            // Take out of hotbar section
            // 9 = 36 - 27
            slot += 9;
        }

        return slot;
    }

    /**
     * Determine the type of the slot by its raw slot ID.
     * <p>
     * If the type of the slot is unknown, then
     * {@link InventoryType.SlotType#CONTAINER} will be returned.
     *
     * @param slot The raw slot ID
     * @return the slot type
     */
    public InventoryType.SlotType getSlotType(int slot) {
        InventoryType.SlotType type = InventoryType.SlotType.CONTAINER;
        if (topInventory != null && slot >= 0 && slot < topInventory.getSize()) {
            switch (this.getType()) {
                case BLAST_FURNACE:
                case FURNACE:
                case SMOKER:
                    if (slot == 2) {
                        type = InventoryType.SlotType.RESULT;
                    } else if (slot == 1) {
                        type = InventoryType.SlotType.FUEL;
                    } else {
                        type = InventoryType.SlotType.CRAFTING;
                    }
                    break;
                case BREWING:
                    if (slot == 3) {
                        type = InventoryType.SlotType.FUEL;
                    } else {
                        type = InventoryType.SlotType.CRAFTING;
                    }
                    break;
                case ENCHANTING:
                case BEACON:
                    type = InventoryType.SlotType.CRAFTING;
                    break;
                case WORKBENCH:
                case CRAFTING:
                    if (slot == 0) {
                        type = InventoryType.SlotType.RESULT;
                    } else {
                        type = InventoryType.SlotType.CRAFTING;
                    }
                    break;
                case ANVIL:
                case SMITHING:
                case CARTOGRAPHY:
                case GRINDSTONE:
                case MERCHANT:
                    if (slot == 2) {
                        type = InventoryType.SlotType.RESULT;
                    } else {
                        type = InventoryType.SlotType.CRAFTING;
                    }
                    break;
                case STONECUTTER:
                    if (slot == 1) {
                        type = InventoryType.SlotType.RESULT;
                    } else {
                        type = InventoryType.SlotType.CRAFTING;
                    }
                    break;
                case LOOM:
                    if (slot == 3) {
                        type = InventoryType.SlotType.RESULT;
                    } else {
                        type = InventoryType.SlotType.CRAFTING;
                    }
                    break;
                default:
                    // Nothing to do, it's a CONTAINER slot
            }
        } else {
            if (slot < 0) {
                type = InventoryType.SlotType.OUTSIDE;
            } else if (this.getType() == InventoryType.CRAFTING) { // Also includes creative inventory
                if (slot < 9) {
                    type = InventoryType.SlotType.ARMOR;
                } else if (slot > 35) {
                    type = InventoryType.SlotType.QUICKBAR;
                }
            } else if (slot >= (this.countSlots() - (9 + 4 + 1))) { // Quickbar, Armor, Offhand
                type = InventoryType.SlotType.QUICKBAR;
            }
        }
        return type;
    }

    /**
     * Check the total number of slots in this view, combining the upper and
     * lower inventories.
     * <p>
     * Note though that it's possible for this to be greater than the sum of
     * the two inventories if for example some slots are not being used.
     *
     * @return The total size
     */
    public int countSlots() {
        return (topInventory == null ? 0 : topInventory.getSize()) + bottomInventory.getSize();
    }

    public void close() {
        player.closeInventory();
    }

    public boolean isSlot(int index) {
        if (topInventory != null) {
            if (index < topInventory.getSize()) {
                return true;
            }
            index -= topInventory.getSize();
        }
        if (bottomInventory instanceof PlayerInventory) {
            return index < 36;
        }
        return index < bottomInventory.getSize();
    }

    public ItemStack getItem(int index) {
        return getInventory(index).getItem(convertSlot(index));
    }

    public void setItem(int index, ItemStack itemStack) {
        getInventory(index).setItem(convertSlot(index), itemStack);
    }

    public void setProperty(InventoryView.Property prop, int value) {
        if (topInventory != null && prop.getType().equals(topInventory.getType())) {
            Integer id = topInventory.getUnsafe().c().get(player);
            if (id != null) {
                properties.put(prop, value);
                PacketPlayOutWindowData packet = new PacketPlayOutWindowData(id, prop.getId(), value);
                try {
                    player.clientConnection.sendPacket(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateView() {
        if (topInventory != null) {
            topInventory.updateInventory(player);
        }
        bottomInventory.updateInventory(player);
        try {
            player.clientConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, 0, carriedItem));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public Unsafe getUnsafe() {
        return unsafe;
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public static class Unsafe {

        private final InventoryView inventoryView;

        @Deprecated
        public Unsafe(InventoryView inventoryView) {
            this.inventoryView = inventoryView;
        }

        @Deprecated
        public void a(Inventory topInventory, Component title) {
            inventoryView.topInventory = topInventory;
            inventoryView.title = title;
            inventoryView.properties.clear();
        }

        @Deprecated
        public int a() {
            if (inventoryView.topInventory != null) {
                return inventoryView.topInventory.getUnsafe().c().getOrDefault(inventoryView.player, -1);
            }
            return inventoryView.bottomInventory.getUnsafe().c().getOrDefault(inventoryView.player, -1);
        }

    }

    /**
     * Represents various extra properties of certain inventory windows.
     */
    public enum Property {
        /**
         * The progress of the down-pointing arrow in a brewing inventory.
         */
        BREW_TIME(0, InventoryType.BREWING),
        /**
         * The progress of the fuel slot in a brewing inventory.
         * <p>
         * This is a value between 0 and 20, with 0 making the bar empty, and 20
         * making the bar full.
         */
        FUEL_TIME(1, InventoryType.BREWING),
        /**
         * The progress of the flame in a furnace inventory.
         */
        BURN_TIME(0, InventoryType.FURNACE),
        /**
         * How many total ticks the current fuel should last.
         */
        TICKS_FOR_CURRENT_FUEL(1, InventoryType.FURNACE),
        /**
         * The progress of the right-pointing arrow in a furnace inventory.
         */
        COOK_TIME(2, InventoryType.FURNACE),
        /**
         * How many total ticks the current smelting should last.
         */
        TICKS_FOR_CURRENT_SMELTING(3, InventoryType.FURNACE),
        /**
         * In an enchanting inventory, the top button's experience level
         * value.
         */
        ENCHANT_BUTTON1(0, InventoryType.ENCHANTING),
        /**
         * In an enchanting inventory, the middle button's experience level
         * value.
         */
        ENCHANT_BUTTON2(1, InventoryType.ENCHANTING),
        /**
         * In an enchanting inventory, the bottom button's experience level
         * value.
         */
        ENCHANT_BUTTON3(2, InventoryType.ENCHANTING),
        /**
         * In an enchanting inventory, the first four bits of the player's xpSeed.
         */
        ENCHANT_XP_SEED(3, InventoryType.ENCHANTING),
        /**
         * In an enchanting inventory, the top button's enchantment's id
         */
        ENCHANT_ID1(4, InventoryType.ENCHANTING),
        /**
         * In an enchanting inventory, the middle button's enchantment's id
         */
        ENCHANT_ID2(5, InventoryType.ENCHANTING),
        /**
         * In an enchanting inventory, the bottom button's enchantment's id
         */
        ENCHANT_ID3(6, InventoryType.ENCHANTING),
        /**
         * In an enchanting inventory, the top button's level value.
         */
        ENCHANT_LEVEL1(7, InventoryType.ENCHANTING),
        /**
         * In an enchanting inventory, the middle button's level value.
         */
        ENCHANT_LEVEL2(8, InventoryType.ENCHANTING),
        /**
         * In an enchanting inventory, the bottom button's level value.
         */
        ENCHANT_LEVEL3(9, InventoryType.ENCHANTING),
        /**
         * In an beacon inventory, the levels of the beacon
         */
        LEVELS(0, InventoryType.BEACON),
        /**
         * In an beacon inventory, the primary potion effect
         */
        PRIMARY_EFFECT(1, InventoryType.BEACON),
        /**
         * In an beacon inventory, the secondary potion effect
         */
        SECONDARY_EFFECT(2, InventoryType.BEACON),
        /**
         * The repair's cost in xp levels
         */
        REPAIR_COST(0, InventoryType.ANVIL),
        /**
         * The lectern's current open book page
         */
        BOOK_PAGE(0, InventoryType.LECTERN);

        private final int id;
        private final InventoryType style;

        Property(int id, InventoryType appliesTo) {
            this.id = id;
            style = appliesTo;
        }

        public InventoryType getType() {
            return style;
        }

        /**
         * Gets the id of this view.
         *
         * @return the id of this view
         * @deprecated Magic value
         */
        @Deprecated
        public int getId() {
            return id;
        }
    }

}

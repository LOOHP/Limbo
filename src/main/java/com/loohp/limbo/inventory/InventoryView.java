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

import com.loohp.limbo.network.protocol.packets.PacketPlayOutWindowData;
import com.loohp.limbo.player.Player;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryView {

    private final Player player;
    private final String title;
    private Inventory topInventory;
    private final Inventory bottomInventory;
    private final Map<Property, Integer> properties;
    private ItemStack carriedItem;

    private final Unsafe unsafe;

    public InventoryView(Player player, String title, Inventory topInventory, Inventory bottomInventory) {
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
        return topInventory.getType();
    }

    public Player getPlayer() {
        return player;
    }

    public String getTitle() {
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

    public int countSlots() {
        return topInventory.getSize() + bottomInventory.getSize();
    }

    public ItemStack getItem(int index) {
        if (topInventory != null) {
            if (index < topInventory.getSize()) {
                return topInventory.getItem(topInventory.getUnsafe().b().applyAsInt(index));
            }
            index -= topInventory.getSize();
        }
        return bottomInventory.getItem(bottomInventory.getUnsafe().b().applyAsInt(index));
    }

    public void setItem(int index, ItemStack itemStack) {
        if (topInventory != null) {
            if (index < topInventory.getSize()) {
                topInventory.setItem(topInventory.getUnsafe().b().applyAsInt(index), itemStack);
                return;
            }
            index -= topInventory.getSize();
        }
        bottomInventory.setItem(bottomInventory.getUnsafe().b().applyAsInt(index), itemStack);
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
        public void a(Inventory topInventory) {
            inventoryView.topInventory = topInventory;
            inventoryView.properties.clear();
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

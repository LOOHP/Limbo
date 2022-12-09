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

package com.loohp.limbo.utils;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.events.inventory.InventoryClickEvent;
import com.loohp.limbo.events.inventory.InventoryDragEvent;
import com.loohp.limbo.inventory.ClickType;
import com.loohp.limbo.inventory.Inventory;
import com.loohp.limbo.inventory.InventoryAction;
import com.loohp.limbo.inventory.InventoryClickType;
import com.loohp.limbo.inventory.InventoryType;
import com.loohp.limbo.inventory.InventoryView;
import com.loohp.limbo.inventory.ItemStack;
import com.loohp.limbo.network.protocol.packets.PacketPlayInWindowClick;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutSetSlot;
import com.loohp.limbo.player.Player;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryClickUtils {

    private static final Map<Player, QuickCraftInfo> QUICK_CRAFT_INFO = Collections.synchronizedMap(new WeakHashMap<>());

    public static synchronized void handle(Player player, PacketPlayInWindowClick packetplayinwindowclick) {
        InventoryClickEvent event;

        InventoryView inventory = player.getInventoryView();
        InventoryType.SlotType type = inventory.getSlotType(packetplayinwindowclick.getSlotNum());
        int rawSlot = packetplayinwindowclick.getSlotNum();

        boolean cancelled = player.getGamemode().equals(GameMode.SPECTATOR);
        ClickType click = ClickType.UNKNOWN;
        InventoryAction action = InventoryAction.UNKNOWN;

        ItemStack itemstack = null;

        switch (packetplayinwindowclick.getClickType()) {
            case PICKUP:
                if (packetplayinwindowclick.getButtonNum() == 0) {
                    click = ClickType.LEFT;
                } else if (packetplayinwindowclick.getButtonNum() == 1) {
                    click = ClickType.RIGHT;
                }
                if (packetplayinwindowclick.getButtonNum() == 0 || packetplayinwindowclick.getButtonNum() == 1) {
                    action = InventoryAction.NOTHING; // Don't want to repeat ourselves
                    if (packetplayinwindowclick.getSlotNum() == -999) {
                        if (inventory.getCarriedItem() != null) {
                            action = packetplayinwindowclick.getButtonNum() == 0 ? InventoryAction.DROP_ALL_CURSOR : InventoryAction.DROP_ONE_CURSOR;
                        }
                    } else if (packetplayinwindowclick.getSlotNum() < 0)  {
                        action = InventoryAction.NOTHING;
                    } else {
                        ItemStack clickedItem = inventory.getItem(rawSlot);
                        if (inventory.isSlot(rawSlot)) {
                            ItemStack cursor = inventory.getCarriedItem();
                            if (clickedItem == null) {
                                if (cursor != null) {
                                    action = packetplayinwindowclick.getButtonNum() == 0 ? InventoryAction.PLACE_ALL : InventoryAction.PLACE_ONE;
                                }
                            } else {
                                if (cursor == null) {
                                    action = packetplayinwindowclick.getButtonNum() == 0 ? InventoryAction.PICKUP_ALL : InventoryAction.PICKUP_HALF;
                                } else {
                                    if (clickedItem.isSimilar(cursor)) {
                                        int toPlace = packetplayinwindowclick.getButtonNum() == 0 ? cursor.amount() : 1;
                                        toPlace = Math.min(toPlace, clickedItem.getMaxStackSize() - clickedItem.amount());
                                        toPlace = Math.min(toPlace, cursor.getMaxStackSize() - clickedItem.amount());
                                        if (toPlace == 1) {
                                            action = InventoryAction.PLACE_ONE;
                                        } else if (toPlace == cursor.amount()) {
                                            action = InventoryAction.PLACE_ALL;
                                        } else if (toPlace < 0) {
                                            action = toPlace != -1 ? InventoryAction.PICKUP_SOME : InventoryAction.PICKUP_ONE; // this happens with oversized stacks
                                        } else if (toPlace != 0) {
                                            action = InventoryAction.PLACE_SOME;
                                        }
                                    } else if (cursor.amount() <= cursor.getMaxStackSize()) {
                                        action = InventoryAction.SWAP_WITH_CURSOR;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case QUICK_MOVE:
                if (packetplayinwindowclick.getButtonNum() == 0) {
                    click = ClickType.SHIFT_LEFT;
                } else if (packetplayinwindowclick.getButtonNum() == 1) {
                    click = ClickType.SHIFT_RIGHT;
                }
                if (packetplayinwindowclick.getButtonNum() == 0 || packetplayinwindowclick.getButtonNum() == 1) {
                    if (packetplayinwindowclick.getSlotNum() < 0) {
                        action = InventoryAction.NOTHING;
                    } else {
                        ItemStack slot = inventory.getItem(rawSlot);
                        if (inventory.isSlot(rawSlot) && slot != null) {
                            action = InventoryAction.MOVE_TO_OTHER_INVENTORY;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    }
                }
                break;
            case SWAP:
                if ((packetplayinwindowclick.getButtonNum() >= 0 && packetplayinwindowclick.getButtonNum() < 9) || packetplayinwindowclick.getButtonNum() == 40) {
                    click = (packetplayinwindowclick.getButtonNum() == 40) ? ClickType.SWAP_OFFHAND : ClickType.NUMBER_KEY;
                    ItemStack clickedSlot = inventory.getItem(rawSlot);
                    ItemStack hotbar = inventory.getPlayer().getInventory().getItem(packetplayinwindowclick.getButtonNum());
                    boolean canCleanSwap = hotbar == null || inventory.getInventory(rawSlot).equals(inventory.getPlayer().getInventory()); // the slot will accept the hotbar item
                    if (clickedSlot != null) {
                        if (canCleanSwap) {
                            action = InventoryAction.HOTBAR_SWAP;
                        } else {
                            action = InventoryAction.HOTBAR_MOVE_AND_READD;
                        }
                    } else if (clickedSlot == null && hotbar != null) {
                        action = InventoryAction.HOTBAR_SWAP;
                    } else {
                        action = InventoryAction.NOTHING;
                    }
                }
                break;
            case CLONE:
                if (packetplayinwindowclick.getButtonNum() == 2) {
                    click = ClickType.MIDDLE;
                    if (packetplayinwindowclick.getSlotNum() < 0) {
                        action = InventoryAction.NOTHING;
                    } else {
                        ItemStack slot = inventory.getItem(rawSlot);
                        if (inventory.isSlot(rawSlot) && slot != null && player.getGamemode().equals(GameMode.CREATIVE) && inventory.getCarriedItem() == null) {
                            action = InventoryAction.CLONE_STACK;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    }
                } else {
                    click = ClickType.UNKNOWN;
                    action = InventoryAction.UNKNOWN;
                }
                break;
            case THROW:
                if (packetplayinwindowclick.getSlotNum() >= 0) {
                    if (packetplayinwindowclick.getButtonNum() == 0) {
                        click = ClickType.DROP;
                        ItemStack slot = inventory.getItem(rawSlot);
                        if (inventory.isSlot(rawSlot) && slot != null && !slot.type().equals(ItemStack.AIR.type())) {
                            action = InventoryAction.DROP_ONE_SLOT;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    } else if (packetplayinwindowclick.getButtonNum() == 1) {
                        click = ClickType.CONTROL_DROP;
                        ItemStack slot = inventory.getItem(rawSlot);
                        if (inventory.isSlot(rawSlot) && slot != null && !slot.type().equals(ItemStack.AIR.type())) {
                            action = InventoryAction.DROP_ALL_SLOT;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    }
                } else {
                    // Sane default (because this happens when they are holding nothing. Don't ask why.)
                    click = ClickType.LEFT;
                    if (packetplayinwindowclick.getButtonNum() == 1) {
                        click = ClickType.RIGHT;
                    }
                    action = InventoryAction.NOTHING;
                }
                break;
            case PICKUP_ALL:
                click = ClickType.DOUBLE_CLICK;
                action = InventoryAction.NOTHING;
                if (packetplayinwindowclick.getSlotNum() >= 0 && inventory.getCarriedItem() != null) {
                    ItemStack cursor = inventory.getCarriedItem();
                    int amount = cursor == null ? 0 : cursor.amount();
                    action = InventoryAction.NOTHING;
                    // Quick check for if we have any of the item
                    if ((inventory.getTopInventory() != null && inventory.getTopInventory().containsAtLeast(cursor, 1)) || inventory.getBottomInventory().containsAtLeast(cursor, 1)) {
                        action = InventoryAction.COLLECT_TO_CURSOR;
                    }
                }
                break;
            case QUICK_CRAFT: {
                QuickCraftInfo quickCraft;
                synchronized (QUICK_CRAFT_INFO) {
                    quickCraft = QUICK_CRAFT_INFO.get(player);
                    if (quickCraft == null) {
                        QUICK_CRAFT_INFO.put(player, quickCraft = new QuickCraftInfo());
                    }
                }
                int slotNum = packetplayinwindowclick.getSlotNum();
                int buttonNum = packetplayinwindowclick.getButtonNum();
                int quickcraftStatus = quickCraft.quickcraftStatus;
                ItemStack itemstack1;
                int l;

                quickCraft.quickcraftStatus = getQuickcraftHeader(buttonNum);
                if ((quickcraftStatus != 1 || quickCraft.quickcraftStatus != 2) && quickcraftStatus != quickCraft.quickcraftStatus) {
                    quickCraft.resetQuickCraft();
                } else if (inventory.getCarriedItem() == null) {
                    quickCraft.resetQuickCraft();
                } else if (quickCraft.quickcraftStatus == 0) {
                    quickCraft.quickcraftType = getQuickcraftType(buttonNum);
                    if (isValidQuickcraftType(quickCraft.quickcraftType, player)) {
                        quickCraft.quickcraftStatus = 1;
                        quickCraft.quickcraftSlots.clear();
                    } else {
                        quickCraft.resetQuickCraft();
                    }
                } else if (quickCraft.quickcraftStatus == 1) {
                    itemstack = inventory.getCarriedItem();
                    if (canItemQuickReplace(inventory, slotNum, itemstack, true) && (quickCraft.quickcraftType == 2 || itemstack.amount() > quickCraft.quickcraftSlots.size())) {
                        quickCraft.quickcraftSlots.add(slotNum);
                    }
                } else if (quickCraft.quickcraftStatus == 2) {
                    if (!quickCraft.quickcraftSlots.isEmpty()) {
                        itemstack1 = inventory.getCarriedItem();
                        l = inventory.getCarriedItem().amount();
                        Iterator<Integer> iterator = quickCraft.quickcraftSlots.iterator();

                        Map<Integer, ItemStack> draggedSlots = new HashMap<>(); // CraftBukkit - Store slots from drag in map (raw slot id -> new stack)
                        while (iterator.hasNext()) {
                            int slot1 = iterator.next();
                            ItemStack slotItem = inventory.getItem(slot1);
                            ItemStack itemstack2 = inventory.getCarriedItem();

                            if (inventory.isSlot(slot1) && canItemQuickReplace(inventory, slot1, slotItem, true) && (quickCraft.quickcraftType == 2 || itemstack2.amount() >= quickCraft.quickcraftSlots.size())) {
                                ItemStack itemstack3 = itemstack1;
                                int j1 = slotItem != null ? slotItem.amount() : 0;

                                itemstack3 = getQuickCraftSlotCount(quickCraft.quickcraftSlots, quickCraft.quickcraftType, itemstack3, j1);
                                int k1 = Math.min(itemstack3.getMaxStackSize(), slotItem == null ? 64 : slotItem.getMaxStackSize());

                                if (itemstack3.amount() > k1) {
                                    itemstack3 = itemstack3.amount(k1);
                                }

                                l -= itemstack3.amount() - j1;
                                // slot1.set(itemstack3);
                                draggedSlots.put(slot1, itemstack3); // CraftBukkit - Put in map instead of setting
                            }
                        }

                        // CraftBukkit start - InventoryDragEvent
                        ItemStack newcursor = itemstack1.amount(l);

                        // It's essential that we set the cursor to the new value here to prevent item duplication if a plugin closes the inventory.
                        ItemStack oldCursor = inventory.getCarriedItem();
                        inventory.setCarriedItem(newcursor);

                        InventoryDragEvent dragEvent = new InventoryDragEvent(inventory, (newcursor.type() != ItemStack.AIR.type() ? newcursor : null), oldCursor, quickCraft.quickcraftType == 1, draggedSlots);
                        Limbo.getInstance().getEventsManager().callEvent(dragEvent);

                        if (!dragEvent.isCancelled()) {
                            for (Map.Entry<Integer, ItemStack> dslot : draggedSlots.entrySet()) {
                                inventory.setItem(dslot.getKey(), dslot.getValue());
                            }
                            // The only time the carried item will be set to null is if the inventory is closed by the server.
                            // If the inventory is closed by the server, then the cursor items are dropped.  This is why we change the cursor early.
                            if (inventory.getCarriedItem() != null) {
                                inventory.setCarriedItem(dragEvent.getCarriedItem());
                            }
                        } else {
                            inventory.setCarriedItem(oldCursor);
                        }
                        inventory.updateView();
                    }

                    quickCraft.resetQuickCraft();
                } else {
                    quickCraft.resetQuickCraft();
                }
                break;
            }
            default:
                break;
        }
        if (packetplayinwindowclick.getClickType() != InventoryClickType.QUICK_CRAFT) {
            if (click == ClickType.NUMBER_KEY) {
                event = new InventoryClickEvent(inventory, type, packetplayinwindowclick.getSlotNum(), click, action, packetplayinwindowclick.getButtonNum());
            } else {
                event = new InventoryClickEvent(inventory, type, packetplayinwindowclick.getSlotNum(), click, action);
            }

            event.setCancelled(cancelled);
            Inventory oldTopInventory = player.getInventoryView().getTopInventory();
            Limbo.getInstance().getEventsManager().callEvent(event);
            if (player.getInventoryView().getTopInventory() != oldTopInventory) {
                return;
            }

            if (event.isCancelled()) {
                try {
                    switch (action) {
                        // Modified other slots
                        case PICKUP_ALL:
                        case MOVE_TO_OTHER_INVENTORY:
                        case HOTBAR_MOVE_AND_READD:
                        case HOTBAR_SWAP:
                        case COLLECT_TO_CURSOR:
                        case UNKNOWN:
                            player.getInventoryView().updateView();
                            break;
                        // Modified cursor and clicked
                        case PICKUP_SOME:
                        case PICKUP_HALF:
                        case PICKUP_ONE:
                        case PLACE_ALL:
                        case PLACE_SOME:
                        case PLACE_ONE:
                        case SWAP_WITH_CURSOR:
                            player.clientConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, 0, inventory.getCarriedItem()));
                            player.clientConnection.sendPacket(new PacketPlayOutSetSlot(inventory.getUnsafe().a(), 0, packetplayinwindowclick.getSlotNum(), inventory.getItem(packetplayinwindowclick.getSlotNum())));
                            break;
                        // Modified clicked only
                        case DROP_ALL_SLOT:
                        case DROP_ONE_SLOT:
                            player.clientConnection.sendPacket(new PacketPlayOutSetSlot(inventory.getUnsafe().a(), 0, packetplayinwindowclick.getSlotNum(), inventory.getItem(packetplayinwindowclick.getSlotNum())));
                            break;
                        // Modified cursor only
                        case DROP_ALL_CURSOR:
                        case DROP_ONE_CURSOR:
                        case CLONE_STACK:
                            player.clientConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, 0, inventory.getCarriedItem()));
                            break;
                        // Nothing
                        case NOTHING:
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                switch (event.getAction()) {
                    case PICKUP_ALL: {
                        inventory.setCarriedItem(event.getCurrentItem());
                        inventory.setItem(event.getRawSlot(), null);
                        break;
                    }
                    case PICKUP_SOME: {
                        int amountTaken = Math.min(event.getCurrentItem().getMaxStackSize(), event.getCurrentItem().amount());
                        inventory.setCarriedItem(event.getCurrentItem().amount(amountTaken));
                        ItemStack oversize = event.getCurrentItem();
                        inventory.setItem(event.getRawSlot(), oversize.amount(oversize.amount() - amountTaken));
                        break;
                    }
                    case PICKUP_HALF: {
                        int amountTaken = (int) Math.ceil((double) event.getCurrentItem().amount() / 2.0);
                        inventory.setCarriedItem(event.getCurrentItem().amount(amountTaken));
                        ItemStack left = event.getCurrentItem();
                        inventory.setItem(event.getRawSlot(), left.amount(left.amount() - amountTaken));
                        break;
                    }
                    case PICKUP_ONE: {
                        inventory.setCarriedItem(event.getCurrentItem().amount(1));
                        ItemStack left = event.getCurrentItem();
                        inventory.setItem(event.getRawSlot(), left.amount(left.amount() - 1));
                        break;
                    }
                    case PLACE_ALL: {
                        ItemStack stack = event.getCarriedItem();
                        inventory.setCarriedItem(null);
                        ItemStack item = event.getCurrentItem();
                        inventory.setItem(event.getRawSlot(), stack.amount((item == null ? 0 : item.amount()) + stack.amount()));
                        break;
                    }
                    case PLACE_SOME: {
                        ItemStack stack = event.getCarriedItem();
                        ItemStack item = event.getCurrentItem();
                        int amountPlaced = item.getMaxStackSize() - item.amount();
                        inventory.setItem(event.getRawSlot(), item.amount(item.getMaxStackSize()));
                        inventory.setCarriedItem(event.getCarriedItem().amount(event.getCarriedItem().amount() - amountPlaced));
                        break;
                    }
                    case PLACE_ONE: {
                        ItemStack stack = event.getCarriedItem();
                        ItemStack item = event.getCurrentItem();
                        inventory.setItem(event.getRawSlot(), item == null ? stack.amount(1) : item.amount(item.amount() + 1));
                        inventory.setCarriedItem(event.getCarriedItem().amount(event.getCarriedItem().amount() - 1));
                        break;
                    }
                    case SWAP_WITH_CURSOR: {
                        ItemStack stack = event.getCarriedItem();
                        inventory.setCarriedItem(event.getCurrentItem());
                        inventory.setItem(event.getRawSlot(), stack);
                        break;
                    }
                    case DROP_ALL_CURSOR: {
                        inventory.setCarriedItem(null);
                        break;
                    }
                    case DROP_ONE_CURSOR: {
                        inventory.setCarriedItem(event.getCarriedItem().amount(event.getCarriedItem().amount() - 1));
                        break;
                    }
                    case DROP_ALL_SLOT: {
                        inventory.setItem(event.getRawSlot(), null);
                        break;
                    }
                    case DROP_ONE_SLOT: {
                        ItemStack item = event.getCurrentItem();
                        inventory.setItem(event.getRawSlot(), item.amount(item.amount() - 1));
                        break;
                    }
                    case MOVE_TO_OTHER_INVENTORY: {
                        ItemStack item = event.getCurrentItem();
                        Inventory inv;
                        if (event.getClickedInventory() == inventory.getTopInventory()) {
                            inv = inventory.getBottomInventory();
                        } else {
                            inv = inventory.getTopInventory();
                        }
                        HashMap<Integer, ItemStack> leftOver = inv.addItem(item);
                        if (leftOver.isEmpty()) {
                            inventory.setItem(event.getRawSlot(), null);
                        } else {
                            inventory.setItem(event.getRawSlot(), leftOver.values().iterator().next());
                        }
                        break;
                    }
                    case HOTBAR_MOVE_AND_READD: {
                        ItemStack item = inventory.getPlayer().getInventory().getItem(event.getHotbarKey());
                        inventory.getPlayer().getInventory().setItem(event.getHotbarKey(), event.getCurrentItem());
                        inventory.setItem(event.getRawSlot(), null);
                        inventory.getPlayer().getInventory().addItem(item);
                        break;
                    }
                    case HOTBAR_SWAP: {
                        int hotbarNum = event.getClick().equals(ClickType.SWAP_OFFHAND) ? 40 : event.getHotbarKey();
                        ItemStack item = inventory.getPlayer().getInventory().getItem(hotbarNum);
                        inventory.getPlayer().getInventory().setItem(hotbarNum, event.getCurrentItem());
                        inventory.setItem(event.getRawSlot(), item);
                        break;
                    }
                    case CLONE_STACK: {
                        ItemStack item = event.getCurrentItem();
                        inventory.setCarriedItem(item.amount(item.getMaxStackSize()));
                        break;
                    }
                    case COLLECT_TO_CURSOR: {
                        ItemStack item = event.getCarriedItem();
                        ItemStack toSearch = item.amount(item.getMaxStackSize() - item.amount());
                        HashMap<Integer, ItemStack> grabbed = event.getClickedInventory().removeItem(toSearch);
                        int newAmount = item.amount() + toSearch.amount();
                        if (!grabbed.isEmpty()) {
                            newAmount -= grabbed.values().iterator().next().amount();
                        }
                        inventory.setCarriedItem(item.amount(newAmount));
                        break;
                    }
                }
            }
            inventory.updateView();
        }
    }

    public static int getQuickcraftType(int i) {
        return i >> 2 & 3;
    }

    public static int getQuickcraftHeader(int i) {
        return i & 3;
    }

    public static int getQuickcraftMask(int i, int j) {
        return i & 3 | (j & 3) << 2;
    }

    public static boolean isValidQuickcraftType(int i, Player player) {
        return i == 0 || (i == 1 || i == 2 && player.getGamemode().equals(GameMode.CREATIVE));
    }

    public static boolean canItemQuickReplace(InventoryView view, int slot, ItemStack itemstack, boolean flag) {
        boolean flag1 = !view.isSlot(slot) || view.getItem(slot) == null;
        ItemStack slotItem = view.getItem(slot);
        return !flag1 && slotItem.isSimilar(itemstack) ? slotItem.amount() + (flag ? 0 : itemstack.amount()) <= itemstack.getMaxStackSize() : flag1;
    }

    public static ItemStack getQuickCraftSlotCount(Set<Integer> set, int i, ItemStack itemstack, int j) {
        switch (i) {
            case 0:
                itemstack = itemstack.amount((int) Math.floor((float) itemstack.amount() / (float) set.size()));
                break;
            case 1:
                itemstack = itemstack.amount(1);
                break;
            case 2:
                itemstack = itemstack.amount(itemstack.getMaxStackSize());
        }
        return itemstack.amount(itemstack.amount() + j);
    }

    public static class QuickCraftInfo {

        public int quickcraftType;
        public int quickcraftStatus;
        public final Set<Integer> quickcraftSlots = ConcurrentHashMap.newKeySet();

        public void resetQuickCraft() {
            quickcraftStatus = 0;
            quickcraftSlots.clear();
        }

    }

}

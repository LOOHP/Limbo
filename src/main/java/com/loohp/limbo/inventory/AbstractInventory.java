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

import com.loohp.limbo.location.Location;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutSetSlot;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutWindowItems;
import com.loohp.limbo.player.Player;
import net.kyori.adventure.key.Key;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public abstract class AbstractInventory implements Inventory {

    protected final InventoryHolder inventoryHolder;
    protected final Map<Player, Integer> viewers;
    protected final InventoryType inventoryType;
    protected final AtomicReferenceArray<ItemStack> inventory;
    protected final InventoryUpdateListener listener;
    protected final IntUnaryOperator slotConvertor;
    protected final IntUnaryOperator inverseSlotConvertor;

    private final Unsafe unsafe;

    protected int maxStackSize;

    public AbstractInventory(int size, InventoryHolder inventoryHolder, InventoryType inventoryType, IntUnaryOperator slotConvertor, IntUnaryOperator inverseSlotConvertor) {
        this.inventoryHolder = inventoryHolder;
        this.viewers = new ConcurrentHashMap<>();
        this.inventoryType = inventoryType;
        this.inventory = new AtomicReferenceArray<>(size);
        this.slotConvertor = slotConvertor == null ? IntUnaryOperator.identity() : slotConvertor;
        this.inverseSlotConvertor = inverseSlotConvertor == null ? IntUnaryOperator.identity() : inverseSlotConvertor;
        this.listener = (inventory, slot, oldItem, newItem) -> {
            for (Map.Entry<Player, Integer> entry : viewers.entrySet()) {
                try {
                    PacketPlayOutSetSlot packet = new PacketPlayOutSetSlot(entry.getValue(), 0, this.slotConvertor.applyAsInt(slot), newItem);
                    entry.getKey().clientConnection.sendPacket(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        this.maxStackSize = 64;
        this.unsafe = new Unsafe(this);
    }

    @Override
    public void updateInventory(Player player) {
        Integer windowId = viewers.get(player);
        if (windowId == null) {
            return;
        }
        ItemStack[] itemStackArray = new ItemStack[IntStream.range(0, inventory.length()).map(slotConvertor).max().orElse(-1) + 1];
        for (int i = 0; i < inventory.length(); i++) {
            itemStackArray[slotConvertor.applyAsInt(i)] = getItem(i);
        }
        try {
            PacketPlayOutWindowItems packet = new PacketPlayOutWindowItems(windowId, 0, Arrays.asList(itemStackArray), ItemStack.AIR);
            player.clientConnection.sendPacket(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateInventory() {
        ItemStack[] itemStackArray = new ItemStack[IntStream.range(0, inventory.length()).map(slotConvertor).max().orElse(0)];
        for (int i = 0; i < inventory.length(); i++) {
            itemStackArray[slotConvertor.applyAsInt(i)] = getItem(i);
        }
        List<ItemStack> itemStacks = Arrays.asList(itemStackArray);
        for (Map.Entry<Player, Integer> entry : viewers.entrySet()) {
            try {
                PacketPlayOutWindowItems packet = new PacketPlayOutWindowItems(entry.getValue(), 0, itemStacks, ItemStack.AIR);
                entry.getKey().clientConnection.sendPacket(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Location getLocation() {
        return inventoryHolder == null ? null : inventoryHolder.getLocation();
    }

    @Override
    public int getSize() {
        return inventory.length();
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public void setMaxStackSize(int size) {
        this.maxStackSize = size;
    }

    @Override
    public ItemStack getItem(int index) {
        return inventory.get(index);
    }

    @Override
    public void setItem(int index, ItemStack item) {
        if (item != null && item.type().equals(ItemStack.AIR.type())) {
            item = null;
        }
        ItemStack oldItem = getItem(index);
        if (!Objects.equals(item, oldItem)) {
            inventory.set(index, item);
            listener.slotChanged(this, index, oldItem, item);
        }
    }

    public int firstPartial(Key material) {
        for (int i = 0; i < inventory.length(); i++) {
            ItemStack item = getItem(i);
            if (item != null && item.type().equals(material) && item.amount() < item.getMaxStackSize()) {
                return i;
            }
        }
        return -1;
    }

    private int firstPartial(ItemStack item) {
        if (item == null) {
            return -1;
        }
        for (int i = 0; i < inventory.length(); i++) {
            ItemStack cItem = getItem(i);
            if (cItem != null && cItem.amount() < cItem.getMaxStackSize() && cItem.isSimilar(item)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... items) throws IllegalArgumentException {
        HashMap<Integer, ItemStack> leftover = new HashMap<>();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            while (true) {
                // Do we already have a stack of it?
                int firstPartial = firstPartial(item);

                // Drat! no partial stack
                if (firstPartial == -1) {
                    // Find a free spot!
                    int firstFree = firstEmpty();

                    if (firstFree == -1) {
                        // No space at all!
                        leftover.put(i, item);
                        break;
                    } else {
                        // More than a single stack!
                        if (item.amount() > getMaxStackSize()) {
                            ItemStack stack = item.clone();
                            stack = stack.amount(getMaxStackSize());
                            setItem(firstFree, stack);
                            item = item.amount(item.amount() - getMaxStackSize());
                            items[i] = item;
                        } else {
                            // Just store it
                            setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do just that
                    ItemStack partialItem = getItem(firstPartial);

                    int amount = item.amount();
                    int partialAmount = partialItem.amount();
                    int maxAmount = partialItem.getMaxStackSize();

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem = partialItem.amount(amount + partialAmount);
                        // To make sure the packet is sent to the client
                        setItem(firstPartial, partialItem);
                        break;
                    }

                    // It fits partially
                    partialItem = partialItem.amount(maxAmount);
                    // To make sure the packet is sent to the client
                    setItem(firstPartial, partialItem);
                    item = item.amount(amount + partialAmount - maxAmount);
                    items[i] = item;
                }
            }
        }
        return leftover;
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) throws IllegalArgumentException {
        HashMap<Integer, ItemStack> leftover = new HashMap<>();

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            int toDelete = item.amount();

            while (true) {
                int first = first(item, false);

                // Drat! we don't have this type in the inventory
                if (first == -1) {
                    item = item.amount(toDelete);
                    items[i] = item;
                    leftover.put(i, item);
                    break;
                } else {
                    ItemStack itemStack = getItem(first);
                    int amount = itemStack.amount();

                    if (amount <= toDelete) {
                        toDelete -= amount;
                        // clear the slot, all used up
                        clear(first);
                    } else {
                        // split the stack and store
                        itemStack = itemStack.amount(amount - toDelete);
                        setItem(first, itemStack);
                        toDelete = 0;
                    }
                }

                // Bail when done
                if (toDelete <= 0) {
                    break;
                }
            }
        }
        return leftover;
    }

    @Override
    public ItemStack[] getContents() {
        return StreamSupport.stream(spliterator(), false).toArray(ItemStack[]::new);
    }

    @Override
    public void setContents(ItemStack[] items) throws IllegalArgumentException {
        if (getSize() < items.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getSize() + " or less");
        }

        for (int i = 0; i < getSize(); i++) {
            if (i >= items.length) {
                setItem(i, null);
            } else {
                setItem(i, items[i]);
            }
        }
    }

    @Override
    public ItemStack[] getStorageContents() {
        return getContents();
    }

    @Override
    public void setStorageContents(ItemStack[] items) throws IllegalArgumentException {
        setContents(items);
    }

    @Override
    public boolean contains(Key material) throws IllegalArgumentException {
        for (int i = 0; i < inventory.length(); i++) {
            ItemStack itemStack = getItem(i);
            if (itemStack != null && itemStack.type().equals(material)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(ItemStack item) {
        for (int i = 0; i < inventory.length(); i++) {
            ItemStack itemStack = getItem(i);
            if (Objects.equals(itemStack, item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(Key material, int amount) throws IllegalArgumentException {
        if (amount <= 0) {
            return true;
        }
        for (int i = 0; i < inventory.length(); i++) {
            ItemStack itemStack = getItem(i);
            if (itemStack != null && itemStack.type().equals(material)) {
                if ((amount -= itemStack.amount()) <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean contains(ItemStack item, int amount) {
        if (item == null) {
            return false;
        }
        if (amount <= 0) {
            return true;
        }
        for (int i = 0; i < inventory.length(); i++) {
            ItemStack itemStack = getItem(i);
            if (item.equals(itemStack) && --amount <= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAtLeast(ItemStack item, int amount) {
        if (item == null) {
            return false;
        }
        if (amount <= 0) {
            return true;
        }
        for (int i = 0; i < inventory.length(); i++) {
            ItemStack itemStack = getItem(i);
            if (item.isSimilar(itemStack) && (amount -= itemStack.amount()) <= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Key material) throws IllegalArgumentException {
        HashMap<Integer, ItemStack> slots = new HashMap<>();
        ItemStack[] inventory = getStorageContents();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && item.type().equals(material)) {
                slots.put(i, item);
            }
        }
        return slots;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
        HashMap<Integer, ItemStack> slots = new HashMap<>();
        if (item != null) {
            ItemStack[] inventory = getStorageContents();
            for (int i = 0; i < inventory.length; i++) {
                if (item.equals(inventory[i])) {
                    slots.put(i, inventory[i]);
                }
            }
        }
        return slots;
    }

    @Override
    public int first(Key material) throws IllegalArgumentException {
        for (int i = 0; i < inventory.length(); i++) {
            ItemStack item = getItem(i);
            if (item != null && item.type().equals(material)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int first(ItemStack item) {
        return first(item, true);
    }

    private int first(ItemStack item, boolean withAmount) {
        if (item == null) {
            return -1;
        }
        for (int i = 0; i < inventory.length(); i++) {
            ItemStack itemStack = inventory.get(i);
            if (itemStack == null) continue;

            if (withAmount ? item.equals(itemStack) : item.isSimilar(itemStack)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int firstEmpty() {
        for (int i = 0; i < inventory.length(); i++) {
            if (getItem(i) == null) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < inventory.length(); i++) {
            if (getItem(i) != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void remove(Key material) throws IllegalArgumentException {
        for (int i = 0; i < inventory.length(); i++) {
            ItemStack itemStack = getItem(i);
            if (itemStack != null && itemStack.type().equals(material)) {
                clear(i);
            }
        }
    }

    @Override
    public void remove(ItemStack item) {
        for (int i = 0; i < inventory.length(); i++) {
            ItemStack itemStack = getItem(i);
            if (itemStack != null && itemStack.equals(item)) {
                clear(i);
            }
        }
    }

    @Override
    public void clear(int index) {
        setItem(index, null);
    }

    @Override
    public void clear() {
        for (int i = 0; i < inventory.length(); i++) {
            setItem(i, null);
        }
    }

    @Override
    public Set<Player> getViewers() {
        return Collections.unmodifiableSet(viewers.keySet());
    }

    @Override
    public InventoryType getType() {
        return inventoryType;
    }

    @Override
    public InventoryHolder getHolder() {
        return inventoryHolder;
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return new InventoryIterator(this);
    }

    @Override
    public ListIterator<ItemStack> iterator(int index) {
        if (index < 0) {
            index += getSize() + 1; // ie, with -1, previous() will return the last element
        }
        return new InventoryIterator(this, index);
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public Unsafe getUnsafe() {
        return unsafe;
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public static class Unsafe implements Inventory.Unsafe {

        private final AbstractInventory inventory;

        @Deprecated
        public Unsafe(AbstractInventory inventory) {
            this.inventory = inventory;
        }

        @Deprecated
        public void a(int index, ItemStack itemStack) {
            if (itemStack != null && itemStack.type().equals(ItemStack.AIR.type())) {
                itemStack = null;
            }
            inventory.inventory.set(index, itemStack);
        }

        @Deprecated
        public void b(int index, ItemStack itemStack) {
            inventory.inventory.set(inventory.inverseSlotConvertor.applyAsInt(index), itemStack);
        }

        @Deprecated
        public IntUnaryOperator a() {
            return inventory.slotConvertor;
        }

        @Deprecated
        public IntUnaryOperator b() {
            return inventory.inverseSlotConvertor;
        }

        @Override
        public Map<Player, Integer> c() {
            return inventory.viewers;
        }
    }

}

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

import java.util.ListIterator;

public class InventoryIterator implements ListIterator<ItemStack> {

    private final Inventory inventory;
    private int nextIndex;
    private Boolean lastDirection; // true = forward, false = backward, null = haven't moved yet

    InventoryIterator(Inventory inventory) {
        this.inventory = inventory;
        this.nextIndex = 0;
    }

    InventoryIterator(Inventory inventory, int index) {
        this.inventory = inventory;
        this.nextIndex = index;
    }

    @Override
    public boolean hasNext() {
        return nextIndex < inventory.getSize();
    }

    @Override
    public ItemStack next() {
        lastDirection = true;
        return inventory.getItem(nextIndex++);
    }

    @Override
    public int nextIndex() {
        return nextIndex;
    }

    @Override
    public boolean hasPrevious() {
        return nextIndex > 0;
    }

    @Override
    public ItemStack previous() {
        lastDirection = false;
        return inventory.getItem(--nextIndex);
    }

    @Override
    public int previousIndex() {
        return nextIndex - 1;
    }

    @Override
    public void set(ItemStack item) {
        if (lastDirection == null) {
            throw new IllegalStateException("No current item!");
        }
        int i = lastDirection ? nextIndex - 1 : nextIndex;
        inventory.setItem(i, item);
    }

    @Override
    public void add(ItemStack item) {
        throw new UnsupportedOperationException("Can't change the size of an inventory!");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Can't change the size of an inventory!");
    }
}
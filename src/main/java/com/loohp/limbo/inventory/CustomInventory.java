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

import net.kyori.adventure.text.Component;

public class CustomInventory extends AbstractInventory implements TitledInventory {

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public static CustomInventory create(Component title, int size, InventoryHolder inventoryHolder) {
        if (size % 9 != 0 || size > 54 || size < 9) {
            throw new IllegalArgumentException("size must be a multiple of 9 and within 9 - 54");
        }
        return new CustomInventory(title, size, inventoryHolder);
    }

    private Component title;

    private CustomInventory(Component title, int size, InventoryHolder inventoryHolder) {
        super(size, inventoryHolder, InventoryType.CHEST, null, null);
        this.title = title;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    public void setTitle(Component title) {
        this.title = title;
    }

}

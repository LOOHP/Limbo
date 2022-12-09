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

public class AnvilInventory extends AbstractInventory implements TitledInventory {

    public static final Component DEFAULT_TITLE = Component.translatable("container.repair");

    private Component title;

    public AnvilInventory(Component title, InventoryHolder inventoryHolder) {
        super(InventoryType.ANVIL.getDefaultSize(), inventoryHolder, InventoryType.ANVIL, null, null);
        this.title = title == null ? DEFAULT_TITLE : title;
    }

    public void setTitle(Component title) {
        this.title = title;
    }

    @Override
    public Component getTitle() {
        return title;
    }
}

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

package com.loohp.limbo.entity;

import com.loohp.limbo.inventory.EquipmentSlot;
import com.loohp.limbo.inventory.ItemStack;

/**
 * An interface to a creatures inventory
 */
public interface EntityEquipment {

    /**
     * Stores the ItemStack at the given equipment slot in the inventory.
     *
     * @param slot the slot to put the ItemStack
     * @param item the ItemStack to set
     */
    void setItem(EquipmentSlot slot, ItemStack item);

    /**
     * Gets the ItemStack at the given equipment slot in the inventory.
     *
     * @param slot the slot to get the ItemStack
     * @return the ItemStack in the given slot
     */
    ItemStack getItem(EquipmentSlot slot);

    /**
     * Gets a copy of the item the entity is currently holding
     * in their main hand.
     *
     * @return the currently held item
     */
    ItemStack getItemInMainHand();

    /**
     * Sets the item the entity is holding in their main hand.
     *
     * @param item The item to put into the entities hand
     */
    void setItemInMainHand(ItemStack item);

    /**
     * Gets a copy of the item the entity is currently holding
     * in their off hand.
     *
     * @return the currently held item
     */
    ItemStack getItemInOffHand();

    /**
     * Sets the item the entity is holding in their off hand.
     *
     * @param item The item to put into the entities hand
     */
    void setItemInOffHand(ItemStack item);

    /**
     * Gets a copy of the helmet currently being worn by the entity
     *
     * @return The helmet being worn
     */
    ItemStack getHelmet();

    /**
     * Sets the helmet worn by the entity
     *
     * @param helmet The helmet to put on the entity
     */
    void setHelmet(ItemStack helmet);

    /**
     * Gets a copy of the chest plate currently being worn by the entity
     *
     * @return The chest plate being worn
     */
    ItemStack getChestplate();

    /**
     * Sets the chest plate worn by the entity
     *
     * @param chestplate The chest plate to put on the entity
     */
    void setChestplate(ItemStack chestplate);

    /**
     * Gets a copy of the leggings currently being worn by the entity
     *
     * @return The leggings being worn
     */
    ItemStack getLeggings();

    /**
     * Sets the leggings worn by the entity
     *
     * @param leggings The leggings to put on the entity
     */
    void setLeggings(ItemStack leggings);

    /**
     * Gets a copy of the boots currently being worn by the entity
     *
     * @return The boots being worn
     */
    ItemStack getBoots();

    /**
     * Sets the boots worn by the entity
     *
     * @param boots The boots to put on the entity
     */
    void setBoots(ItemStack boots);

    /**
     * Gets a copy of all worn armor
     *
     * @return The array of worn armor. Individual items may be null.
     */
    ItemStack[] getArmorContents();

    /**
     * Sets the entities armor to the provided array of ItemStacks
     *
     * @param items The items to set the armor as. Individual items may be null.
     */
    void setArmorContents(ItemStack[] items);

    /**
     * Clears the entity of all armor and held items
     */
    void clear();

    /**
     * Get the entity this EntityEquipment belongs to
     *
     * @return the entity this EntityEquipment belongs to
     */
    Entity getHolder();

}
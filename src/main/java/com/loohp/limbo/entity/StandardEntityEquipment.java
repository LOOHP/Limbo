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

import java.util.EnumMap;

public class StandardEntityEquipment implements EntityEquipment {

    private final Entity entity;
    private final EnumMap<EquipmentSlot, ItemStack> itemStacks;

    public StandardEntityEquipment(Entity entity) {
        this.entity = entity;
        this.itemStacks = new EnumMap<>(EquipmentSlot.class);
    }

    @Override
    public void setItem(EquipmentSlot slot, ItemStack item) {

    }

    @Override
    public ItemStack getItem(EquipmentSlot slot) {
        return itemStacks.get(slot);
    }

    @Override
    public ItemStack getItemInMainHand() {
        return itemStacks.get(EquipmentSlot.MAINHAND);
    }

    @Override
    public void setItemInMainHand(ItemStack item) {

    }

    @Override
    public ItemStack getItemInOffHand() {
        return getItem(EquipmentSlot.OFFHAND);
    }

    @Override
    public void setItemInOffHand(ItemStack item) {

    }

    @Override
    public ItemStack getHelmet() {
        return getItem(EquipmentSlot.HELMET);
    }

    @Override
    public void setHelmet(ItemStack helmet) {

    }

    @Override
    public ItemStack getChestplate() {
        return getItem(EquipmentSlot.CHESTPLATE);
    }

    @Override
    public void setChestplate(ItemStack chestplate) {

    }

    @Override
    public ItemStack getLeggings() {
        return getItem(EquipmentSlot.LEGGINGS);
    }

    @Override
    public void setLeggings(ItemStack leggings) {

    }

    @Override
    public ItemStack getBoots() {
        return getItem(EquipmentSlot.BOOTS);
    }

    @Override
    public void setBoots(ItemStack boots) {

    }

    @Override
    public ItemStack[] getArmorContents() {
        return itemStacks.values().toArray(new ItemStack[0]);
    }

    @Override
    public void setArmorContents(ItemStack[] items) {
        if (items.length != 6) {
            throw new IllegalArgumentException("items must have a length of 6");
        }
        EquipmentSlot[] equipmentSlots = EquipmentSlot.values();
        int i = 0;
        for (EquipmentSlot equipmentSlot : equipmentSlots) {
            setItem(equipmentSlot, items[i++]);
        }
    }

    @Override
    public void clear() {
        for (EquipmentSlot equipmentSlot : itemStacks.keySet()) {
            setItem(equipmentSlot, null);
        }
    }

    @Override
    public Entity getHolder() {
        return null;
    }
}

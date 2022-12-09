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

package com.loohp.limbo.player;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.loohp.limbo.entity.EntityEquipment;
import com.loohp.limbo.inventory.AbstractInventory;
import com.loohp.limbo.inventory.EquipmentSlot;
import com.loohp.limbo.inventory.InventoryType;
import com.loohp.limbo.inventory.ItemStack;
import com.loohp.limbo.location.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.ToIntFunction;

public class PlayerInventory extends AbstractInventory implements EntityEquipment {

    private static final Map<EquipmentSlot, ToIntFunction<PlayerInventory>> EQUIPMENT_SLOT_MAPPING;
    private static final BiMap<Integer, Integer> SLOT_MAPPING;

    static {
        Map<EquipmentSlot, ToIntFunction<PlayerInventory>> equipmentSlotMapping = new EnumMap<>(EquipmentSlot.class);
        equipmentSlotMapping.put(EquipmentSlot.MAINHAND, i -> i.getHolder().selectedSlot);
        equipmentSlotMapping.put(EquipmentSlot.OFFHAND, i -> 40);
        equipmentSlotMapping.put(EquipmentSlot.BOOTS, i -> 36);
        equipmentSlotMapping.put(EquipmentSlot.LEGGINGS, i -> 37);
        equipmentSlotMapping.put(EquipmentSlot.CHESTPLATE, i -> 38);
        equipmentSlotMapping.put(EquipmentSlot.HELMET, i -> 39);
        EQUIPMENT_SLOT_MAPPING = Collections.unmodifiableMap(equipmentSlotMapping);

        BiMap<Integer, Integer> slotMapping = HashBiMap.create(41);
        for (int i = 0; i < 9; i++) {
            slotMapping.put(i, i + 36);
        }
        for (int i = 9; i < 36; i++) {
            slotMapping.put(i, i);
        }
        for (int i = 36; i < 40; i++) {
            slotMapping.put(i, i - 31);
        }
        slotMapping.put(40, 45);
        SLOT_MAPPING = ImmutableBiMap.copyOf(slotMapping);
    }

    private final Player player;

    public PlayerInventory(Player player) {
        super(InventoryType.PLAYER.getDefaultSize(), player, InventoryType.PLAYER, i -> SLOT_MAPPING.getOrDefault(i, i), i -> SLOT_MAPPING.inverse().getOrDefault(i, i));
        this.player = player;
        this.viewers.put(player, 0);
    }

    @Override
    public Player getHolder() {
        return player;
    }

    @Override
    public void setItem(EquipmentSlot slot, ItemStack item) {
        setItem(EQUIPMENT_SLOT_MAPPING.get(slot).applyAsInt(this), item);
    }

    @Override
    public ItemStack getItem(EquipmentSlot slot) {
        return getItem(EQUIPMENT_SLOT_MAPPING.get(slot).applyAsInt(this));
    }

    @Override
    public ItemStack getItemInMainHand() {
        return getItem(EquipmentSlot.MAINHAND);
    }

    @Override
    public void setItemInMainHand(ItemStack item) {
        setItem(EquipmentSlot.MAINHAND, item);
    }

    @Override
    public ItemStack getItemInOffHand() {
        return getItem(EquipmentSlot.OFFHAND);
    }

    @Override
    public void setItemInOffHand(ItemStack item) {
        setItem(EquipmentSlot.OFFHAND, item);
    }

    @Override
    public ItemStack getHelmet() {
        return getItem(EquipmentSlot.HELMET);
    }

    @Override
    public void setHelmet(ItemStack helmet) {
        setItem(EquipmentSlot.HELMET, helmet);
    }

    @Override
    public ItemStack getChestplate() {
        return getItem(EquipmentSlot.CHESTPLATE);
    }

    @Override
    public void setChestplate(ItemStack chestplate) {
        setItem(EquipmentSlot.CHESTPLATE, chestplate);
    }

    @Override
    public ItemStack getLeggings() {
        return getItem(EquipmentSlot.LEGGINGS);
    }

    @Override
    public void setLeggings(ItemStack leggings) {
        setItem(EquipmentSlot.LEGGINGS, leggings);
    }

    @Override
    public ItemStack getBoots() {
        return getItem(EquipmentSlot.BOOTS);
    }

    @Override
    public void setBoots(ItemStack boots) {
        setItem(EquipmentSlot.BOOTS, boots);
    }

    @Override
    public ItemStack[] getArmorContents() {
        return Arrays.stream(EquipmentSlot.values()).filter(EquipmentSlot::isArmorSlot).map(this::getItem).toArray(ItemStack[]::new);
    }

    @Override
    public void setArmorContents(ItemStack[] items) {
        int i = 0;
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot.isArmorSlot()) {
                if (i < items.length) {
                    setItem(equipmentSlot, items[i]);
                }
                i++;
            }
        }
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

}

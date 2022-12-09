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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;
import java.util.Objects;

public class ItemStack implements Cloneable {

    public static final ItemStack AIR = new ItemStack(Key.key("minecraft:air"));

    private final Key material;
    private final int amount;
    private final CompoundTag nbt;

    private CompoundTag fullTag;

    public ItemStack(Key material) {
        this(material, 1);
    }

    public ItemStack(Key material, int amount) {
        this(material, amount, null);
    }

    public ItemStack(Key material, int amount, CompoundTag nbt) {
        this.material = material;
        this.amount = amount;
        this.nbt = nbt;
        this.fullTag = null;
    }

    public ItemStack(CompoundTag fullTag) {
        this.material = Key.key(fullTag.getString("id"));
        this.amount = fullTag.getInt("Count");
        this.nbt = fullTag.containsKey("tag") ? fullTag.getCompoundTag("tag") : null;
        this.fullTag = fullTag.clone();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ItemStack clone() {
        return new ItemStack(material, amount, nbt == null ? null : nbt.clone());
    }

    public Key type() {
        return material;
    }

    public ItemStack type(Key material) {
        return new ItemStack(material, amount, nbt == null ? null : nbt.clone());
    }

    public int amount() {
        return amount;
    }

    public ItemStack amount(int amount) {
        return new ItemStack(material, amount, nbt == null ? null : nbt.clone());
    }

    public CompoundTag nbt() {
        return nbt;
    }

    public ItemStack nbt(CompoundTag nbt) {
        return new ItemStack(material, amount, nbt == null ? null : nbt.clone());
    }

    public Component displayName() {
        if (type().equals(AIR.type()) || nbt == null) {
            return null;
        }
        CompoundTag displayTag = nbt.getCompoundTag("display");
        if (displayTag == null) {
            return null;
        }
        String json = displayTag.getString("Name");
        if (json == null) {
            return null;
        }
        try {
            return GsonComponentSerializer.gson().deserialize(json);
        } catch (Exception e) {
            return null;
        }
    }

    public ItemStack displayName(Component component) {
        if (type().equals(AIR.type())) {
            return this;
        }
        try {
            String json = GsonComponentSerializer.gson().serialize(component);
            CompoundTag nbt = this.nbt.clone();
            CompoundTag displayTag = nbt.getCompoundTag("display");
            if (displayTag == null) {
                nbt.put("display", displayTag = new CompoundTag());
            }
            displayTag.putString("Name", json);
            return nbt(nbt);
        } catch (Exception ignore) {
        }
        return this;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public CompoundTag getFullTag() {
        if (fullTag != null) {
            return fullTag;
        }
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", material.toString());
        compoundTag.putInt("Count", amount);
        if (nbt != null) {
            compoundTag.put("tag", nbt);
        }
        return fullTag = compoundTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemStack itemStack = (ItemStack) o;
        return amount == itemStack.amount && material.equals(itemStack.material) && Objects.equals(nbt, itemStack.nbt);
    }

    public boolean isSimilar(ItemStack stack) {
        return stack != null && material.equals(stack.material) && Objects.equals(nbt, stack.nbt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, amount, nbt);
    }

    @Override
    public String toString() {
        try {
            return SNBTUtil.toSNBT(getFullTag());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

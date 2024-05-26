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

import com.loohp.limbo.registry.DataComponentType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.querz.nbt.tag.Tag;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ItemStack implements Cloneable {

    public static final ItemStack AIR = new ItemStack(Key.key("minecraft:air"));

    private final Key material;
    private final int amount;
    private final Map<Key, Tag<?>> components;

    public ItemStack(Key material) {
        this(material, 1);
    }

    public ItemStack(Key material, int amount) {
        this(material, amount, Collections.emptyMap());
    }

    public ItemStack(Key material, int amount, Map<Key, Tag<?>> components) {
        this.material = material;
        this.amount = amount;
        this.components = components;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ItemStack clone() {
        return new ItemStack(material, amount, components);
    }

    public Key type() {
        return material;
    }

    public ItemStack type(Key material) {
        return new ItemStack(material, amount, components);
    }

    public int amount() {
        return amount;
    }

    public ItemStack amount(int amount) {
        return new ItemStack(material, amount, components);
    }

    public Map<Key, Tag<?>> components() {
        return new HashMap<>(components);
    }

    public ItemStack components(Map<Key, Tag<?>> components) {
        return new ItemStack(material, amount, components);
    }

    public <T> T component(DataComponentType<T> type) {
        return type.getCodec().decode(components.get(type.getKey()));
    }

    public <T> ItemStack component(DataComponentType<T> type, T value) {
        Map<Key, Tag<?>> components = components();
        components.put(type.getKey(), type.getCodec().encode(value));
        return components(components);
    }

    public Component displayName() {
        if (type().equals(AIR.type()) || components == null) {
            return null;
        }
        try {
            return component(DataComponentType.CUSTOM_NAME);
        } catch (Exception e) {
            return null;
        }
    }

    public ItemStack displayName(Component component) {
        if (type().equals(AIR.type())) {
            return this;
        }
        return component(DataComponentType.CUSTOM_NAME, component);
    }

    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemStack itemStack = (ItemStack) o;
        return amount == itemStack.amount && material.equals(itemStack.material) && Objects.equals(components, itemStack.components);
    }

    public boolean isSimilar(ItemStack stack) {
        return stack != null && material.equals(stack.material) && Objects.equals(components, stack.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, amount, components);
    }

    @Override
    public String toString() {
        return "ItemStack{" +
                "material=" + material +
                ", amount=" + amount +
                ", components=" + components +
                '}';
    }
}

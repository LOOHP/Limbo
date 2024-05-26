/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2024. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2024. Contributors
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

package com.loohp.limbo.registry;

import com.google.gson.JsonElement;
import com.loohp.limbo.utils.NbtComponentSerializer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.querz.nbt.tag.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DataComponentType<T> {

    private static final Map<Key, DataComponentType<?>> REGISTERED_TYPES = new HashMap<>();

    public static final DataComponentType<Component> CUSTOM_NAME = register(new DataComponentType<>("custom_name", new DataComponentCodec<>(component -> {
        JsonElement element = GsonComponentSerializer.gson().serializeToTree(component);
        return NbtComponentSerializer.jsonComponentToTag(element);
    }, tag -> {
        JsonElement element = NbtComponentSerializer.tagComponentToJson(tag);
        return GsonComponentSerializer.gson().deserializeFromTree(element);
    })));

    public static <T> DataComponentType<T> register(DataComponentType<T> type) {
        REGISTERED_TYPES.put(type.getKey(), type);
        return type;
    }

    public static boolean isKnownType(Key key) {
        return REGISTERED_TYPES.containsKey(key);
    }

    private final Key key;
    private final DataComponentCodec<T> codec;

    @SuppressWarnings("PatternValidation")
    public DataComponentType(String key, DataComponentCodec<T> codec) {
        this(Key.key(key), codec);
    }

    public DataComponentType(Key key, DataComponentCodec<T> codec) {
        this.key = key;
        this.codec = codec;
    }

    public Key getKey() {
        return key;
    }

    public DataComponentCodec<T> getCodec() {
        return codec;
    }

    public static class DataComponentCodec<T> {

        private final Function<T, Tag<?>> encode;
        private final Function<Tag<?>, T> decode;

        public DataComponentCodec(Function<T, Tag<?>> encode, Function<Tag<?>, T> decode) {
            this.encode = encode;
            this.decode = decode;
        }

        public Tag<?> encode(T t) {
            return encode.apply((T) t);
        }

        public T decode(Tag<?> tag) {
            return decode.apply(tag);
        }
    }

}

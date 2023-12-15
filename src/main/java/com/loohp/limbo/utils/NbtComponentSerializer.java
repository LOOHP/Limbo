/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2023. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2023. Contributors
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

package com.loohp.limbo.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;
import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.EndTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.LongArrayTag;
import net.querz.nbt.tag.LongTag;
import net.querz.nbt.tag.NumberTag;
import net.querz.nbt.tag.ShortTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Taken from <a href="https://github.com/GeyserMC/MCProtocolLib/blob/master/src/main/java/com/github/steveice10/mc/protocol/codec/NbtComponentSerializer.java">MCProtocolLib's NbtComponentSerializer</a>
 */
public class NbtComponentSerializer {

    private static final Set<String> BOOLEAN_TYPES = new HashSet<>(Arrays.asList(
            "interpret",
            "bold",
            "italic",
            "underlined",
            "strikethrough",
            "obfuscated"
    ));
    // Order is important
    private static final List<Pair<String, String>> COMPONENT_TYPES = Arrays.asList(
            new Pair<>("text", "text"),
            new Pair<>("translatable", "translate"),
            new Pair<>("score", "score"),
            new Pair<>("selector", "selector"),
            new Pair<>("keybind", "keybind"),
            new Pair<>("nbt", "nbt")
    );

    private NbtComponentSerializer() {

    }
    
    public static JsonElement tagComponentToJson(Tag<?> tag) {
        return convertToJson(null, tag);
    }


    public static Tag<?> jsonComponentToTag(JsonElement component) {
        return convertToTag(component);
    }
    
    private static Tag<?> convertToTag(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        } else if (element.isJsonObject()) {
            final CompoundTag tag = new CompoundTag();
            final JsonObject jsonObject = element.getAsJsonObject();
            for (final Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                convertObjectEntry(entry.getKey(), entry.getValue(), tag);
            }

            addComponentType(jsonObject, tag);
            return tag;
        } else if (element.isJsonArray()) {
            return convertJsonArray(element.getAsJsonArray());
        } else if (element.isJsonPrimitive()) {
            final JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                return new StringTag(primitive.getAsString());
            } else if (primitive.isBoolean()) {
                return new ByteTag((byte) (primitive.getAsBoolean() ? 1 : 0));
            }

            final Number number = primitive.getAsNumber();
            if (number instanceof Integer) {
                return new IntTag(number.intValue());
            } else if (number instanceof Byte) {
                return new ByteTag(number.byteValue());
            } else if (number instanceof Short) {
                return new ShortTag(number.shortValue());
            } else if (number instanceof Long) {
                return new LongTag(number.longValue());
            } else if (number instanceof Double) {
                return new DoubleTag(number.doubleValue());
            } else if (number instanceof Float) {
                return new FloatTag(number.floatValue());
            } else if (number instanceof LazilyParsedNumber) {
                // TODO: This might need better handling
                return new IntTag(number.intValue());
            }
            return new IntTag(number.intValue()); // ???
        }
        throw new IllegalArgumentException("Unhandled json type " + element.getClass().getSimpleName() + " with value " + element.getAsString());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ListTag<?> convertJsonArray(JsonArray array) {
        // TODO Number arrays?
        final ListTag listTag = ListTag.createUnchecked(EndTag.class);
        boolean singleType = true;
        for (final JsonElement entry : array) {
            final Tag<?> convertedEntryTag = convertToTag(entry);
            if (listTag.getTypeClass() != null && listTag.getTypeClass() != convertedEntryTag.getClass()) {
                singleType = false;
                break;
            }

            listTag.add(convertedEntryTag);
        }

        if (singleType) {
            return listTag;
        }

        // Generally, vanilla-esque serializers should not produce this format, so it should be rare
        // Lists are only used for lists of components ("extra" and "with")
        final ListTag processedListTag = ListTag.createUnchecked(EndTag.class);
        for (final JsonElement entry : array) {
            final Tag<?> convertedTag = convertToTag(entry);
            if (convertedTag instanceof CompoundTag) {
                processedListTag.add(convertedTag);
                continue;
            }

            // Wrap all entries in compound tags, as lists can only consist of one type of tag
            final CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("type", new StringTag("text"));
            if (convertedTag instanceof ListTag) {
                compoundTag.put("text", new StringTag());
                compoundTag.put("extra", convertedTag.clone());
            } else {
                compoundTag.put("text", new StringTag(stringValue(convertedTag)));
            }
            processedListTag.add(compoundTag);
        }
        return processedListTag;
    }

    /**
     * Converts a json object entry to a tag entry.
     *
     * @param key   key of the entry
     * @param value value of the entry
     * @param tag   the resulting compound tag
     */
    private static void convertObjectEntry(final String key, final JsonElement value, final CompoundTag tag) {
        if ((key.equals("contents")) && value.isJsonObject()) {
            // Store show_entity id as int array instead of uuid string
            // Not really required, but we might as well make it more compact
            final JsonObject hoverEvent = value.getAsJsonObject();
            final JsonElement id = hoverEvent.get("id");
            final UUID uuid;
            if (id != null && id.isJsonPrimitive() && (uuid = parseUUID(id.getAsString())) != null) {
                hoverEvent.remove("id");

                final CompoundTag convertedTag = (CompoundTag) convertToTag(value);
                convertedTag.put("id", new IntArrayTag(toIntArray(uuid)));
                tag.put(key, convertedTag);
                return;
            }
        }

        tag.put(key, convertToTag(value));
    }

    private static void addComponentType(final JsonObject object, final CompoundTag tag) {
        if (object.has("type")) {
            return;
        }

        // Add the type to speed up deserialization and make DFU errors slightly more useful
        for (final Pair<String, String> pair : COMPONENT_TYPES) {
            if (object.has(pair.value)) {
                tag.put("type", new StringTag(pair.key));
                return;
            }
        }
    }

    private static JsonElement convertToJson(final String key, final Tag<?> tag) {
        if (tag == null) {
            return null;
        } else if (tag instanceof CompoundTag) {
            final JsonObject object = new JsonObject();
            if (!"value".equals(key)) {
                removeComponentType(object);
            }

            for (final Map.Entry<String, Tag<?>> entry : ((CompoundTag) tag).entrySet()) {
                convertCompoundTagEntry(entry.getKey(), entry.getValue(), object);
            }
            return object;
        } else if (tag instanceof ListTag<?>) {
            final ListTag<?> list = (ListTag<?>) tag;
            final JsonArray array = new JsonArray();
            for (final Tag<?> listEntry : list) {
                array.add(convertToJson(null, listEntry));
            }
            return array;
        } else if (tag instanceof NumberTag<?>) {
            if (key != null && BOOLEAN_TYPES.contains(key)) {
                // Booleans don't have a direct representation in nbt
                return new JsonPrimitive(((NumberTag<?>) tag).asByte() != 0);
            }
            if (tag instanceof ByteTag) {
                return new JsonPrimitive(((ByteTag) tag).asByte());
            } else if (tag instanceof ShortTag) {
                return new JsonPrimitive(((ShortTag) tag).asShort());
            } else if (tag instanceof IntTag) {
                return new JsonPrimitive(((IntTag) tag).asInt());
            } else if (tag instanceof LongTag) {
                return new JsonPrimitive(((LongTag) tag).asLong());
            } else if (tag instanceof FloatTag) {
                return new JsonPrimitive(((FloatTag) tag).asFloat());
            } else if (tag instanceof DoubleTag) {
                return new JsonPrimitive(((DoubleTag) tag).asDouble());
            }
            return new JsonPrimitive(((NumberTag<?>) tag).asDouble());
        } else if (tag instanceof StringTag) {
            return new JsonPrimitive(((StringTag) tag).getValue());
        } else if (tag instanceof ByteArrayTag) {
            final ByteArrayTag arrayTag = (ByteArrayTag) tag;
            final JsonArray array = new JsonArray();
            for (final byte num : arrayTag.getValue()) {
                array.add(num);
            }
            return array;
        } else if (tag instanceof IntArrayTag) {
            final IntArrayTag arrayTag = (IntArrayTag) tag;
            final JsonArray array = new JsonArray();
            for (final int num : arrayTag.getValue()) {
                array.add(num);
            }
            return array;
        } else if (tag instanceof LongArrayTag) {
            final LongArrayTag arrayTag = (LongArrayTag) tag;
            final JsonArray array = new JsonArray();
            for (final long num : arrayTag.getValue()) {
                array.add(num);
            }
            return array;
        }
        throw new IllegalArgumentException("Unhandled tag type " + tag.getClass().getSimpleName());
    }

    private static void convertCompoundTagEntry(final String key, final Tag<?> tag, final JsonObject object) {
        if ((key.equals("contents")) && tag instanceof CompoundTag) {
            // Back to a UUID string
            final CompoundTag showEntity = (CompoundTag) tag;
            final Tag<?> idTag = showEntity.get("id");
            if (idTag instanceof IntArrayTag) {
                showEntity.remove("id");

                final JsonObject convertedElement = (JsonObject) convertToJson(key, tag);
                final UUID uuid = fromIntArray(((IntArrayTag) idTag).getValue());
                convertedElement.addProperty("id", uuid.toString());
                object.add(key, convertedElement);
                return;
            }
        }

        // "":1 is a valid tag, but not a valid json component
        object.add(key.isEmpty() ? "text" : key, convertToJson(key, tag));
    }

    private static void removeComponentType(final JsonObject object) {
        final JsonElement type = object.remove("type");
        if (type == null || !type.isJsonPrimitive()) {
            return;
        }

        // Remove the other fields
        final String typeString = type.getAsString();
        for (final Pair<String, String> pair : COMPONENT_TYPES) {
            if (!pair.key.equals(typeString)) {
                object.remove(pair.value);
            }
        }
    }

    // Last adopted from https://github.com/ViaVersion/ViaVersion/blob/8e38e25cbad1798abb628b4994f4047eaf64640d/common/src/main/java/com/viaversion/viaversion/util/UUIDUtil.java
    public static UUID fromIntArray(final int[] parts) {
        if (parts.length != 4) {
            return new UUID(0, 0);
        }
        return new UUID((long) parts[0] << 32 | (parts[1] & 0xFFFFFFFFL), (long) parts[2] << 32 | (parts[3] & 0xFFFFFFFFL));
    }

    public static int[] toIntArray(final UUID uuid) {
        return toIntArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    public static int[] toIntArray(final long msb, final long lsb) {
        return new int[]{(int) (msb >> 32), (int) msb, (int) (lsb >> 32), (int) lsb};
    }

    public static UUID parseUUID(final String uuidString) {
        try {
            return UUID.fromString(uuidString);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    // Last adopted from https://github.com/ViaVersion/ViaNBT/commit/ad8ac024c48c2fc25e18dc689b3ca62602420ab9
    private static String stringValue(Tag<?> tag) {
        if (tag instanceof ByteArrayTag) {
            return Arrays.toString(((ByteArrayTag) tag).getValue());
        } else if (tag instanceof ByteTag) {
            return Byte.toString(((ByteTag) tag).asByte());
        } else if (tag instanceof DoubleTag) {
            return Double.toString(((DoubleTag) tag).asDouble());
        } else if (tag instanceof FloatTag) {
            return Float.toString(((FloatTag) tag).asFloat());
        } else if (tag instanceof IntArrayTag) {
            return Arrays.toString(((IntArrayTag) tag).getValue());
        } else if (tag instanceof IntTag) {
            return Integer.toString(((IntTag) tag).asInt());
        } else if (tag instanceof LongArrayTag) {
            return Arrays.toString(((LongArrayTag) tag).getValue());
        } else if (tag instanceof LongTag) {
            return Long.toString(((LongTag) tag).asLong());
        } else if (tag instanceof ShortTag) {
            return Short.toString(((ShortTag) tag).asShort());
        } else if (tag instanceof StringTag) {
            return ((StringTag) tag).getValue();
        } else {
            return tag.valueToString();
        }
    }
    
    private static class Pair<K, V> {
        
        private final K key;
        private final V value;

        private Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
        
    }
}
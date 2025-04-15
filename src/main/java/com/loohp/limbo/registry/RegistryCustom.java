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

import com.loohp.limbo.Limbo;
import com.loohp.limbo.utils.ClasspathResourcesUtils;
import com.loohp.limbo.utils.CustomNBTUtils;
import net.kyori.adventure.key.Key;
import net.querz.nbt.tag.CompoundTag;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistryCustom {

    private static final Map<Key, RegistryCustom> REGISTRIES = new HashMap<>();

    public static final RegistryCustom CAT_VARIANT = register("cat_variant");
    public static final RegistryCustom CHAT_TYPE = register("chat_type");
    public static final RegistryCustom CHICKEN_VARIANT = register("chicken_variant");
    public static final RegistryCustom COW_VARIANT = register("cow_variant");
    public static final RegistryCustom DAMAGE_TYPE = register("damage_type");
    public static final RegistryCustom DIMENSION_TYPE = register("dimension_type");
    public static final RegistryCustom FROG_VARIANT = register("frog_variant");
    public static final RegistryCustom PAINTING_VARIANT = register("painting_variant");
    public static final RegistryCustom PIG_VARIANT = register("pig_variant");
    public static final RegistryCustom WOLF_SOUND_VARIANT = register("wolf_sound_variant");
    public static final RegistryCustom WOLF_VARIANT = register("wolf_variant");
    public static final RegistryCustom WORLDGEN_BIOME = register("worldgen/biome");

    private static RegistryCustom register(String identifier) {
        RegistryCustom registryCustom = new RegistryCustom(identifier);
        REGISTRIES.put(registryCustom.getIdentifier(), registryCustom);
        return registryCustom;
    }

    public static RegistryCustom getRegistry(Key identifier) {
        return REGISTRIES.get(identifier);
    }

    public static Collection<RegistryCustom> getRegistries() {
        return REGISTRIES.values();
    }

    private final Key identifier;
    private final Map<Key, CompoundTag> entries;

    private RegistryCustom(Key identifier, Map<Key, CompoundTag> entries) {
        this.identifier = identifier;
        this.entries = entries;
    }

    @SuppressWarnings("PatternValidation")
    public RegistryCustom(String identifier) {
        this(Key.key(identifier));
    }

    @SuppressWarnings("PatternValidation")
    public RegistryCustom(Key identifier) {
        this.identifier = identifier;
        Map<Key, CompoundTag> entries = new LinkedHashMap<>();
        String pathStart = "data/" + identifier.namespace() + "/" + identifier.value() + "/";
        Pattern pattern = Pattern.compile(Pattern.quote(pathStart) + ".*");
        for (String path : ClasspathResourcesUtils.getResources(pattern)) {
            if (path.endsWith(".json")) {
                try (InputStream inputStream = Limbo.class.getClassLoader().getResourceAsStream(path)) {
                    Key entryKey = Key.key(identifier.namespace(), path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf(".")));
                    JSONObject jsonObject = (JSONObject) new JSONParser().parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    CompoundTag value = CustomNBTUtils.getCompoundTagFromJson(jsonObject);
                    entries.put(entryKey, value);
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.entries = entries;
    }

    public Key getIdentifier() {
        return identifier;
    }

    public Map<Key, CompoundTag> getEntries() {
        return entries;
    }

    public int indexOf(Key key) {
        int i = 0;
        for (Key entryKey : entries.keySet()) {
            if (key.equals(entryKey)) {
                return i;
            }
            i++;
        }
        return -1;
    }

}

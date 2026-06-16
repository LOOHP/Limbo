/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2026. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2026. Contributors
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
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import net.querz.nbt.tag.CompoundTag;
import org.json.simple.JSONArray;
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

    public static final RegistryCustom BANNER_PATTERN = register("banner_pattern");
    public static final RegistryCustom CAT_SOUND_VARIANT = register("cat_sound_variant");
    public static final RegistryCustom CAT_VARIANT = register("cat_variant");
    public static final RegistryCustom CHAT_TYPE = register("chat_type");
    public static final RegistryCustom CHICKEN_SOUND_VARIANT = register("chicken_sound_variant");
    public static final RegistryCustom CHICKEN_VARIANT = register("chicken_variant");
    public static final RegistryCustom COW_SOUND_VARIANT = register("cow_sound_variant");
    public static final RegistryCustom COW_VARIANT = register("cow_variant");
    public static final RegistryCustom DAMAGE_TYPE = register("damage_type");
    public static final RegistryCustom DIMENSION_TYPE = register("dimension_type");
    public static final RegistryCustom FROG_VARIANT = register("frog_variant");
    public static final RegistryCustom INSTRUMENT = register("instrument");
    public static final RegistryCustom JUKEBOX_SONG = register("jukebox_song");
    public static final RegistryCustom PAINTING_VARIANT = register("painting_variant");
    public static final RegistryCustom PIG_SOUND_VARIANT = register("pig_sound_variant");
    public static final RegistryCustom PIG_VARIANT = register("pig_variant");
    public static final RegistryCustom TRIM_MATERIAL = register("trim_material");
    public static final RegistryCustom TRIM_PATTERN = register("trim_pattern");
    public static final RegistryCustom WOLF_SOUND_VARIANT = register("wolf_sound_variant");
    public static final RegistryCustom WOLF_VARIANT = register("wolf_variant");
    public static final RegistryCustom WORLDGEN_BIOME = register("worldgen/biome");
    public static final RegistryCustom ZOMBIE_NAUTILUS_VARIANT = register("zombie_nautilus_variant");

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
    private final Map<Key, List<Tag>> tags;

    private RegistryCustom(Key identifier, Map<Key, CompoundTag> entries, Map<Key, List<Tag>> tags) {
        this.identifier = identifier;
        this.entries = entries;
        this.tags = tags;
    }

    @SuppressWarnings("PatternValidation")
    public RegistryCustom(String identifier) {
        this(Key.key(identifier));
    }

    public RegistryCustom(Key identifier) {
        this.identifier = identifier;
        this.entries = loadEntries();
        this.tags = loadTags();
    }

    @SuppressWarnings("PatternValidation")
    private Map<Key, CompoundTag> loadEntries() {
        Map<Key, CompoundTag> entries = new LinkedHashMap<>();
        String pathStart = "data/" + identifier.namespace() + "/" + identifier.value() + "/";
        Pattern pattern = Pattern.compile(Pattern.quote(pathStart) + ".*");
        for (String path : ClasspathResourcesUtils.getResources(pattern)) {
            if (path.endsWith(".json")) {
                try (InputStream inputStream = Limbo.class.getClassLoader().getResourceAsStream(path)) {
                    Key entryKey = Key.key(identifier.namespace(), path.substring(path.indexOf(identifier.value()) + identifier.value().length() + 1, path.lastIndexOf(".")));
                    JSONObject jsonObject = (JSONObject) new JSONParser().parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    CompoundTag value = CustomNBTUtils.getCompoundTagFromJson(jsonObject);
                    entries.put(entryKey, value);
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return entries;
    }

    @SuppressWarnings("PatternValidation")
    private Map<Key, List<Tag>> loadTags() {
        Map<Key, List<Tag>> entries = new LinkedHashMap<>();
        String pathStart = "data/" + identifier.namespace() + "/tags/" + identifier.value() + "/";
        Pattern pattern = Pattern.compile(Pattern.quote(pathStart) + ".*");
        for (String path : ClasspathResourcesUtils.getResources(pattern)) {
            if (path.endsWith(".json")) {
                try (InputStream inputStream = Limbo.class.getClassLoader().getResourceAsStream(path)) {
                    Key entryKey = Key.key(identifier.namespace(), path.substring(pathStart.length(), path.lastIndexOf(".")));
                    JSONObject jsonObject = (JSONObject) new JSONParser().parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    JSONArray valuesArray = (JSONArray) jsonObject.get("values");
                    List<Tag> values = new ArrayList<>();
                    if (valuesArray != null) {
                        for (Object value : valuesArray) {
                            if (value instanceof String) {
                                if (((String) value).startsWith("#")) {
                                    values.add(new Tag(Key.key(((String) value).substring(1)), true));
                                } else {
                                    values.add(new Tag(Key.key((String) value), false));
                                }
                            }
                        }
                    }
                    entries.put(entryKey, values);
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return entries;
    }

    public Key getIdentifier() {
        return identifier;
    }

    public Map<Key, CompoundTag> getEntries() {
        return entries;
    }

    public Map<Key, List<Tag>> getTags() {
        return tags;
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

    public record Tag(Key key, boolean isReference) {}

}

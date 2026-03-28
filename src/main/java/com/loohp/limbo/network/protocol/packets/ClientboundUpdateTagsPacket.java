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

package com.loohp.limbo.network.protocol.packets;

import com.loohp.limbo.registry.PacketRegistry;
import com.loohp.limbo.registry.RegistryCustom;
import com.loohp.limbo.utils.DataTypeIO;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.key.Key;

public class ClientboundUpdateTagsPacket extends PacketOut {

    private final Collection<RegistryCustom> registries;

    public ClientboundUpdateTagsPacket(Collection<RegistryCustom> registries) {
        this.registries = registries;
    }

    public Collection<RegistryCustom> getRegistries() {
        return registries;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(PacketRegistry.getPacketId(getClass()));

        DataTypeIO.writeVarInt(output, registries.size());
        for (RegistryCustom registry : registries) {
            DataTypeIO.writeString(output, registry.getIdentifier().asString(), StandardCharsets.UTF_8);
            DataTypeIO.writeVarInt(output, registry.getTags().size());
            for (Map.Entry<Key, List<RegistryCustom.Tag>> entry : registry.getTags().entrySet()) {
                DataTypeIO.writeString(output, entry.getKey().asString(), StandardCharsets.UTF_8);
                List<Integer> tagIds = transformTags(registry, entry.getValue());
                DataTypeIO.writeVarInt(output, tagIds.size());
                for (int value : tagIds) {
                    DataTypeIO.writeVarInt(output, value);
                }
            }
        }

        return buffer.toByteArray();
    }

    private static List<Integer> transformTags(RegistryCustom registry, List<RegistryCustom.Tag> tags) {
        List<Integer> tagIds = new ArrayList<>();
        for (RegistryCustom.Tag value : tags) {
            if (value.isReference()) {
                List<RegistryCustom.Tag> referencedTags = registry.getTags().get(value.key());
                if (referencedTags != null) {
                    tagIds.addAll(transformTags(registry, referencedTags));
                }
            } else {
                int index = registry.indexOf(value.key());
                if (index >= 0) {
                    tagIds.add(index);
                }
            }
        }
        return tagIds;
    }
}

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

package com.loohp.limbo.network.protocol.packets;

import com.loohp.limbo.bossbar.KeyedBossBar;
import com.loohp.limbo.utils.DataTypeIO;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketPlayOutBoss extends PacketOut {

    public enum BossBarAction {
        ADD,
        REMOVE,
        UPDATE_PROGRESS,
        UPDATE_NAME,
        UPDATE_STYLE,
        UPDATE_PROPERTIES;
    }

    private static int encodeProperties(boolean darkenScreen, boolean playMusic, boolean createWorldFog) {
        int i = 0;
        if (darkenScreen) {
            i |= 1;
        }
        if (playMusic) {
            i |= 2;
        }
        if (createWorldFog) {
            i |= 4;
        }
        return i;
    }

    private KeyedBossBar bossBar;
    private BossBarAction action;

    public PacketPlayOutBoss(KeyedBossBar bossBar, BossBarAction action) {
        this.bossBar = bossBar;
        this.action = action;
    }

    public KeyedBossBar getBossBar() {
        return bossBar;
    }

    public BossBarAction getAction() {
        return action;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));

        DataTypeIO.writeUUID(output, bossBar.getUniqueId());
        DataTypeIO.writeVarInt(output, action.ordinal());

        BossBar properties = bossBar.getProperties();
        switch (action) {
            case ADD: {
                DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(properties.name()), StandardCharsets.UTF_8);
                output.writeFloat(properties.progress());
                DataTypeIO.writeVarInt(output, properties.color().ordinal());
                DataTypeIO.writeVarInt(output, properties.overlay().ordinal());
                output.writeByte(encodeProperties(properties.hasFlag(BossBar.Flag.DARKEN_SCREEN), properties.hasFlag(BossBar.Flag.PLAY_BOSS_MUSIC), properties.hasFlag(BossBar.Flag.CREATE_WORLD_FOG)));
                break;
            }
            case REMOVE: {
                break;
            }
            case UPDATE_PROGRESS: {
                output.writeFloat(properties.progress());
                break;
            }
            case UPDATE_NAME: {
                DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(properties.name()), StandardCharsets.UTF_8);
                break;
            }
            case UPDATE_STYLE: {
                DataTypeIO.writeVarInt(output, properties.color().ordinal());
                DataTypeIO.writeVarInt(output, properties.overlay().ordinal());
                break;
            }
            case UPDATE_PROPERTIES: {
                output.writeByte(encodeProperties(properties.hasFlag(BossBar.Flag.DARKEN_SCREEN), properties.hasFlag(BossBar.Flag.PLAY_BOSS_MUSIC), properties.hasFlag(BossBar.Flag.CREATE_WORLD_FOG)));
                break;
            }
        }

        return buffer.toByteArray();
    }
}

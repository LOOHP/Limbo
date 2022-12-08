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

import com.loohp.limbo.utils.DataTypeIO;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketPlayOutStopSound extends PacketOut {

    private Key sound;
    private Sound.Source source;

    public PacketPlayOutStopSound(Key sound, Sound.Source source) {
        this.sound = sound;
        this.source = source;
    }

    public Key getSound() {
        return sound;
    }

    public Sound.Source getSource() {
        return source;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));

        if (source != null) {
            if (sound != null) {
                output.writeByte(3);
                DataTypeIO.writeVarInt(output, source.ordinal());
                DataTypeIO.writeString(output, sound.toString(), StandardCharsets.UTF_8);
            } else {
                output.writeByte(1);
                DataTypeIO.writeVarInt(output, source.ordinal());
            }
        } else if (sound != null) {
            output.writeByte(2);
            DataTypeIO.writeString(output, sound.toString(), StandardCharsets.UTF_8);
        } else {
            output.writeByte(0);
        }

        return buffer.toByteArray();
    }
}

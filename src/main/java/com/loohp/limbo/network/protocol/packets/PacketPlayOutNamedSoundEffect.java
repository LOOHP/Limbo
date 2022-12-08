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

import com.loohp.limbo.sounds.SoundEffect;
import com.loohp.limbo.utils.DataTypeIO;
import net.kyori.adventure.sound.Sound;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class PacketPlayOutNamedSoundEffect extends PacketOut {

    private SoundEffect sound;
    private Sound.Source source;
    private int x;
    private int y;
    private int z;
    private float volume;
    private float pitch;
    private long seed;

    public PacketPlayOutNamedSoundEffect(SoundEffect sound, Sound.Source source, double x, double y, double z, float volume, float pitch, long seed) {
        this.sound = sound;
        this.source = source;
        this.x = (int) (x * 8.0);
        this.y = (int) (y * 8.0);
        this.z = (int) (z * 8.0);
        this.volume = volume;
        this.pitch = pitch;
        this.seed = seed;
    }

    public SoundEffect getSound() {
        return sound;
    }

    public Sound.Source getSource() {
        return source;
    }

    public double getX() {
        return (float) this.x / 8.0F;
    }

    public double getY() {
        return (float) this.y / 8.0F;
    }

    public double getZ() {
        return (float) this.z / 8.0F;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public long getSeed() {
        return seed;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));

        DataTypeIO.writeVarInt(output, 0);
        DataTypeIO.writeString(output, sound.getSound().toString(), StandardCharsets.UTF_8);
        Optional<Float> fixedRange = sound.fixedRange();
        if (fixedRange.isPresent()) {
            output.writeBoolean(true);
            output.writeFloat(fixedRange.get());
        } else {
            output.writeBoolean(false);
        }
        DataTypeIO.writeVarInt(output, source.ordinal());
        output.writeInt(x);
        output.writeInt(y);
        output.writeInt(z);
        output.writeFloat(volume);
        output.writeFloat(pitch);
        output.writeLong(seed);

        return buffer.toByteArray();
    }
}

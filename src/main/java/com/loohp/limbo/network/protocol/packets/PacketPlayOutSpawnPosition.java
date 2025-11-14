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

import com.loohp.limbo.location.GlobalPos;
import com.loohp.limbo.registry.PacketRegistry;
import com.loohp.limbo.utils.DataTypeIO;
import net.kyori.adventure.key.Key;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketPlayOutSpawnPosition extends PacketOut {
	
	private final GlobalPos position;
	private final float yaw;
    private final float pitch;
	
	public PacketPlayOutSpawnPosition(GlobalPos position, float yaw, float pitch) {
		this.position = position;
		this.yaw = yaw;
        this.pitch = pitch;
	}

    public GlobalPos getPosition() {
        return position;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(PacketRegistry.getPacketId(getClass()));
        DataTypeIO.writeString(output, Key.key(position.getWorld().getName()).toString(), StandardCharsets.UTF_8);
		DataTypeIO.writeBlockPosition(output, position.getPos());
		output.writeFloat(yaw);
        output.writeFloat(pitch);
		
		return buffer.toByteArray();
	}

}

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

import com.loohp.limbo.entity.EntityType;
import com.loohp.limbo.location.Vector;
import com.loohp.limbo.registry.PacketRegistry;
import com.loohp.limbo.utils.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PacketPlayOutSpawnEntity extends PacketOut {
	
	private final int entityId;
    private final UUID uuid;
    private final EntityType type;
    private final double x;
    private final double y;
    private final double z;
    private final float pitch;
    private final float yaw;
	private final float headYaw;
    private final int data;
    private final Vector movement;
	
	public PacketPlayOutSpawnEntity(int entityId, UUID uuid, EntityType type, double x, double y, double z, float pitch, float yaw, float headYaw, int data, Vector movement) {
		this.entityId = entityId;
		this.uuid = uuid;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.headYaw = headYaw;
		this.data = data;
		this.movement = movement.clone();
	}

	public int getEntityId() {
		return entityId;
	}

	public UUID getUuid() {
		return uuid;
	}

	public EntityType getType() {
		return type;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getHeadYaw() {
		return headYaw;
	}

	public int getData() {
		return data;
	}

    public Vector getMovement() {
        return movement.clone();
    }

    @Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(PacketRegistry.getPacketId(getClass()));
		DataTypeIO.writeVarInt(output, entityId);
		DataTypeIO.writeUUID(output, uuid);
		DataTypeIO.writeVarInt(output, type.getTypeId());
		output.writeDouble(x);
		output.writeDouble(y);
		output.writeDouble(z);
        DataTypeIO.writeLpVec3(output, movement);
		output.writeByte((byte) (int) (pitch * 256.0F / 360.0F));
		output.writeByte((byte) (int) (yaw * 256.0F / 360.0F));
		output.writeByte((byte) (int) (headYaw * 256.0F / 360.0F));
		DataTypeIO.writeVarInt(output, data);
		
		return buffer.toByteArray();
	}

}

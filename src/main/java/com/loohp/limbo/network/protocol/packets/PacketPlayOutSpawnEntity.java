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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.loohp.limbo.entity.EntityType;
import com.loohp.limbo.utils.DataTypeIO;

public class PacketPlayOutSpawnEntity extends PacketOut {
	
	private int entityId;
    private UUID uuid;
    private EntityType type;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
	private float headYaw;
    private int data;
    private short velocityX;
    private short velocityY;
    private short velocityZ;
	
	public PacketPlayOutSpawnEntity(int entityId, UUID uuid, EntityType type, double x, double y, double z, float pitch, float yaw, float headYaw, int data, short velocityX, short velocityY, short velocityZ) {
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
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
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

	public short getVelocityX() {
		return velocityX;
	}

	public short getVelocityY() {
		return velocityY;
	}

	public short getVelocityZ() {
		return velocityZ;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeVarInt(output, entityId);
		DataTypeIO.writeUUID(output, uuid);
		DataTypeIO.writeVarInt(output, type.getTypeId());
		output.writeDouble(x);
		output.writeDouble(y);
		output.writeDouble(z);
		output.writeByte((byte) (int) (pitch * 256.0F / 360.0F));
		output.writeByte((byte) (int) (yaw * 256.0F / 360.0F));
		output.writeByte((byte) (int) (headYaw * 256.0F / 360.0F));
		DataTypeIO.writeVarInt(output, data);
		output.writeShort(velocityX * 8000);
		output.writeShort(velocityY * 8000);
		output.writeShort(velocityZ * 8000);
		
		return buffer.toByteArray();
	}

}

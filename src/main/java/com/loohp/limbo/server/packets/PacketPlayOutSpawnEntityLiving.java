package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.loohp.limbo.entity.EntityType;
import com.loohp.limbo.utils.DataTypeIO;

public class PacketPlayOutSpawnEntityLiving extends PacketOut {
	
	private int entityId;
    private UUID uuid;
    private EntityType type;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private float headPitch;
    private short velocityX;
    private short velocityY;
    private short velocityZ;
	
	public PacketPlayOutSpawnEntityLiving(int entityId, UUID uuid, EntityType type, double x, double y, double z, float yaw, float pitch, float headPitch, short velocityX, short velocityY, short velocityZ) {
		this.entityId = entityId;
		this.uuid = uuid;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.headPitch = headPitch;
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
	
	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public float getHeadPitch() {
		return headPitch;
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
		output.writeByte((byte) (int) (yaw * 256.0F / 360.0F));
		output.writeByte((byte) (int) (pitch * 256.0F / 360.0F));
		output.writeByte((byte) (int) (headPitch * 256.0F / 360.0F));
		output.writeShort((int) (velocityX * 8000));
		output.writeShort((int) (velocityY * 8000));
		output.writeShort((int) (velocityZ * 8000));
		
		return buffer.toByteArray();
	}

}

package com.loohp.limbo.server.packets;

import com.loohp.limbo.entity.EntityType;
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
    private final int data;
    private final short velocityX;
    private final short velocityY;
    private final short velocityZ;

    public PacketPlayOutSpawnEntity(int entityId, UUID uuid, EntityType type, double x, double y, double z, float pitch, float yaw, short velocityX, short velocityY, short velocityZ) {
        this.entityId = entityId;
        this.uuid = uuid;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.data = 0; //TO-DO
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
        output.writeInt(data);
        output.writeShort(velocityX * 8000);
        output.writeShort(velocityY * 8000);
        output.writeShort(velocityZ * 8000);

        return buffer.toByteArray();
    }

}

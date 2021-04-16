package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutEntityDestroy extends PacketOut {

    private final int[] entityIds;

    public PacketPlayOutEntityDestroy(int... entityIds) {
        this.entityIds = entityIds;
    }

    public int[] getEntityIds() {
        return entityIds;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));
        DataTypeIO.writeVarInt(output, entityIds.length);
        for (int id : entityIds) {
            DataTypeIO.writeVarInt(output, id);
        }

        return buffer.toByteArray();
    }

}

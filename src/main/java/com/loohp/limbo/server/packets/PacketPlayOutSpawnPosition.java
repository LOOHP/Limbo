package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.world.BlockPosition;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutSpawnPosition extends PacketOut {

    private final BlockPosition position;

    public PacketPlayOutSpawnPosition(BlockPosition position) {
        this.position = position;
    }

    public BlockPosition getPosition() {
        return position;
    }

    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));
        DataTypeIO.writeBlockPosition(output, position);

        return buffer.toByteArray();
    }

}

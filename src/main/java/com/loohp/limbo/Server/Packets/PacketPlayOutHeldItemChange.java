package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutHeldItemChange extends PacketOut {

    private final byte slot;

    public PacketPlayOutHeldItemChange(byte slot) {
        this.slot = slot;
    }

    public byte getSlot() {
        return slot;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));
        output.writeByte(slot);

        return buffer.toByteArray();
    }
}

package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutGameState extends PacketOut {

    private final int reason;
    private final float value;

    public PacketPlayOutGameState(int reason, float value) {
        this.reason = reason;
        this.value = value;
    }

    public int getReason() {
        return reason;
    }

    public float getValue() {
        return value;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));
        output.writeByte(reason);
        output.writeFloat(value);

        return buffer.toByteArray();
    }

}

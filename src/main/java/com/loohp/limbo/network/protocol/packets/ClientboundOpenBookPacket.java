package com.loohp.limbo.network.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientboundOpenBookPacket extends PacketOut {

    private final int hand; // 0 = main hand, 1 = off hand

    public ClientboundOpenBookPacket(int hand) {
        this.hand = hand;
    }

    public int getHand() {
        return hand;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));
        output.writeByte(hand);

        return buffer.toByteArray();
    }
}

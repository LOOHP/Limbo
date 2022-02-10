package com.loohp.limbo.network;

import com.loohp.limbo.network.protocol.packets.PacketOut;

public final class ChannelPacketWrite {

    private PacketOut packet;

    ChannelPacketWrite(PacketOut packet) {
        this.packet = packet;
    }

    public PacketOut getPacket() {
        return packet;
    }

    public void setPacket(PacketOut packet) {
        this.packet = packet;
    }

}

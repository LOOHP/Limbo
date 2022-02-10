package com.loohp.limbo.network;

import com.loohp.limbo.network.protocol.packets.PacketIn;

public final class ChannelPacketRead {

    private int packetId;
    private PacketIn packet;

    protected ChannelPacketRead(int packetId, PacketIn packet) {
        this.packetId = packetId;
        this.packet = packet;
    }

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public PacketIn getPacket() {
        return packet;
    }

    public void setPacket(PacketIn packet) {
        this.packet = packet;
    }

}

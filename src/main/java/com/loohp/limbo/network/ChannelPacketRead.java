package com.loohp.limbo.network;

import com.loohp.limbo.network.protocol.packets.PacketIn;

import java.io.DataInput;

public final class ChannelPacketRead {

    private int size;
    private int packetId;
    private DataInput input;
    private PacketIn packet;

    ChannelPacketRead(int size, int packetId, DataInput input) {
        this.size = size;
        this.packetId = packetId;
        this.input = input;
        this.packet = null;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public boolean hasReadPacket() {
        return packet != null;
    }

    public PacketIn getReadPacket() {
        return packet;
    }

    public void setPacket(PacketIn packet) {
        this.packet = packet;
    }

    public DataInput getDataInput() {
        return input;
    }

}

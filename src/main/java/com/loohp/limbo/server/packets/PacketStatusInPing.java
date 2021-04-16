package com.loohp.limbo.server.packets;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketStatusInPing extends PacketIn {

    private final long payload;

    public PacketStatusInPing(long payload) {
        this.payload = payload;
    }

    public PacketStatusInPing(DataInputStream in) throws IOException {
        this(in.readLong());
    }

    public long getPayload() {
        return payload;
    }

}

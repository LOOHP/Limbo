package com.loohp.limbo.server.packets;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInKeepAlive extends PacketIn {

    private final long payload;

    public PacketPlayInKeepAlive(long payload) {
        this.payload = payload;
    }

    public PacketPlayInKeepAlive(DataInputStream in) throws IOException {
        this(in.readLong());
    }

    public long getPayload() {
        return payload;
    }

}

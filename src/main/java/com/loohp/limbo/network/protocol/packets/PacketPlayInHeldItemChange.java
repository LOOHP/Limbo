package com.loohp.limbo.network.protocol.packets;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInHeldItemChange extends PacketIn {

    private short slot;

    public PacketPlayInHeldItemChange(short slot) {
        this.slot = slot;
    }

    public PacketPlayInHeldItemChange(DataInputStream in) throws IOException {
        this(in.readShort());
    }

    public short getSlot() {
        return slot;
    }
}

package com.loohp.limbo.server.packets;

import java.io.IOException;

public abstract class PacketOut extends Packet {

    public abstract byte[] serializePacket() throws IOException;

}

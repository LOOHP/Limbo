package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.NamespacedKey;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketPlayInPluginMessaging extends PacketIn {

    private final NamespacedKey channel;
    private final byte[] data;

    public PacketPlayInPluginMessaging(NamespacedKey channel, byte[] data) {
        this.channel = channel;
        this.data = data;
    }

    public PacketPlayInPluginMessaging(DataInputStream in, int packetLength, int packetId) throws IOException {
        String rawChannel = DataTypeIO.readString(in, StandardCharsets.UTF_8);
        channel = new NamespacedKey(rawChannel);
        int dataLength = packetLength - DataTypeIO.getVarIntLength(packetId) - DataTypeIO.getStringLength(rawChannel, StandardCharsets.UTF_8);
        data = new byte[dataLength];
        in.read(data);
    }

    public NamespacedKey getChannel() {
        return channel;
    }

    public byte[] getData() {
        return data;
    }

}

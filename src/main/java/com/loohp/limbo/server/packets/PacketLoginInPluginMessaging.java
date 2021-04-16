package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.NamespacedKey;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketLoginInPluginMessaging extends PacketIn {

    private final int messageId;
    private final NamespacedKey channel;
    private final byte[] data;

    public PacketLoginInPluginMessaging(int messageId, NamespacedKey channel, byte[] data) {
        this.messageId = messageId;
        this.channel = channel;
        this.data = data;
    }

    public PacketLoginInPluginMessaging(DataInputStream in, int packetLength, int packetId) throws IOException {
        messageId = DataTypeIO.readVarInt(in);
        String rawChannel = DataTypeIO.readString(in, StandardCharsets.UTF_8);
        channel = new NamespacedKey(rawChannel);
        int dataLength = packetLength - DataTypeIO.getVarIntLength(packetId) - DataTypeIO.getVarIntLength(messageId) - DataTypeIO.getStringLength(rawChannel, StandardCharsets.UTF_8);
        data = new byte[dataLength];
        in.read(data);
    }

    public int getMessageId() {
        return messageId;
    }

    public NamespacedKey getChannel() {
        return channel;
    }

    public byte[] getData() {
        return data;
    }

}

package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PacketPlayOutChat extends PacketOut {

    private final String json;
    private final int position;
    private final UUID sender;

    public PacketPlayOutChat(String json, int position, UUID sender) {
        this.json = json;
        this.position = position;
        this.sender = sender;
    }

    public String getJson() {
        return json;
    }

    public int getPosition() {
        return position;
    }

    public UUID getSender() {
        return sender;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));
        DataTypeIO.writeString(output, json, StandardCharsets.UTF_8);
        output.writeByte(position);
        DataTypeIO.writeUUID(output, sender);

        return buffer.toByteArray();
    }

}

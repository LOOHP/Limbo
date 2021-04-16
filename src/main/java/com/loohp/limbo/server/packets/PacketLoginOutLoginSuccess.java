package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PacketLoginOutLoginSuccess extends PacketOut {

    private final UUID uuid;
    private final String username;

    public PacketLoginOutLoginSuccess(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getLoginOut().get(getClass()));
        DataTypeIO.writeUUID(output, uuid);
        DataTypeIO.writeString(output, username, StandardCharsets.UTF_8);

        return buffer.toByteArray();
    }

}

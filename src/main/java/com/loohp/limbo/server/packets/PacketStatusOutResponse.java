package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketStatusOutResponse extends PacketOut {

    private final String json;

    public PacketStatusOutResponse(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getStatusOut().get(getClass()));
        DataTypeIO.writeString(output, json, StandardCharsets.UTF_8);

        return buffer.toByteArray();
    }

}

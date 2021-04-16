package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketPlayInChat extends PacketIn {

    private final String message;

    public PacketPlayInChat(String message) {
        this.message = message;
    }

    public PacketPlayInChat(DataInputStream in) throws IOException {
        this(DataTypeIO.readString(in, StandardCharsets.UTF_8));
    }

    public String getMessage() {
        return message;
    }

}

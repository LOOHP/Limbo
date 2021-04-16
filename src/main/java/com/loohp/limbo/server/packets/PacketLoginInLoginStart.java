package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketLoginInLoginStart extends PacketIn {

    private final String username;

    public PacketLoginInLoginStart(String username) {
        this.username = username;
    }

    public PacketLoginInLoginStart(DataInputStream in) throws IOException {
        this(DataTypeIO.readString(in, StandardCharsets.UTF_8));
    }

    public String getUsername() {
        return username;
    }

}

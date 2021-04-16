package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketPlayInTabComplete extends PacketIn {

    private final int id;
    private final String text;

    public PacketPlayInTabComplete(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public PacketPlayInTabComplete(DataInputStream in) throws IOException {
        this(DataTypeIO.readVarInt(in), DataTypeIO.readString(in, StandardCharsets.UTF_8));
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

}

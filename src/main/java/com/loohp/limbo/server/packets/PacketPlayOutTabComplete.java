package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class PacketPlayOutTabComplete extends PacketOut {

    private final int id;
    private final int start;
    private final int length;
    private final TabCompleteMatches[] matches;

    public PacketPlayOutTabComplete(int id, int start, int length, TabCompleteMatches... matches) {
        this.id = id;
        this.start = start;
        this.length = length;
        this.matches = matches;
    }

    public int getId() {
        return id;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public TabCompleteMatches[] getMatches() {
        return matches;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));
        DataTypeIO.writeVarInt(output, id);
        DataTypeIO.writeVarInt(output, start);
        DataTypeIO.writeVarInt(output, length);
        DataTypeIO.writeVarInt(output, matches.length);

        for (TabCompleteMatches match : matches) {
            DataTypeIO.writeString(output, match.getMatch(), StandardCharsets.UTF_8);
            if (match.getTooltip().isPresent()) {
                output.writeBoolean(true);
                DataTypeIO.writeString(output, ComponentSerializer.toString(match.getTooltip().get()), StandardCharsets.UTF_8);
            } else {
                output.writeBoolean(false);
            }
        }

        return buffer.toByteArray();
    }

    public static class TabCompleteMatches {

        private final String match;
        private final Optional<BaseComponent[]> tooltip;

        public TabCompleteMatches(String match, BaseComponent... tooltip) {
            this.match = match;
            this.tooltip = tooltip.length > 0 ? Optional.of(tooltip) : Optional.empty();
        }

        public String getMatch() {
            return match;
        }

        public Optional<BaseComponent[]> getTooltip() {
            return tooltip;
        }

    }

}

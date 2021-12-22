package com.loohp.limbo.network.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.loohp.limbo.utils.DataTypeIO;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class PacketPlayOutTabComplete extends PacketOut {

	private int id;
	private int start;
	private int length;
	private TabCompleteMatches[] matches;

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
				DataTypeIO.writeString(output, GsonComponentSerializer.gson().serialize(match.getTooltip().get()), StandardCharsets.UTF_8);
			} else {
				output.writeBoolean(false);
			}
		}
		
		return buffer.toByteArray();
	}

	public static class TabCompleteMatches {

		private String match;
		private Optional<Component> tooltip;
		
		public TabCompleteMatches(String match) {
			this.match = match;
			this.tooltip = Optional.empty();
		}

		public TabCompleteMatches(String match, Component tooltip) {
			this.match = match;
			this.tooltip = Optional.ofNullable(tooltip);
		}

		public String getMatch() {
			return match;
		}

		public Optional<Component> getTooltip() {
			return tooltip;
		}

	}

}

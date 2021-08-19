package com.loohp.limbo.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.server.packets.PacketPlayOutDeclareCommands;

public class DeclareCommands {
	
	public static PacketPlayOutDeclareCommands getDeclareCommandsPacket(CommandSender sender) throws IOException {
		List<String> commands = Limbo.getInstance().getPluginManager().getTabOptions(sender, new String[0]);
		
		if (commands.isEmpty()) {
			return null;
		}
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();		
		DataOutputStream output = new DataOutputStream(buffer);

		DataTypeIO.writeVarInt(output, commands.size() * 2 + 1);
		
		output.writeByte(0);
		DataTypeIO.writeVarInt(output, commands.size());
		for (int i = 1; i <= commands.size() * 2; i++) {
			DataTypeIO.writeVarInt(output, i++);
		}
		
		int i = 1;
		for (String label : commands) {
			output.writeByte(1 | 0x04);
			DataTypeIO.writeVarInt(output, 1);
			DataTypeIO.writeVarInt(output, i + 1);
			DataTypeIO.writeString(output, label, StandardCharsets.UTF_8);
			i++;
			
			output.writeByte(2 | 0x04 | 0x10);
			DataTypeIO.writeVarInt(output, 1);
			DataTypeIO.writeVarInt(output, i);
			DataTypeIO.writeString(output, "arg", StandardCharsets.UTF_8);
			DataTypeIO.writeString(output, "brigadier:string", StandardCharsets.UTF_8);
			DataTypeIO.writeVarInt(output, 0);
			DataTypeIO.writeString(output, "minecraft:ask_server", StandardCharsets.UTF_8);
			i++;
		}
		
		DataTypeIO.writeVarInt(output, 0);
		
		return new PacketPlayOutDeclareCommands(buffer.toByteArray());
	}

}

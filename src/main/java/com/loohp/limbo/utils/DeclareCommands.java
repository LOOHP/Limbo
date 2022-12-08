/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loohp.limbo.utils;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutDeclareCommands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
			DataTypeIO.writeVarInt(output, 0);
			DataTypeIO.writeString(output, "arg", StandardCharsets.UTF_8);
			DataTypeIO.writeVarInt(output, 5); //brigadier:string
			DataTypeIO.writeVarInt(output, 2);
			DataTypeIO.writeString(output, "minecraft:ask_server", StandardCharsets.UTF_8);
			i++;
		}
		
		DataTypeIO.writeVarInt(output, 0);
		
		return new PacketPlayOutDeclareCommands(buffer.toByteArray());
	}

}

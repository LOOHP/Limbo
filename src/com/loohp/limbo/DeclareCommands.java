package com.loohp.limbo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.Server.Packets.PacketPlayOutDeclareCommands;
import com.loohp.limbo.Utils.DataTypeIO;

public class DeclareCommands {
	
	public static PacketPlayOutDeclareCommands getDeclareCommandPacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();		
		DataOutputStream output = new DataOutputStream(buffer);

		DataTypeIO.writeVarInt(output, 2);
		
		output.writeByte(0);
		DataTypeIO.writeVarInt(output, 1);
		DataTypeIO.writeVarInt(output, 1);
		
		output.writeByte(1 | 0x04);
		DataTypeIO.writeVarInt(output, 0);
		DataTypeIO.writeString(output, "spawn", StandardCharsets.UTF_8);
		
		DataTypeIO.writeVarInt(output, 0);
		
		return new PacketPlayOutDeclareCommands(buffer.toByteArray());
	}

}

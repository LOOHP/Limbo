package com.loohp.limbo.Server.Packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.Utils.DataTypeIO;

public class PacketPlayInChat extends PacketIn {
	
	private String message;
	
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

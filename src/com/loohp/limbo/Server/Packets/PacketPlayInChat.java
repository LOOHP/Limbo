package com.loohp.limbo.Server.Packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.loohp.limbo.Utils.DataTypeIO;

public class PacketPlayInChat extends Packet {
	
	private String message;
	
	public PacketPlayInChat(String message) {
		this.message = message;
	}
	
	public PacketPlayInChat(DataInputStream in) throws IOException {
		this(DataTypeIO.readString(in));
	}

	public String getMessage() {
		return message;
	}

}

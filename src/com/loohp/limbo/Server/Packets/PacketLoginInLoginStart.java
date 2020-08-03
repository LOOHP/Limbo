package com.loohp.limbo.Server.Packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.loohp.limbo.Utils.DataTypeIO;

public class PacketLoginInLoginStart extends Packet {
	
	private String username;
	
	public PacketLoginInLoginStart(String username) {
		this.username = username;
	}
	
	public PacketLoginInLoginStart(DataInputStream in) throws IOException {
		this(DataTypeIO.readString(in));
	}

	public String getUsername() {
		return username;
	}

}

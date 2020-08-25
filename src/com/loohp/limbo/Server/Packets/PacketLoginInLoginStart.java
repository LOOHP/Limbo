package com.loohp.limbo.Server.Packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.Utils.DataTypeIO;

public class PacketLoginInLoginStart extends PacketIn {
	
	private String username;
	
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

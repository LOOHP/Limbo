package com.loohp.limbo.Server.Packets;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInKeepAlive extends PacketIn {
	
	long payload;
	
	public PacketPlayInKeepAlive(long payload) {
		this.payload = payload;
	}
	
	public PacketPlayInKeepAlive(DataInputStream in) throws IOException {
		this(in.readLong());
	}
		
	public long getPayload() {
		return payload;
	}

}

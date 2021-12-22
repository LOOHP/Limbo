package com.loohp.limbo.network.protocol.packets;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInKeepAlive extends PacketIn {
	
	private long payload;
	
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

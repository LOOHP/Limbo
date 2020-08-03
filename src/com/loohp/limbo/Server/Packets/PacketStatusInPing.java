package com.loohp.limbo.Server.Packets;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketStatusInPing extends Packet {
	
	private long payload;
	
	public PacketStatusInPing(long payload) {
		this.payload = payload;
	}
	
	public PacketStatusInPing(DataInputStream in) throws IOException {
		this(in.readLong());
	}

	public long getPayload() {
		return payload;
	}
	
}

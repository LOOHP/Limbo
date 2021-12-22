package com.loohp.limbo.network.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutGameState extends PacketOut {

	private int reason;
	private float value;

	public PacketPlayOutGameState(int reason, float value) {
		this.reason = reason;
		this.value = value;
	}

	public int getReason() {
		return reason;
	}

	public float getValue() {
		return value;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		output.writeByte(reason);
		output.writeFloat(value);
		
		return buffer.toByteArray();
	}

}

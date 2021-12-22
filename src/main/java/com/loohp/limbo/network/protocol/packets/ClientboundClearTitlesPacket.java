package com.loohp.limbo.network.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientboundClearTitlesPacket extends PacketOut {
	
	private boolean reset;

	public ClientboundClearTitlesPacket(boolean reset) {
		this.reset = reset;
	}

	public boolean isReset() {
		return reset;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		output.writeBoolean(reset);
		
		return buffer.toByteArray();
	}

}

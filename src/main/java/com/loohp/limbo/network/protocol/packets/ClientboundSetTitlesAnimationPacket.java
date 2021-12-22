package com.loohp.limbo.network.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientboundSetTitlesAnimationPacket extends PacketOut {

	private int fadeIn;
	private int stay;
	private int fadeOut;

	public ClientboundSetTitlesAnimationPacket(int fadeIn, int stay, int fadeOut) {
		this.fadeIn = fadeIn;
		this.stay = stay;
		this.fadeOut = fadeOut;
	}

	public int getFadeIn() {
		return fadeIn;
	}

	public int getStay() {
		return stay;
	}

	public int getFadeOut() {
		return fadeOut;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		
		output.writeInt(fadeIn);
		output.writeInt(stay);
		output.writeInt(fadeOut);
		
		return buffer.toByteArray();
	}

}

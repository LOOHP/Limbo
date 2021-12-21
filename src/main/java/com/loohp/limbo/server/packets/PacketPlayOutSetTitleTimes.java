package com.loohp.limbo.server.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutSetTitleTimes extends PacketOut {

	private Integer fadeIn;
	private Integer stay;
	private Integer fadeOut;

	public PacketPlayOutSetTitleTimes(Integer fadeIn, Integer stay, Integer fadeOut) {
		this.fadeIn = fadeIn;
		this.stay = stay;
		this.fadeOut = fadeOut;
	}

	public Integer getFadeIn() {
		return fadeIn;
	}

	public Integer getStay() {
		return stay;
	}

	public Integer getFadeOut() {
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

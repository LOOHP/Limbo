package com.loohp.limbo.network.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.world.BlockPosition;

public class PacketPlayOutSpawnPosition extends PacketOut {
	
	private BlockPosition position;
	private float angle;
	
	public PacketPlayOutSpawnPosition(BlockPosition position, float angle) {
		this.position = position;
		this.angle = angle;
	}

	public BlockPosition getPosition() {
		return position;
	}
	
	public float getAngle() {
		return angle;
	}
	
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeBlockPosition(output, position);
		output.writeFloat(angle);
		
		return buffer.toByteArray();
	}

}

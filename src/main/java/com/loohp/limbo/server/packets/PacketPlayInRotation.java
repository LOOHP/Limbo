package com.loohp.limbo.server.packets;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInRotation extends PacketIn {
	
    private float yaw;
    private float pitch;
    private boolean onGround;
	
	public PacketPlayInRotation(float yaw, float pitch, boolean onGround) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
	}
	
	public PacketPlayInRotation(DataInputStream in) throws IOException {
		this(in.readFloat(), in.readFloat(), in.readBoolean());
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public boolean onGround() {
		return onGround;
	}

}

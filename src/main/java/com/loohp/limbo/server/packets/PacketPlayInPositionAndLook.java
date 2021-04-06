package com.loohp.limbo.server.packets;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInPositionAndLook extends PacketIn {
	
	private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;
	
	public PacketPlayInPositionAndLook(double x, double y, double z, float yaw, float pitch, boolean onGround) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
	}
	
	public PacketPlayInPositionAndLook(DataInputStream in) throws IOException {
		this(in.readDouble(), in.readDouble(), in.readDouble(), in.readFloat(), in.readFloat(), in.readBoolean());
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
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

package com.loohp.limbo.Server.Packets;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInPosition extends Packet {
	
	private double x;
    private double y;
    private double z;
    private boolean onGround;
	
	public PacketPlayInPosition(double x, double y, double z, boolean onGround) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.onGround = onGround;
	}
	
	public PacketPlayInPosition(DataInputStream in) throws IOException {
		this(in.readDouble(), in.readDouble(), in.readDouble(), in.readBoolean());
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

	public boolean onGround() {
		return onGround;
	}

}

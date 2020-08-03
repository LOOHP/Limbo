package com.loohp.limbo.Location;

import com.loohp.limbo.World.World;

public class Location {
	
	World world;
	double x;
	double y;
	double z;
	float yaw;
	float pitch;
	
	public Location(World world, double x, double y, double z, float yaw, float pitch) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public Location(World world, double x, double y, double z) {
		this(world, x, y, z, 0, 0);
	}
	
	@Override
	public Location clone() {
		return new Location(this.world, this.x, this.y, this.z, this.yaw, this.pitch);
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}		

}

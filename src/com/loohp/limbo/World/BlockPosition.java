package com.loohp.limbo.World;

import com.loohp.limbo.Location.Location;

public class BlockPosition {
	private int x;
	private int y;
	private int z;

	public BlockPosition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}
	
	public static BlockPosition from(Location location) {
		return new BlockPosition((int) Math.floor(location.getX()), (int) Math.floor(location.getY()), (int) Math.floor(location.getZ()));
	}
}

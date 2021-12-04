package com.loohp.limbo.world;

import com.loohp.limbo.location.Location;

import net.querz.mca.Chunk;

public class ChunkPosition {
	
	private World world;
	private int x;
	private int z;
	
	public ChunkPosition(World world, int chunkX, int chunkZ) {
		this.world = world;
		this.x = chunkX;
		this.z = chunkZ;
	}
	
	public ChunkPosition(Location location) {
		this(location.getWorld(), (int) location.getX() >> 4, (int) location.getZ() >> 4);
	}
	
	public ChunkPosition(World world, Chunk chunk) {
		this(world, world.getChunkX(chunk), world.getChunkZ(chunk));
	}
	
	public World getWorld() {
		return world;
	}
	
	public int getChunkX() {
		return x;
	}
	
	public int getChunkZ() {
		return z;
	}
	
	public Chunk getChunk() {
		return getWorld().getChunkAt(x, z);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		result = prime * result + x;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChunkPosition other = (ChunkPosition) obj;
		if (world == null) {
			if (other.world != null) {
				return false;
			}
		} else if (!world.equals(other.world)) {
			return false;
		}
		if (x != other.x) {
			return false;
		}
		if (z != other.z) {
			return false;
		}
		return true;
	}

}

package com.loohp.limbo.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.server.packets.PacketPlayOutEntityDestroy;
import com.loohp.limbo.server.packets.PacketPlayOutEntityMetadata;
import com.loohp.limbo.server.packets.PacketPlayOutLightUpdate;
import com.loohp.limbo.server.packets.PacketPlayOutMapChunk;
import com.loohp.limbo.server.packets.PacketPlayOutSpawnEntity;
import com.loohp.limbo.server.packets.PacketPlayOutSpawnEntityLiving;
import com.loohp.limbo.server.packets.PacketPlayOutUnloadChunk;
import com.loohp.limbo.entity.Entity;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.world.World;

import net.querz.mca.Chunk;

public class PlayerInteractManager {
	
	private Player player;
	
	private Set<Entity> entities;
	private Map<Chunk, World> chunks;
	
	public PlayerInteractManager() {
		this.player = null;
		this.entities = new HashSet<>();
		this.chunks = new HashMap<>();
	}
	
	protected void setPlayer(Player player) {
		if (this.player == null) {
			this.player = player;
		} else {
			throw new RuntimeException("Player in PlayerInteractManager cannot be changed once created");
		}
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void update() throws IOException {
		int viewDistanceChunks = Limbo.getInstance().getServerProperties().getViewDistance();
		int viewDistanceBlocks = viewDistanceChunks << 4;
		Location location = player.getLocation();
		Set<Entity> entitiesInRange = player.getWorld().getEntities().stream().filter(each -> each.getLocation().distanceSquared(location) < viewDistanceBlocks * viewDistanceBlocks).collect(Collectors.toSet());
		for (Entity entity : entitiesInRange) {
			if (!entities.contains(entity)) {
				if (entity.getType().isAlive()) {
					PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity.getEntityId(), entity.getUniqueId(), entity.getType(), entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch(), entity.getPitch(), (short) 0, (short) 0, (short) 0);
					player.clientConnection.sendPacket(packet);
					
					PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(entity);
					player.clientConnection.sendPacket(meta);
				} else {
					PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entity.getEntityId(), entity.getUniqueId(), entity.getType(), entity.getX(), entity.getY(), entity.getZ(), entity.getPitch(), entity.getYaw(), (short) 0, (short) 0, (short) 0);
					player.clientConnection.sendPacket(packet);
					
					PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(entity);
					player.clientConnection.sendPacket(meta);
				}
			}
		}
		List<Integer> ids = new ArrayList<>();
		for (Entity entity : entities) {
			if (!entitiesInRange.contains(entity)) {
				ids.add(entity.getEntityId());
			}
		}
		for (int id : ids) {
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(id);
			player.clientConnection.sendPacket(packet);
		}
		
		entities = entitiesInRange;
		
		int playerChunkX = (int) location.getX() >> 4;
		int playerChunkZ = (int) location.getZ() >> 4;
		World world = location.getWorld();
		
		Set<Chunk> chunksInRange = new HashSet<>();
		
		for (int x = playerChunkX - viewDistanceChunks; x < playerChunkX + viewDistanceChunks; x++) {
			for (int z = playerChunkZ - viewDistanceChunks; z < playerChunkZ + viewDistanceChunks; z++) {
				Chunk chunk = world.getChunkAt(x, z);
				if (chunk != null) {
					chunksInRange.add(chunk);
				}
			}
		}
		
		for (Entry<Chunk, World> entry : chunks.entrySet()) {
			Chunk chunk = entry.getKey();
			if (location.getWorld().getChunkXZ(chunk) == null) {
				World world0 = entry.getValue();
				int[] chunkPos = world0.getChunkXZ(chunk);
				PacketPlayOutUnloadChunk packet0 = new PacketPlayOutUnloadChunk(chunkPos[0], chunkPos[1]);
				player.clientConnection.sendPacket(packet0);
			}
		}
		
		for (Chunk chunk : chunksInRange) {
			if (!chunks.containsKey(chunk)) {
				int[] chunkPos = world.getChunkXZ(chunk);
				PacketPlayOutMapChunk packet0 = new PacketPlayOutMapChunk(chunkPos[0], chunkPos[1], chunk, world.getEnvironment());
				player.clientConnection.sendPacket(packet0);
				
				List<Byte[]> blockChunk = world.getLightEngineBlock().getBlockLightBitMask(chunkPos[0], chunkPos[1]);
				if (blockChunk == null) {
					blockChunk = new ArrayList<>();
				}
				List<Byte[]> skyChunk = null;
				if (world.hasSkyLight()) {
					skyChunk = world.getLightEngineSky().getSkyLightBitMask(chunkPos[0], chunkPos[1]);
				}
				if (skyChunk == null) {
					skyChunk = new ArrayList<>();
				}
				PacketPlayOutLightUpdate chunkdata = new PacketPlayOutLightUpdate(chunkPos[0], chunkPos[1], true, skyChunk, blockChunk);
				player.clientConnection.sendPacket(chunkdata);
			}
		}
		
		chunks = chunksInRange.stream().collect(Collectors.toMap(each -> each, each -> world));
	}

}

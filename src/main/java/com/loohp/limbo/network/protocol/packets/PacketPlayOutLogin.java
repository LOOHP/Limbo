/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loohp.limbo.network.protocol.packets;

import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.GameMode;
import com.loohp.limbo.world.Environment;
import com.loohp.limbo.world.World;
import net.kyori.adventure.key.Key;
import net.querz.nbt.tag.CompoundTag;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PacketPlayOutLogin extends PacketOut {
	
	private int entityId;
	private boolean isHardcore;
	private GameMode gamemode;
	private List<World> worlds;
	private CompoundTag dimensionCodec;
	private Environment dimension;
	private World world;
	private long hashedSeed;
	private byte maxPlayers;
	private int viewDistance;
	private int simulationDistance;
	private boolean reducedDebugInfo;
	private boolean enableRespawnScreen;
	private boolean isDebug;
	private boolean isFlat;

	public PacketPlayOutLogin(int entityId, boolean isHardcore, GameMode gamemode, List<World> worlds, CompoundTag dimensionCodec, World world, long hashedSeed, byte maxPlayers, int viewDistance, int simulationDistance, boolean reducedDebugInfo, boolean enableRespawnScreen, boolean isDebug, boolean isFlat) {
		this.entityId = entityId;
		this.isHardcore = isHardcore;
		this.gamemode = gamemode;
		this.worlds = worlds;
		this.dimensionCodec = dimensionCodec;
		this.dimension = world.getEnvironment();
		this.world = world;
		this.hashedSeed = hashedSeed;
		this.maxPlayers = maxPlayers;
		this.viewDistance = viewDistance;
		this.simulationDistance = simulationDistance;
		this.reducedDebugInfo = reducedDebugInfo;
		this.enableRespawnScreen = enableRespawnScreen;
		this.isDebug = isDebug;
		this.isFlat = isFlat;
	}

	public int getEntityId() {
		return entityId;
	}

	public boolean isHardcore() {
		return isHardcore;
	}

	public GameMode getGamemode() {
		return gamemode;
	}

	public List<World> getWorldsNames() {
		return worlds;
	}

	public CompoundTag getDimensionCodec() {
		return dimensionCodec;
	}

	public Environment getDimension() {
		return dimension;
	}

	public World getWorld() {
		return world;
	}

	public long getHashedSeed() {
		return hashedSeed;
	}

	public byte getMaxPlayers() {
		return maxPlayers;
	}

	public int getViewDistance() {
		return viewDistance;
	}
	
	public int getSimulationDistance() {
		return simulationDistance;
	}

	public boolean isReducedDebugInfo() {
		return reducedDebugInfo;
	}

	public boolean isEnableRespawnScreen() {
		return enableRespawnScreen;
	}

	public boolean isDebug() {
		return isDebug;
	}

	public boolean isFlat() {
		return isFlat;
	}
	
	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		output.writeInt(entityId);
		output.writeBoolean(isHardcore);
        output.writeByte((byte) gamemode.getId());
		output.writeByte(-1);
		DataTypeIO.writeVarInt(output, worlds.size());
		for (World world : worlds) {
			DataTypeIO.writeString(output, Key.key(world.getName()).toString(), StandardCharsets.UTF_8);
		}
		DataTypeIO.writeCompoundTag(output, dimensionCodec);
		DataTypeIO.writeString(output, world.getEnvironment().getKey().toString(), StandardCharsets.UTF_8);
		DataTypeIO.writeString(output, Key.key(world.getName()).toString(), StandardCharsets.UTF_8);
		output.writeLong(hashedSeed);
		DataTypeIO.writeVarInt(output, maxPlayers);
		DataTypeIO.writeVarInt(output, viewDistance);
		DataTypeIO.writeVarInt(output, simulationDistance);
		output.writeBoolean(reducedDebugInfo);
		output.writeBoolean(enableRespawnScreen);
		output.writeBoolean(isDebug);
		output.writeBoolean(isFlat);
		output.writeBoolean(false);

		return buffer.toByteArray();
	}

}

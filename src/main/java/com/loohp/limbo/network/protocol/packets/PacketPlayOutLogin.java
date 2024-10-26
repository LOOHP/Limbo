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

import com.loohp.limbo.registry.PacketRegistry;
import com.loohp.limbo.registry.RegistryCustom;
import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.GameMode;
import com.loohp.limbo.world.Environment;
import com.loohp.limbo.world.World;
import net.kyori.adventure.key.Key;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PacketPlayOutLogin extends PacketOut {
	
	private final int entityId;
	private final boolean isHardcore;
	private final List<World> worlds;
	private final int maxPlayers;
	private final int viewDistance;
	private final int simulationDistance;
	private final boolean reducedDebugInfo;
	private final boolean enableRespawnScreen;
	private final boolean doLimitedCrafting;
	private final Environment dimension;
	private final World world;
	private final long hashedSeed;
	private final GameMode gamemode;
	private final boolean isDebug;
	private final boolean isFlat;
	private final int portalCooldown;
	private final int seaLevel;
	private final boolean enforcesSecureChat;

	public PacketPlayOutLogin(int entityId, boolean isHardcore, List<World> worlds, int maxPlayers, int viewDistance, int simulationDistance, boolean reducedDebugInfo, boolean enableRespawnScreen, boolean doLimitedCrafting, Environment dimension, World world, long hashedSeed, GameMode gamemode, boolean isDebug, boolean isFlat, int portalCooldown, int seaLevel, boolean enforcesSecureChat) {
		this.entityId = entityId;
		this.isHardcore = isHardcore;
		this.worlds = worlds;
		this.maxPlayers = maxPlayers;
		this.viewDistance = viewDistance;
		this.simulationDistance = simulationDistance;
		this.reducedDebugInfo = reducedDebugInfo;
		this.enableRespawnScreen = enableRespawnScreen;
		this.doLimitedCrafting = doLimitedCrafting;
		this.dimension = dimension;
		this.world = world;
		this.hashedSeed = hashedSeed;
		this.gamemode = gamemode;
		this.isDebug = isDebug;
		this.isFlat = isFlat;
		this.portalCooldown = portalCooldown;
		this.seaLevel = seaLevel;
		this.enforcesSecureChat = enforcesSecureChat;
	}

	public int getEntityId() {
		return entityId;
	}

	public boolean isHardcore() {
		return isHardcore;
	}

	public List<World> getWorlds() {
		return worlds;
	}

	public int getMaxPlayers() {
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

	public boolean isDoLimitedCrafting() {
		return doLimitedCrafting;
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

	public GameMode getGamemode() {
		return gamemode;
	}

	public boolean isDebug() {
		return isDebug;
	}

	public boolean isFlat() {
		return isFlat;
	}

	public int getPortalCooldown() {
		return portalCooldown;
	}

	public int getSeaLevel() {
		return seaLevel;
	}

	public boolean isEnforcesSecureChat() {
		return enforcesSecureChat;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(PacketRegistry.getPacketId(getClass()));
		output.writeInt(entityId);
		output.writeBoolean(isHardcore);
		DataTypeIO.writeVarInt(output, worlds.size());
		for (World world : worlds) {
			DataTypeIO.writeString(output, Key.key(world.getName()).toString(), StandardCharsets.UTF_8);
		}
		DataTypeIO.writeVarInt(output, maxPlayers);
		DataTypeIO.writeVarInt(output, viewDistance);
		DataTypeIO.writeVarInt(output, simulationDistance);
		output.writeBoolean(reducedDebugInfo);
		output.writeBoolean(enableRespawnScreen);
		output.writeBoolean(doLimitedCrafting);
		DataTypeIO.writeVarInt(output, RegistryCustom.DIMENSION_TYPE.indexOf(world.getEnvironment().getKey()));
		DataTypeIO.writeString(output, Key.key(world.getName()).toString(), StandardCharsets.UTF_8);
		output.writeLong(hashedSeed);
        output.writeByte((byte) gamemode.getId());
		output.writeByte(-1);
		output.writeBoolean(isDebug);
		output.writeBoolean(isFlat);
		output.writeBoolean(false);
		DataTypeIO.writeVarInt(output, portalCooldown);
		DataTypeIO.writeVarInt(output, seaLevel);
		output.writeBoolean(enforcesSecureChat);

		return buffer.toByteArray();
	}

}

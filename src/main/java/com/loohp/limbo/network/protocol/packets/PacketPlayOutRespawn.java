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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.GameMode;
import com.loohp.limbo.utils.NamespacedKey;
import com.loohp.limbo.world.Environment;
import com.loohp.limbo.world.World;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class PacketPlayOutRespawn extends PacketOut {

	private Environment dimension;
	private String worldName;
	private CompoundTag dimensionCodec;
	private long hashedSeed;
	private GameMode gamemode;
	private boolean isDebug;
	private boolean isFlat;
	private boolean copyMetaData;

	public PacketPlayOutRespawn(World world, CompoundTag dimensionCodec, long hashedSeed, GameMode gamemode, boolean isDebug, boolean isFlat, boolean copyMetaData) {
		this.dimension = world.getEnvironment();
		this.dimensionCodec = dimensionCodec;
		this.worldName = new NamespacedKey(world.getName()).toString();
		this.hashedSeed = hashedSeed;
		this.gamemode = gamemode;
		this.isDebug = isDebug;
		this.isFlat = isFlat;
		this.copyMetaData = copyMetaData;
	}

	public CompoundTag getDimensionCodec() {
		return dimensionCodec;
	}

	public Environment getDimension() {
		return dimension;
	}

	public String getWorldName() {
		return worldName;
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

	public boolean isCopyMetaData() {
		return copyMetaData;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		CompoundTag tag = null;
		ListTag<CompoundTag> list = dimensionCodec.getCompoundTag("minecraft:dimension_type").getListTag("value").asCompoundTagList();
		for (CompoundTag each : list) {
			if (each.getString("name").equals(dimension.getNamespacedKey().toString())) {
				tag = each.getCompoundTag("element");
				break;
			}
		}
		DataTypeIO.writeCompoundTag(output, tag != null ? tag : list.get(0));
		DataTypeIO.writeString(output, worldName, StandardCharsets.UTF_8);
		output.writeLong(hashedSeed);
        output.writeByte((byte) gamemode.getId());
		output.writeByte((byte) gamemode.getId());
		output.writeBoolean(isDebug);
		output.writeBoolean(isFlat);
		output.writeBoolean(copyMetaData);
		
		return buffer.toByteArray();
	}

}

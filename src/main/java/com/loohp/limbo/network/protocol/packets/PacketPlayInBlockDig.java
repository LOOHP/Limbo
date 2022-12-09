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

import com.loohp.limbo.location.BlockFace;
import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.world.BlockPosition;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInBlockDig extends PacketIn {

	public enum PlayerDigType {

		START_DESTROY_BLOCK,
		ABORT_DESTROY_BLOCK,
		STOP_DESTROY_BLOCK,
		DROP_ALL_ITEMS,
		DROP_ITEM,
		RELEASE_USE_ITEM,
		SWAP_ITEM_WITH_OFFHAND;

	}

	private PlayerDigType action;
	private BlockPosition pos;
	private BlockFace direction;
	private int sequence;

	public PacketPlayInBlockDig(PlayerDigType action, BlockPosition pos, BlockFace direction, int sequence) {
		this.action = action;
		this.pos = pos;
		this.direction = direction;
		this.sequence = sequence;
	}

	public PacketPlayInBlockDig(DataInputStream in) throws IOException {
		this(PlayerDigType.values()[DataTypeIO.readVarInt(in)], DataTypeIO.readBlockPosition(in), BlockFace.values()[in.readByte()], DataTypeIO.readVarInt(in));
	}

	public BlockPosition getPos() {
		return pos;
	}

	public BlockFace getDirection() {
		return direction;
	}

	public PlayerDigType getAction() {
		return action;
	}

	public int getSequence() {
		return sequence;
	}
}

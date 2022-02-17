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

import java.io.DataInputStream;
import java.io.IOException;

import com.loohp.limbo.utils.DataTypeIO;

public class PacketPlayInResourcePackStatus extends PacketIn {
	
	public static enum EnumResourcePackStatus {
		SUCCESS,
		DECLINED,
		FAILED,
		ACCEPTED;
	}
	
	private EnumResourcePackStatus loaded;
	
	public PacketPlayInResourcePackStatus(EnumResourcePackStatus loaded) {
		this.loaded = loaded;
	}
	
	public PacketPlayInResourcePackStatus(DataInputStream in) throws IOException {
		this(toLoadedValue(DataTypeIO.readVarInt(in)));
	}
	
	public EnumResourcePackStatus getLoadedValue() {
		return loaded;
	}

	private static EnumResourcePackStatus toLoadedValue(int value) {
		switch (value) {
			case 0: 
				return EnumResourcePackStatus.SUCCESS;
			case 1: 
				return EnumResourcePackStatus.DECLINED;
			case 2: 
				return EnumResourcePackStatus.FAILED;
			case 3: 
				return EnumResourcePackStatus.ACCEPTED;
			default: 
				return EnumResourcePackStatus.FAILED;
		}
	}

}

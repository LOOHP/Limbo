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
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.utils.DataTypeIO;

public class PacketHandshakingIn extends PacketIn {
	
	public enum HandshakeType {
		STATUS(1),
		LOGIN(2);
		
		int networkId;
		
		HandshakeType(int networkId) {
			this.networkId = networkId;
		}
		
		public int getNetworkId() {
			return networkId;
		}
		
		public static HandshakeType fromNetworkId(int networkId) {
			for (HandshakeType type : HandshakeType.values()) {
				if (type.getNetworkId() == networkId) {
					return type;
				}
			}
			return null;
		}
	}
	
	//==============================
	
	private int protocolVersion;
	private String serverAddress;
	private int serverPort;
	private HandshakeType handshakeType;

	public PacketHandshakingIn(int protocolVersion, String serverAddress, int serverPort, HandshakeType handshakeType) {
		this.protocolVersion = protocolVersion;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.handshakeType = handshakeType;
	}
	
	public PacketHandshakingIn(DataInputStream in) throws IOException {
		this(DataTypeIO.readVarInt(in), DataTypeIO.readString(in, StandardCharsets.UTF_8), in.readShort() & 0xFFFF, HandshakeType.fromNetworkId(DataTypeIO.readVarInt(in)));
	}

	public int getProtocolVersion() {
		return protocolVersion;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public int getServerPort() {
		return serverPort;
	}

	public HandshakeType getHandshakeType() {
		return handshakeType;
	}

}

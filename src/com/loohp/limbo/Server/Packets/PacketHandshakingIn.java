package com.loohp.limbo.Server.Packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.loohp.limbo.Utils.DataTypeIO;

public class PacketHandshakingIn extends PacketIn {
	
	public static enum HandshakeType {
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
			System.out.println("Invalid HandshakeType networkId, expected 0 or 1, but got " + networkId);
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
		this(DataTypeIO.readVarInt(in), DataTypeIO.readString(in), in.readShort() & 0xFFFF, HandshakeType.fromNetworkId(DataTypeIO.readVarInt(in)));
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

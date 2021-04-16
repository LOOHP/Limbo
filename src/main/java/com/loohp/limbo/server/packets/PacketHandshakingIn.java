package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketHandshakingIn extends PacketIn {

    private final int protocolVersion;

    //==============================
    private final String serverAddress;
    private final int serverPort;
    private final HandshakeType handshakeType;
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

    public enum HandshakeType {
        STATUS(1),
        LOGIN(2);

        int networkId;

        HandshakeType(int networkId) {
            this.networkId = networkId;
        }

        public static HandshakeType fromNetworkId(int networkId) {
            for (HandshakeType type : HandshakeType.values()) {
                if (type.getNetworkId() == networkId) {
                    return type;
                }
            }
            return null;
        }

        public int getNetworkId() {
            return networkId;
        }
    }

}

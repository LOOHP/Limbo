package com.loohp.limbo.server.packets;

import com.loohp.limbo.server.packets.PacketPlayOutPlayerInfo.PlayerInfoData.PlayerInfoDataAddPlayer;
import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.GameMode;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class PacketPlayOutPlayerInfo extends PacketOut {

    private final PlayerInfoAction action;
    private final UUID uuid;
    private final PlayerInfoData data;
    public PacketPlayOutPlayerInfo(PlayerInfoAction action, UUID uuid, PlayerInfoData data) {
        this.action = action;
        this.uuid = uuid;
        this.data = data;
    }

    public PlayerInfoAction getAction() {
        return action;
    }

    public UUID getUuid() {
        return uuid;
    }

    public PlayerInfoData getData() {
        return data;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));
        DataTypeIO.writeVarInt(output, action.getId());
        DataTypeIO.writeVarInt(output, 1);
        DataTypeIO.writeUUID(output, uuid);

        switch (action) {
            case ADD_PLAYER:
                PlayerInfoDataAddPlayer data = (PlayerInfoDataAddPlayer) this.data;
                DataTypeIO.writeString(output, data.getName(), StandardCharsets.UTF_8);
                if (data.getProperty().isPresent()) {
                    DataTypeIO.writeVarInt(output, 1);
                    DataTypeIO.writeString(output, "textures", StandardCharsets.UTF_8);
                    DataTypeIO.writeString(output, data.getProperty().get().getSkin(), StandardCharsets.UTF_8);
                    output.writeBoolean(true);
                    DataTypeIO.writeString(output, data.getProperty().get().getSignature(), StandardCharsets.UTF_8);
                } else {
                    DataTypeIO.writeVarInt(output, 0);
                }
                DataTypeIO.writeVarInt(output, data.getGamemode().getId());
                DataTypeIO.writeVarInt(output, data.getPing());
                if (data.getDisplayNameJson().isPresent()) {
                    output.writeBoolean(true);
                    DataTypeIO.writeString(output, data.getDisplayNameJson().get(), StandardCharsets.UTF_8);
                } else {
                    output.writeBoolean(false);
                }
                break;
            case REMOVE_PLAYER:
                break;
            case UPDATE_DISPLAY_NAME:
                break;
            case UPDATE_GAMEMODE:
                break;
            case UPDATE_LATENCY:
                break;
        }

        return buffer.toByteArray();
    }

    public enum PlayerInfoAction {
        ADD_PLAYER(0), UPDATE_GAMEMODE(1), UPDATE_LATENCY(2), UPDATE_DISPLAY_NAME(3), REMOVE_PLAYER(4);

        int id;

        PlayerInfoAction(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    // =========

    public static class PlayerInfoData {

        public static class PlayerInfoDataAddPlayer extends PlayerInfoData {

            private final String name;
            private final Optional<PlayerSkinProperty> skin;
            private final GameMode gamemode;
            private final int ping;
            private final boolean hasDisplayName;
            private final Optional<String> displayNameJson;

            public PlayerInfoDataAddPlayer(String name, Optional<PlayerSkinProperty> skin, GameMode gamemode, int ping,
                                           boolean hasDisplayName, Optional<String> displayNameJson) {
                this.name = name;
                this.skin = skin;
                this.gamemode = gamemode;
                this.ping = ping;
                this.hasDisplayName = hasDisplayName;
                this.displayNameJson = displayNameJson;
            }

            public String getName() {
                return name;
            }

            public Optional<PlayerSkinProperty> getProperty() {
                return skin;
            }

            public GameMode getGamemode() {
                return gamemode;
            }

            public int getPing() {
                return ping;
            }

            public boolean isHasDisplayName() {
                return hasDisplayName;
            }

            public Optional<String> getDisplayNameJson() {
                return displayNameJson;
            }

            public static class PlayerSkinProperty {

                private final String skin;
                private final String signature;

                public PlayerSkinProperty(String skin, String signature) {
                    this.skin = skin;
                    this.signature = signature;
                }

                public String getSkin() {
                    return skin;
                }

                public String getSignature() {
                    return signature;
                }

            }

        }

    }

}

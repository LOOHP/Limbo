/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2024. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2024. Contributors
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

package com.loohp.limbo.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.loohp.limbo.Limbo;
import com.loohp.limbo.network.ClientConnection;
import com.loohp.limbo.network.protocol.packets.ClientboundChunkBatchFinishedPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundChunkBatchStartPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundClearTitlesPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundFinishConfigurationPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundLevelChunkWithLightPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundRegistryDataPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundResourcePackPushPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundSetActionBarTextPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundSetSubtitleTextPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundSetTitleTextPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundSetTitlesAnimationPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundSystemChatPacket;
import com.loohp.limbo.network.protocol.packets.Packet;
import com.loohp.limbo.network.protocol.packets.PacketHandshakingIn;
import com.loohp.limbo.network.protocol.packets.PacketLoginInLoginStart;
import com.loohp.limbo.network.protocol.packets.PacketLoginInPluginMessaging;
import com.loohp.limbo.network.protocol.packets.PacketLoginOutDisconnect;
import com.loohp.limbo.network.protocol.packets.PacketLoginOutLoginSuccess;
import com.loohp.limbo.network.protocol.packets.PacketLoginOutPluginMessaging;
import com.loohp.limbo.network.protocol.packets.PacketPlayInBlockDig;
import com.loohp.limbo.network.protocol.packets.PacketPlayInBlockPlace;
import com.loohp.limbo.network.protocol.packets.PacketPlayInChat;
import com.loohp.limbo.network.protocol.packets.PacketPlayInCloseWindow;
import com.loohp.limbo.network.protocol.packets.PacketPlayInHeldItemChange;
import com.loohp.limbo.network.protocol.packets.PacketPlayInItemName;
import com.loohp.limbo.network.protocol.packets.PacketPlayInKeepAlive;
import com.loohp.limbo.network.protocol.packets.PacketPlayInPickItem;
import com.loohp.limbo.network.protocol.packets.PacketPlayInPluginMessaging;
import com.loohp.limbo.network.protocol.packets.PacketPlayInPosition;
import com.loohp.limbo.network.protocol.packets.PacketPlayInPositionAndLook;
import com.loohp.limbo.network.protocol.packets.PacketPlayInRotation;
import com.loohp.limbo.network.protocol.packets.PacketPlayInSetCreativeSlot;
import com.loohp.limbo.network.protocol.packets.PacketPlayInTabComplete;
import com.loohp.limbo.network.protocol.packets.PacketPlayInUseItem;
import com.loohp.limbo.network.protocol.packets.PacketPlayInWindowClick;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutBoss;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutCloseWindow;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutDeclareCommands;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutDisconnect;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutEntityDestroy;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutEntityMetadata;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutGameStateChange;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutHeldItemChange;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutKeepAlive;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutLogin;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutNamedSoundEffect;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutOpenWindow;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPlayerAbilities;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPlayerInfo;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPlayerListHeaderFooter;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPluginMessaging;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPositionAndLook;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutRespawn;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutSetSlot;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutSpawnEntity;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutSpawnPosition;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutStopSound;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutTabComplete;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutUnloadChunk;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutUpdateViewPosition;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutWindowData;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutWindowItems;
import com.loohp.limbo.network.protocol.packets.PacketStatusInPing;
import com.loohp.limbo.network.protocol.packets.PacketStatusInRequest;
import com.loohp.limbo.network.protocol.packets.PacketStatusOutPong;
import com.loohp.limbo.network.protocol.packets.PacketStatusOutResponse;
import com.loohp.limbo.network.protocol.packets.ServerboundChatCommandPacket;
import com.loohp.limbo.network.protocol.packets.ServerboundFinishConfigurationPacket;
import com.loohp.limbo.network.protocol.packets.ServerboundLoginAcknowledgedPacket;
import com.loohp.limbo.network.protocol.packets.ServerboundResourcePackPacket;
import net.kyori.adventure.key.Key;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("PatternValidation")
public class PacketRegistry {

    private static final Map<NetworkPhase, Map<PacketBound, BiMap<Key, Integer>>> ID_REGISTRY = new HashMap<>();
    private static final BiMap<Class<? extends Packet>, PacketClassInfo> CLASS_REGISTRY = HashBiMap.create();

    static {
        String name = "reports/packets.json";

        InputStream inputStream = Limbo.class.getClassLoader().getResourceAsStream(name);
        if (inputStream == null) {
            throw new RuntimeException("Failed to load " + name + " from jar!");
        }
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            JSONObject json = (JSONObject) new JSONParser().parse(reader);
            for (Object objKey : json.keySet()) {
                String key = (String) objKey;
                NetworkPhase networkPhase = NetworkPhase.fromName(key);
                Map<PacketBound, BiMap<Key, Integer>> mappings = new HashMap<>();
                JSONObject jsonMappings = (JSONObject) json.get(key);
                for (Object objBoundKey : jsonMappings.keySet()) {
                    String boundKey = (String) objBoundKey;
                    PacketBound packetBound = PacketBound.fromName(boundKey);
                    BiMap<Key, Integer> idMapping = HashBiMap.create();
                    JSONObject jsonIds = (JSONObject) jsonMappings.get(boundKey);
                    for (Object objPacketKey : jsonIds.keySet()) {
                        String packetKey = (String) objPacketKey;
                        idMapping.put(Key.key(packetKey), (int) (long) ((JSONObject) jsonIds.get(packetKey)).get("protocol_id"));
                    }
                    mappings.put(packetBound, idMapping);
                }
                ID_REGISTRY.put(networkPhase, mappings);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        registerClass(PacketHandshakingIn.class, "minecraft:intention", NetworkPhase.HANDSHAKE, PacketBound.SERVERBOUND);

        registerClass(PacketStatusInRequest.class, "minecraft:status_request", NetworkPhase.STATUS, PacketBound.SERVERBOUND);
        registerClass(PacketStatusInPing.class, "minecraft:ping_request", NetworkPhase.STATUS, PacketBound.SERVERBOUND);

        registerClass(PacketStatusOutResponse.class, "minecraft:status_response", NetworkPhase.STATUS, PacketBound.CLIENTBOUND);
        registerClass(PacketStatusOutPong.class, "minecraft:pong_response", NetworkPhase.STATUS, PacketBound.CLIENTBOUND);

        registerClass(PacketLoginInLoginStart.class, "minecraft:hello", NetworkPhase.LOGIN, PacketBound.SERVERBOUND);
        registerClass(PacketLoginInPluginMessaging.class, "minecraft:custom_query_answer", NetworkPhase.LOGIN, PacketBound.SERVERBOUND);
        registerClass(ServerboundLoginAcknowledgedPacket.class, "minecraft:login_acknowledged", NetworkPhase.LOGIN, PacketBound.SERVERBOUND);

        registerClass(PacketLoginOutLoginSuccess.class, "minecraft:login_finished", NetworkPhase.LOGIN, PacketBound.CLIENTBOUND);
        registerClass(PacketLoginOutDisconnect.class, "minecraft:login_disconnect", NetworkPhase.LOGIN, PacketBound.CLIENTBOUND);
        registerClass(PacketLoginOutPluginMessaging.class, "minecraft:custom_query", NetworkPhase.LOGIN, PacketBound.CLIENTBOUND);

        registerClass(ServerboundFinishConfigurationPacket.class, "minecraft:finish_configuration", NetworkPhase.CONFIGURATION, PacketBound.SERVERBOUND);

        registerClass(ClientboundRegistryDataPacket.class, "minecraft:registry_data", NetworkPhase.CONFIGURATION, PacketBound.CLIENTBOUND);
        registerClass(ClientboundFinishConfigurationPacket.class, "minecraft:finish_configuration", NetworkPhase.CONFIGURATION, PacketBound.CLIENTBOUND);

        registerClass(PacketPlayInKeepAlive.class, "minecraft:keep_alive", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(ServerboundChatCommandPacket.class, "minecraft:chat_command", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInChat.class, "minecraft:chat", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInPosition.class, "minecraft:move_player_pos", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInPositionAndLook.class, "minecraft:move_player_pos_rot", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInRotation.class, "minecraft:move_player_rot", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInPluginMessaging.class, "minecraft:custom_payload", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInTabComplete.class, "minecraft:command_suggestion", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInHeldItemChange.class, "minecraft:set_carried_item", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(ServerboundResourcePackPacket.class, "minecraft:resource_pack", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInUseItem.class, "minecraft:use_item_on", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInBlockPlace.class, "minecraft:use_item", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInSetCreativeSlot.class, "minecraft:set_creative_mode_slot", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInWindowClick.class, "minecraft:container_click", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInCloseWindow.class, "minecraft:container_close", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInPickItem.class, "minecraft:pick_item", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInBlockDig.class, "minecraft:player_action", NetworkPhase.PLAY, PacketBound.SERVERBOUND);
        registerClass(PacketPlayInItemName.class, "minecraft:rename_item", NetworkPhase.PLAY, PacketBound.SERVERBOUND);

        registerClass(PacketPlayOutLogin.class, "minecraft:login", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutPositionAndLook.class, "minecraft:player_position", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutSpawnPosition.class, "minecraft:set_default_spawn_position", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(ClientboundSystemChatPacket.class, "minecraft:system_chat", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutPlayerAbilities.class, "minecraft:player_abilities", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(ClientboundLevelChunkWithLightPacket.class, "minecraft:level_chunk_with_light", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutUnloadChunk.class, "minecraft:forget_level_chunk", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutKeepAlive.class, "minecraft:keep_alive", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutGameStateChange.class, "minecraft:game_event", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutPlayerInfo.class, "minecraft:player_info_update", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutUpdateViewPosition.class, "minecraft:set_chunk_cache_center", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutDisconnect.class, "minecraft:disconnect", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutPluginMessaging.class, "minecraft:custom_payload", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutTabComplete.class, "minecraft:command_suggestions", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutDeclareCommands.class, "minecraft:commands", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutRespawn.class, "minecraft:respawn", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutEntityDestroy.class, "minecraft:remove_entities", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutEntityMetadata.class, "minecraft:set_entity_data", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutSpawnEntity.class, "minecraft:add_entity", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutHeldItemChange.class, "minecraft:set_carried_item", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutPlayerListHeaderFooter.class, "minecraft:tab_list", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(ClientboundResourcePackPushPacket.class, "minecraft:resource_pack_push", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(ClientboundSetTitlesAnimationPacket.class, "minecraft:set_titles_animation", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(ClientboundSetTitleTextPacket.class, "minecraft:set_title_text", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(ClientboundSetSubtitleTextPacket.class, "minecraft:set_subtitle_text", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(ClientboundSetActionBarTextPacket.class, "minecraft:set_action_bar_text", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(ClientboundClearTitlesPacket.class, "minecraft:clear_titles", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutBoss.class, "minecraft:boss_event", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutNamedSoundEffect.class, "minecraft:sound", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutStopSound.class, "minecraft:stop_sound", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutWindowItems.class, "minecraft:container_set_content", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutSetSlot.class, "minecraft:container_set_slot", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutOpenWindow.class, "minecraft:open_screen", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutCloseWindow.class, "minecraft:container_close", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(PacketPlayOutWindowData.class, "minecraft:container_set_data", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(ClientboundChunkBatchFinishedPacket.class, "minecraft:chunk_batch_finished", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
        registerClass(ClientboundChunkBatchStartPacket.class, "minecraft:chunk_batch_start", NetworkPhase.PLAY, PacketBound.CLIENTBOUND);
    }

    private static void registerClass(Class<? extends Packet> packetClass, String key, NetworkPhase networkPhase, PacketBound packetBound) {
        CLASS_REGISTRY.put(packetClass, new PacketClassInfo(Key.key(key), networkPhase, packetBound));
    }

    public static PacketClassInfo getPacketInfo(Class<? extends Packet> packetClass) {
        return CLASS_REGISTRY.get(packetClass);
    }

    public static int getPacketId(Class<? extends Packet> packetClass) {
        PacketClassInfo info = getPacketInfo(packetClass);
        return ID_REGISTRY.get(info.getNetworkPhase()).get(info.getPacketBound()).get(info.getKey());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Packet> Class<? extends T> getPacketClass(int packetId, NetworkPhase networkPhase, PacketBound packetBound) {
        Key key = ID_REGISTRY.get(networkPhase).get(packetBound).inverse().get(packetId);
        return (Class<? extends T>) CLASS_REGISTRY.inverse().get(new PacketClassInfo(key, networkPhase, packetBound));
    }

    public enum NetworkPhase {

        HANDSHAKE("handshake", ClientConnection.ClientState.HANDSHAKE),
        STATUS("status", ClientConnection.ClientState.STATUS),
        CONFIGURATION("configuration", ClientConnection.ClientState.CONFIGURATION),
        LOGIN("login", ClientConnection.ClientState.LOGIN),
        PLAY("play", ClientConnection.ClientState.PLAY);

        public static NetworkPhase fromName(String name) {
            for (NetworkPhase phase : values()) {
                if (phase.getName().equals(name)) {
                    return phase;
                }
            }
            return null;
        }

        public static NetworkPhase fromClientState(ClientConnection.ClientState clientState) {
            for (NetworkPhase phase : values()) {
                if (phase.getClientState().equals(clientState)) {
                    return phase;
                }
            }
            return null;
        }

        private final String name;
        private final ClientConnection.ClientState clientState;

        NetworkPhase(String name, ClientConnection.ClientState clientState) {
            this.name = name;
            this.clientState = clientState;
        }

        public String getName() {
            return name;
        }

        public ClientConnection.ClientState getClientState() {
            return clientState;
        }
    }

    public enum PacketBound {

        SERVERBOUND("serverbound"),
        CLIENTBOUND("clientbound");

        public static PacketBound fromName(String name) {
            for (PacketBound bound : values()) {
                if (bound.getName().equals(name)) {
                    return bound;
                }
            }
            return null;
        }

        private final String name;

        PacketBound(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class PacketClassInfo {
        private final Key key;
        private final NetworkPhase networkPhase;
        private final PacketBound packetBound;

        public PacketClassInfo(Key key, NetworkPhase networkPhase, PacketBound packetBound) {
            this.key = key;
            this.networkPhase = networkPhase;
            this.packetBound = packetBound;
        }

        public Key getKey() {
            return key;
        }

        public NetworkPhase getNetworkPhase() {
            return networkPhase;
        }

        public PacketBound getPacketBound() {
            return packetBound;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PacketClassInfo that = (PacketClassInfo) o;
            return Objects.equals(key, that.key) && networkPhase == that.networkPhase && packetBound == that.packetBound;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, networkPhase, packetBound);
        }
    }

}

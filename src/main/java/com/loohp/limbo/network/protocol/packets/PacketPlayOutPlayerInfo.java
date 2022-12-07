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
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

import com.loohp.limbo.network.protocol.packets.PacketPlayOutPlayerInfo.PlayerInfoData.PlayerInfoDataAddPlayer;
import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.GameMode;

public class PacketPlayOutPlayerInfo extends PacketOut {

	public enum PlayerInfoAction {
		ADD_PLAYER,
		INITIALIZE_CHAT,
		UPDATE_GAME_MODE,
		UPDATE_LISTED,
		UPDATE_LATENCY,
		UPDATE_DISPLAY_NAME;
	}

	private EnumSet<PlayerInfoAction> actions;
	private UUID uuid;
	private PlayerInfoData data;

	public PacketPlayOutPlayerInfo(EnumSet<PlayerInfoAction> actions, UUID uuid, PlayerInfoData data) {
		this.actions = actions;
		this.uuid = uuid;
		this.data = data;
	}

	public EnumSet<PlayerInfoAction> getActions() {
		return actions;
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

		DataTypeIO.writeEnumSet(output, actions, PlayerInfoAction.class);
		DataTypeIO.writeVarInt(output, 1);
		DataTypeIO.writeUUID(output, uuid);

		PlayerInfoDataAddPlayer data = (PlayerInfoDataAddPlayer) this.data;
		for (PlayerInfoAction action : actions) {
			switch (action) {
				case ADD_PLAYER: {
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
					break;
				}
				case INITIALIZE_CHAT: {
					break;
				}
				case UPDATE_GAME_MODE: {
					DataTypeIO.writeVarInt(output, data.getGamemode().getId());
					break;
				}
				case UPDATE_LISTED: {
					output.writeBoolean(data.isListed());
					break;
				}
				case UPDATE_LATENCY: {
					DataTypeIO.writeVarInt(output, data.getPing());
					break;
				}
				case UPDATE_DISPLAY_NAME: {
					if (data.getDisplayNameJson().isPresent()) {
						output.writeBoolean(true);
						DataTypeIO.writeString(output, data.getDisplayNameJson().get(), StandardCharsets.UTF_8);
					} else {
						output.writeBoolean(false);
					}
					break;
				}
			}
		}
		
		return buffer.toByteArray();
	}

	// =========

	public static class PlayerInfoData {

		public static class PlayerInfoDataAddPlayer extends PlayerInfoData {

			private String name;
			private boolean listed;
			private Optional<PlayerSkinProperty> skin;
			private GameMode gamemode;
			private int ping;
			private boolean hasDisplayName;
			private Optional<String> displayNameJson;
			
			public PlayerInfoDataAddPlayer(String name, boolean listed, Optional<PlayerSkinProperty> skin, GameMode gamemode, int ping, boolean hasDisplayName, Optional<String> displayNameJson) {
				this.name = name;
				this.listed = listed;
				this.skin = skin;
				this.gamemode = gamemode;
				this.ping = ping;
				this.hasDisplayName = hasDisplayName;
				this.displayNameJson = displayNameJson;
			}

			public String getName() {
				return name;
			}

			public boolean isListed() {
				return listed;
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

				private String skin;
				private String signature;
				
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

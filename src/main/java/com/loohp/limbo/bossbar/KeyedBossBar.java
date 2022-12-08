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

package com.loohp.limbo.bossbar;

import com.loohp.limbo.network.protocol.packets.PacketPlayOutBoss;
import com.loohp.limbo.player.Player;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class KeyedBossBar {

    private final UUID uuid;
    private final Key key;
    private final BossBar properties;
    private final Set<Player> players;
    protected final LimboBossBarHandler listener;
    protected final AtomicBoolean valid;
    private final Unsafe unsafe;

    KeyedBossBar(Key key, BossBar properties) {
        this.uuid = UUID.randomUUID();
        this.key = key;
        this.properties = properties;
        this.players = ConcurrentHashMap.newKeySet();
        this.listener = new LimboBossBarHandler(this);
        this.properties.addListener(listener);
        this.valid = new AtomicBoolean(true);
        this.unsafe = new Unsafe(this);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Key getKey() {
        return key;
    }

    public BossBar getProperties() {
        return properties;
    }

    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isValid() {
        return valid.get();
    }

    @Deprecated
    public Unsafe getUnsafe() {
        return unsafe;
    }

    public boolean showPlayer(Player player) {
        PacketPlayOutBoss packetPlayOutBoss = new PacketPlayOutBoss(this, PacketPlayOutBoss.BossBarAction.ADD);
        try {
            player.clientConnection.sendPacket(packetPlayOutBoss);
        } catch (IOException ignore) {
        }
        return players.add(player);
    }

    public boolean hidePlayer(Player player) {
        PacketPlayOutBoss packetPlayOutBoss = new PacketPlayOutBoss(this, PacketPlayOutBoss.BossBarAction.REMOVE);
        try {
            player.clientConnection.sendPacket(packetPlayOutBoss);
        } catch (IOException ignore) {
        }
        return players.remove(player);
    }

    public static class LimboBossBarHandler implements BossBar.Listener {

        private final KeyedBossBar parent;

        private LimboBossBarHandler(KeyedBossBar parent) {
            this.parent = parent;
        }

        @Override
        public void bossBarNameChanged(@NotNull BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
            PacketPlayOutBoss packetPlayOutBoss = new PacketPlayOutBoss(parent, PacketPlayOutBoss.BossBarAction.UPDATE_NAME);
            for (Player player : parent.getPlayers()) {
                try {
                    player.clientConnection.sendPacket(packetPlayOutBoss);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void bossBarProgressChanged(@NotNull BossBar bar, float oldProgress, float newProgress) {
            PacketPlayOutBoss packetPlayOutBoss = new PacketPlayOutBoss(parent, PacketPlayOutBoss.BossBarAction.UPDATE_PROGRESS);
            for (Player player : parent.getPlayers()) {
                try {
                    player.clientConnection.sendPacket(packetPlayOutBoss);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void bossBarColorChanged(@NotNull BossBar bar, BossBar.@NotNull Color oldColor, BossBar.@NotNull Color newColor) {
            PacketPlayOutBoss packetPlayOutBoss = new PacketPlayOutBoss(parent, PacketPlayOutBoss.BossBarAction.UPDATE_STYLE);
            for (Player player : parent.getPlayers()) {
                try {
                    player.clientConnection.sendPacket(packetPlayOutBoss);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void bossBarOverlayChanged(@NotNull BossBar bar, BossBar.@NotNull Overlay oldOverlay, BossBar.@NotNull Overlay newOverlay) {
            PacketPlayOutBoss packetPlayOutBoss = new PacketPlayOutBoss(parent, PacketPlayOutBoss.BossBarAction.UPDATE_STYLE);
            for (Player player : parent.getPlayers()) {
                try {
                    player.clientConnection.sendPacket(packetPlayOutBoss);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void bossBarFlagsChanged(@NotNull BossBar bar, @NotNull Set<BossBar.Flag> flagsAdded, @NotNull Set<BossBar.Flag> flagsRemoved) {
            PacketPlayOutBoss packetPlayOutBoss = new PacketPlayOutBoss(parent, PacketPlayOutBoss.BossBarAction.UPDATE_PROPERTIES);
            for (Player player : parent.getPlayers()) {
                try {
                    player.clientConnection.sendPacket(packetPlayOutBoss);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

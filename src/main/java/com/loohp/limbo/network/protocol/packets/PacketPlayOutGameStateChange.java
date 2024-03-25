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

package com.loohp.limbo.network.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutGameStateChange extends PacketOut {

    public enum GameStateChangeEvent {
        NO_RESPAWN_BLOCK_AVAILABLE(0),
        START_RAINING(1),
        STOP_RAINING(2),
        CHANGE_GAME_MODE(3),
        WIN_GAME(4),
        DEMO_EVENT(5),
        ARROW_HIT_PLAYER(6),
        RAIN_LEVEL_CHANGE(7),
        THUNDER_LEVEL_CHANGE(8),
        PUFFER_FISH_STING(9),
        GUARDIAN_ELDER_EFFECT(10),
        IMMEDIATE_RESPAWN(11),
        LIMITED_CRAFTING(12),
        LEVEL_CHUNKS_LOAD_START(13);

        private final int id;

        GameStateChangeEvent(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private GameStateChangeEvent event;
    private float value;

    public PacketPlayOutGameStateChange(GameStateChangeEvent event, float value) {
        this.event = event;
        this.value = value;
    }

    public GameStateChangeEvent getEvent() {
        return event;
    }

    public float getValue() {
        return value;
    }

    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));
        output.writeByte(event.getId());
        output.writeFloat(value);

        return buffer.toByteArray();
    }

}

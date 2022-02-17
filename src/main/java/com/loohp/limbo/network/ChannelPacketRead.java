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

package com.loohp.limbo.network;

import com.loohp.limbo.network.protocol.packets.PacketIn;

import java.io.DataInput;

public final class ChannelPacketRead {

    private int size;
    private int packetId;
    private DataInput input;
    private PacketIn packet;

    ChannelPacketRead(int size, int packetId, DataInput input) {
        this.size = size;
        this.packetId = packetId;
        this.input = input;
        this.packet = null;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public boolean hasReadPacket() {
        return packet != null;
    }

    public PacketIn getReadPacket() {
        return packet;
    }

    public void setPacket(PacketIn packet) {
        this.packet = packet;
    }

    public DataInput getDataInput() {
        return input;
    }

}

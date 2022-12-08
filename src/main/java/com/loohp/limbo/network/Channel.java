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
import com.loohp.limbo.network.protocol.packets.PacketOut;
import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.NamespacedKey;
import com.loohp.limbo.utils.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Channel implements AutoCloseable {

    private final List<Pair<NamespacedKey, ChannelPacketHandler>> handlers;
    private final AtomicBoolean valid;
    protected final DataInputStream input;
    protected final DataOutputStream output;

    public Channel(DataInputStream input, DataOutputStream output) {
        this.input = input;
        this.output = output;
        this.handlers = new CopyOnWriteArrayList<>();
        this.valid = new AtomicBoolean(true);
    }

    private void ensureOpen() {
        if (!valid.get()) {
            close();
        }
    }

    public void addHandlerBefore(NamespacedKey key, ChannelPacketHandler handler) {
        handlers.add(0, new Pair<>(key, handler));
    }

    public void addHandlerAfter(NamespacedKey key, ChannelPacketHandler handler) {
        handlers.add(new Pair<>(key, handler));
    }

    public void removeHandler(NamespacedKey key) {
        handlers.removeIf(each -> each.getFirst().equals(key));
    }

    protected PacketIn readPacket() throws Exception {
        return readPacket(-1);
    }

    protected PacketIn readPacket(int size) throws IOException {
        PacketIn packet = null;
        do {
            ensureOpen();
            size = size < 0 ? DataTypeIO.readVarInt(input) : size;
            int packetId = DataTypeIO.readVarInt(input);
            ChannelPacketRead read = new ChannelPacketRead(size, packetId, input);
            for (Pair<NamespacedKey, ChannelPacketHandler> pair : handlers) {
                read = pair.getSecond().read(read);
                if (read == null) {
                    packet = null;
                    break;
                }
                packet = read.getReadPacket();
            }
            size = -1;
        } while (packet == null);
        return packet;
    }

    protected boolean writePacket(PacketOut packet) throws IOException {
        ensureOpen();
        ChannelPacketWrite write = new ChannelPacketWrite(packet);
        for (Pair<NamespacedKey, ChannelPacketHandler> pair : handlers) {
            write = pair.getSecond().write(write);
            if (write == null) {
                return false;
            }
        }
        packet = write.getPacket();
        byte[] packetByte = packet.serializePacket();
        DataTypeIO.writeVarInt(output, packetByte.length);
        output.write(packetByte);
        output.flush();
        return true;
    }

    @Override
    public synchronized void close() {
        if (valid.compareAndSet(true, false)) {
            try {
                input.close();
                output.close();
            } catch (Exception ignore) {
            }
        }
    }

}

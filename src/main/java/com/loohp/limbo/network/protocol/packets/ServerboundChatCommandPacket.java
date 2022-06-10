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

import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.NetworkEncryptionUtils.ArgumentSignatures;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ServerboundChatCommandPacket extends PacketIn {

    private String command;
    private Instant time;
    private ArgumentSignatures argumentSignatures;
    private boolean commandPreview;

    public ServerboundChatCommandPacket(String command, Instant time, ArgumentSignatures argumentSignatures, boolean commandPreview) {
        this.command = command;
        this.time = time;
        this.argumentSignatures = argumentSignatures;
        this.commandPreview = commandPreview;
    }

    public ServerboundChatCommandPacket(DataInputStream in) throws IOException {
        this.command = DataTypeIO.readString(in, StandardCharsets.UTF_8);
        this.time = Instant.ofEpochMilli(in.readLong());
        long salt = in.readLong();
        int size = DataTypeIO.readVarInt(in);
        Map<String, byte[]> signatures = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            String key = DataTypeIO.readString(in, StandardCharsets.UTF_8);
            int arraySize = DataTypeIO.readVarInt(in);
            byte[] value = new byte[arraySize];
            in.readFully(value);
            signatures.put(key, value);
        }
        this.argumentSignatures = new ArgumentSignatures(salt, signatures);
        this.commandPreview = in.readBoolean();
    }

    public String getCommand() {
        return command;
    }

    public Instant getTime() {
        return time;
    }

    public ArgumentSignatures getArgumentSignatures() {
        return argumentSignatures;
    }

    public boolean isCommandPreview() {
        return commandPreview;
    }

}

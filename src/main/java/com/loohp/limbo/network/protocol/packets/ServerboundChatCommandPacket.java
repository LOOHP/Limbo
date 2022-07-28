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

import com.loohp.limbo.utils.ArgumentSignatures;
import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.LastSeenMessages;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class ServerboundChatCommandPacket extends PacketIn {

    private String command;
    private Instant time;
    private long salt;
    private ArgumentSignatures argumentSignatures;
    private boolean commandPreview;
    private LastSeenMessages.b lastSeenMessages;

    public ServerboundChatCommandPacket(String command, Instant time, long salt, ArgumentSignatures argumentSignatures, boolean commandPreview, LastSeenMessages.b lastSeenMessages) {
        this.command = command;
        this.time = time;
        this.salt = salt;
        this.argumentSignatures = argumentSignatures;
        this.commandPreview = commandPreview;
        this.lastSeenMessages = lastSeenMessages;
    }

    public ServerboundChatCommandPacket(DataInputStream in) throws IOException {
        this.command = DataTypeIO.readString(in, StandardCharsets.UTF_8);
        this.time = Instant.ofEpochMilli(in.readLong());
        this.salt = in.readLong();
        this.argumentSignatures = new ArgumentSignatures(in);
        this.commandPreview = in.readBoolean();
        this.lastSeenMessages = new LastSeenMessages.b(in);
    }

    public String getCommand() {
        return command;
    }

    public Instant getTime() {
        return time;
    }

    public long getSalt() {
        return salt;
    }

    public ArgumentSignatures getArgumentSignatures() {
        return argumentSignatures;
    }

    public boolean isCommandPreview() {
        return commandPreview;
    }

    public LastSeenMessages.b getLastSeenMessages() {
        return lastSeenMessages;
    }
}

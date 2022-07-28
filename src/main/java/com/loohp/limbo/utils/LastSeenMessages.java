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

package com.loohp.limbo.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LastSeenMessages {

    public static LastSeenMessages EMPTY = new LastSeenMessages(Collections.emptyList());
    public static final int LAST_SEEN_MESSAGES_MAX_LENGTH = 5;

    private List<a> entries;

    public LastSeenMessages(List<LastSeenMessages.a> entries) {
        this.entries = entries;
    }

    public LastSeenMessages(DataInputStream in) throws IOException {
        int size = DataTypeIO.readVarInt(in);
        entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            entries.add(new LastSeenMessages.a(in));
        }
    }

    public void write(DataOutputStream out) throws IOException {
        DataTypeIO.writeVarInt(out, entries.size());
        for (LastSeenMessages.a lastseenmessages_a : entries) {
            lastseenmessages_a.write(out);
        }
    }

    public void updateHash(DataOutputStream dataoutput) throws IOException {
        Iterator<a> iterator = this.entries.iterator();

        while (iterator.hasNext()) {
            LastSeenMessages.a lastseenmessages_a = iterator.next();
            UUID uuid = lastseenmessages_a.getProfileId();
            MessageSignature messagesignature = lastseenmessages_a.getLastSignature();

            dataoutput.writeByte(70);
            dataoutput.writeLong(uuid.getMostSignificantBits());
            dataoutput.writeLong(uuid.getLeastSignificantBits());
            dataoutput.write(messagesignature.getBytes());
        }

    }

    public static class a {

        private UUID profileId;
        private MessageSignature lastSignature;

        public UUID getProfileId() {
            return profileId;
        }

        public MessageSignature getLastSignature() {
            return lastSignature;
        }

        public a(UUID profileId, MessageSignature lastSignature) {
            this.profileId = profileId;
            this.lastSignature = lastSignature;
        }

        public a(DataInputStream in) throws IOException {
            this(DataTypeIO.readUUID(in), new MessageSignature(in));
        }

        public void write(DataOutputStream out) throws IOException {
            DataTypeIO.writeUUID(out, this.profileId);
            this.lastSignature.write(out);
        }
    }

    public static class b {

        private LastSeenMessages lastSeen;
        private Optional<a> lastReceived;

        public b(LastSeenMessages lastSeen, Optional<LastSeenMessages.a> lastReceived) {
            this.lastSeen = lastSeen;
            this.lastReceived = lastReceived;
        }

        public b(DataInputStream in) throws IOException {
            this.lastSeen = new LastSeenMessages(in);
            if (in.readBoolean()) {
                this.lastReceived = Optional.of(new LastSeenMessages.a(in));
            } else {
                this.lastReceived = Optional.empty();
            }
        }

        public void write(DataOutputStream out) throws IOException {
            this.lastSeen.write(out);
            if (lastReceived.isPresent()) {
                out.writeBoolean(true);
                lastReceived.get().write(out);
            } else {
                out.writeBoolean(false);
            }
        }

        public LastSeenMessages getLastSeen() {
            return lastSeen;
        }

        public Optional<a> getLastReceived() {
            return lastReceived;
        }
    }

}

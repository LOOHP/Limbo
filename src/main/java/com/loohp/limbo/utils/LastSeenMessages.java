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
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class LastSeenMessages {

    public static final ArgumentSignatures EMPTY = new ArgumentSignatures(Collections.emptyList());
    private static final int MAX_ARGUMENT_COUNT = 8;
    private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

    private List<MessageSignature> entries;

    public static class a {

        public static final LastSeenMessages.a EMPTY = new LastSeenMessages.a(Collections.emptyList());

        private final List<MessageSignature.a> entries;

        public a(List<MessageSignature.a> entries) {
            this.entries = entries;
        }

        public a(DataInputStream in) throws IOException {
            int size = DataTypeIO.readVarInt(in);
            entries = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                entries.add(MessageSignature.a.read(in));
            }
        }

        public void write(DataOutputStream out) throws IOException {
            DataTypeIO.writeVarInt(out, this.entries.size());
            for (MessageSignature.a entry : this.entries) {
                MessageSignature.a.write(out, entry);
            }
        }
    }

    public static class b {

        private final int offset;
        private final BitSet acknowledged;
        private final byte checksum;

        public b(int offset, BitSet acknowledged, byte checksum) {
            this.offset = offset;
            this.acknowledged = acknowledged;
            this.checksum = checksum;
        }

        public b(DataInputStream in) throws IOException {
            this(DataTypeIO.readVarInt(in), DataTypeIO.readFixedBitSet(in, 20), in.readByte());
        }

        public void write(DataOutputStream out) throws IOException {
            DataTypeIO.writeVarInt(out, this.offset);
            DataTypeIO.writeFixedBitSet(out, this.acknowledged, 20);
            out.writeByte(checksum);
        }
    }

}

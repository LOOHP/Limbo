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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ArgumentSignatures {

    public static final ArgumentSignatures EMPTY = new ArgumentSignatures(Collections.emptyList());
    private static final int MAX_ARGUMENT_COUNT = 8;
    private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

    private List<a> entries;

    public ArgumentSignatures(List<a> entries) {
        this.entries = entries;
    }

    public ArgumentSignatures(DataInputStream in) throws IOException {
        int size = DataTypeIO.readVarInt(in);
        entries = new ArrayList<>(8);
        for (int i = 0; i < size; i++) {
            entries.add(new ArgumentSignatures.a(in));
        }
    }

    public List<a> getEntries() {
        return entries;
    }

    public MessageSignature get(String s) {
        Iterator<ArgumentSignatures.a> iterator = this.entries.iterator();

        ArgumentSignatures.a argumentsignatures_a;

        do {
            if (!iterator.hasNext()) {
                return MessageSignature.EMPTY;
            }

            argumentsignatures_a = iterator.next();
        } while (!argumentsignatures_a.name.equals(s));

        return argumentsignatures_a.signature;
    }

    public void write(DataOutputStream out) throws IOException {
        DataTypeIO.writeVarInt(out, entries.size());
        for (ArgumentSignatures.a argumentsignatures_a : entries) {
            argumentsignatures_a.write(out);
        }
    }

    public static class a {

        private String name;
        private MessageSignature signature;

        public a(String name, MessageSignature signature) {
            this.name = name;
            this.signature = signature;
        }

        public String getName() {
            return name;
        }

        public MessageSignature getSignature() {
            return signature;
        }

        public a(DataInputStream in) throws IOException {
            this(DataTypeIO.readString(in, StandardCharsets.UTF_8), new MessageSignature(in));
        }

        public void write(DataOutputStream out) throws IOException {
            DataTypeIO.writeString(out, name, StandardCharsets.UTF_8);
            this.signature.write(out);
        }
    }

    @FunctionalInterface
    public interface b {

        MessageSignature sign(String s, String s1);
    }

}

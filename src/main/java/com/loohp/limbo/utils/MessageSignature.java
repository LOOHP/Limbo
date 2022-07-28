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
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;

public class MessageSignature {

    public static final MessageSignature EMPTY = new MessageSignature(new byte[0]);

    private byte[] bytes;

    public MessageSignature(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public MessageSignature(DataInputStream in) throws IOException {
        this.bytes = new byte[DataTypeIO.readVarInt(in)];
        in.readFully(bytes);
    }

    public void write(DataOutputStream out) throws IOException {
        out.write(this.bytes.length);
        out.write(this.bytes);
    }

    public boolean isEmpty() {
        return this.bytes.length == 0;
    }

    public ByteBuffer asByteBuffer() {
        return !this.isEmpty() ? ByteBuffer.wrap(this.bytes) : null;
    }

    public boolean equals(Object object) {
        boolean flag;

        if (this != object) {
            label22:
            {
                if (object instanceof MessageSignature) {
                    MessageSignature messagesignature = (MessageSignature) object;

                    if (Arrays.equals(this.bytes, messagesignature.bytes)) {
                        break label22;
                    }
                }

                flag = false;
                return flag;
            }
        }

        flag = true;
        return flag;
    }

    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    public String toString() {
        return !this.isEmpty() ? Base64.getEncoder().encodeToString(this.bytes) : "empty";
    }

}

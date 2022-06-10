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

import com.google.common.primitives.Longs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class NetworkEncryptionUtils {

    public static class SignatureData {

        public static final SignatureData NONE = new SignatureData(0L, new byte[0]);
        private long salt;
        private byte[] signature;

        public SignatureData(long salt, byte[] signature) {
            this.salt = salt;
            this.signature = signature;
        }

        public SignatureData(DataInputStream in) throws IOException {
            this.salt = in.readLong();
            int length = DataTypeIO.readVarInt(in);
            this.signature = new byte[length];
            in.readFully(this.signature);
        }

        public boolean isSignaturePresent() {
            return this.signature.length > 0;
        }

        public static void write(DataOutputStream out, SignatureData signatureData) throws IOException {
            out.writeLong(signatureData.salt);
            DataTypeIO.writeVarInt(out, signatureData.signature.length);
            out.write(signatureData.signature);
        }

        public byte[] getSalt() {
            return Longs.toByteArray(this.salt);
        }
    }

    public static class ArgumentSignatures {

        private long salt;
        private Map<String, byte[]> signatures;

        public ArgumentSignatures(long salt, Map<String, byte[]> signatures) {
            this.salt = salt;
            this.signatures = signatures;
        }

        public long getSalt() {
            return salt;
        }

        public Map<String, byte[]> getSignatures() {
            return signatures;
        }

    }

}

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

import com.loohp.limbo.registry.PacketRegistry;
import com.loohp.limbo.utils.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PacketPlayOutPositionAndLook extends PacketOut {

	public enum Relative {

		X(0), Y(1), Z(2), Y_ROT(3), X_ROT(4), DELTA_X(5), DELTA_Y(6), DELTA_Z(7), ROTATE_DELTA(8);

		public static final Set<Relative> ALL = new LinkedHashSet<>(Arrays.asList(values()));
		public static final Set<Relative> ROTATION = Stream.of(Relative.X_ROT, Relative.Y_ROT).collect(Collectors.toSet());
		public static final Set<Relative> DELTA = Stream.of(Relative.DELTA_X, Relative.DELTA_Y, Relative.DELTA_Z, Relative.ROTATE_DELTA).collect(Collectors.toSet());

		private final int bit;

		@SafeVarargs
		public static Set<Relative> union(Set<Relative>... aset) {
			HashSet<Relative> hashset = new HashSet<>();
            int i = aset.length;
            for (Set<Relative> set : aset) {
                hashset.addAll(set);
            }
			return hashset;
		}

		Relative(final int i) {
			this.bit = i;
		}

		private int getMask() {
			return 1 << this.bit;
		}

		private boolean isSet(int i) {
			return (i & this.getMask()) == this.getMask();
		}

		public static Set<Relative> unpack(int i) {
			Set<Relative> set = EnumSet.noneOf(Relative.class);
			Relative[] arelative = values();
			int j = arelative.length;
            for (Relative relative : arelative) {
                if (relative.isSet(i)) {
                    set.add(relative);
                }
            }
			return set;
		}

		public static int pack(Set<Relative> set) {
			int i = 0;
			Relative relative;
			for (Iterator<Relative> iterator = set.iterator(); iterator.hasNext(); i |= relative.getMask()) {
				relative = iterator.next();
			}
			return i;
		}
	}
	
	private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final Set<Relative> relatives;
    private final int teleportId;
	
	public PacketPlayOutPositionAndLook(double x, double y, double z, float yaw, float pitch, int teleportId, Relative... relatives) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.teleportId = teleportId;
		this.relatives = new HashSet<>(Arrays.asList(relatives));
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public Set<Relative> getRelatives() {
		return relatives;
	}

	public int getTeleportId() {
		return teleportId;
	}
	
	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(PacketRegistry.getPacketId(getClass()));
		DataTypeIO.writeVarInt(output, teleportId);
		output.writeDouble(x);
		output.writeDouble(y);
		output.writeDouble(z);
		output.writeDouble(0);
		output.writeDouble(0);
		output.writeDouble(0);
		output.writeFloat(yaw);
		output.writeFloat(pitch);
		output.writeInt(Relative.pack(relatives));
		
		return buffer.toByteArray();
	}

}

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

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInPosition extends PacketIn {
	
	private final double x;
    private final double y;
    private final double z;
    private final boolean onGround;
	
	public PacketPlayInPosition(double x, double y, double z, boolean onGround) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.onGround = onGround;
	}
	
	public PacketPlayInPosition(DataInputStream in) throws IOException {
		this(in.readDouble(), in.readDouble(), in.readDouble(), in.readBoolean());
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

	public boolean onGround() {
		return onGround;
	}

}

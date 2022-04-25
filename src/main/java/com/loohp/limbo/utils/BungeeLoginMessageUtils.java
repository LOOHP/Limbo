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

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPluginMessaging;

public class BungeeLoginMessageUtils {

	public static final String BUNGEECORD_MAIN = new NamespacedKey("bungeecord", "main").toString();

	public static void sendUUIDRequest(DataOutputStream output) throws IOException {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("UUID");
		
		PacketPlayOutPluginMessaging packet = new PacketPlayOutPluginMessaging(BUNGEECORD_MAIN, out.toByteArray());
		byte[] packetByte = packet.serializePacket();
		DataTypeIO.writeVarInt(output, packetByte.length);
		output.write(packetByte);
	}
	
	public static UUID readUUIDResponse(byte[] data) {
		ByteArrayDataInput in = ByteStreams.newDataInput(data);
	    String subchannel = in.readUTF();
	    if (subchannel.equals("UUID")) {
	    	return UUID.fromString(in.readUTF());
	    } else {
	    	throw new RuntimeException("Bungeecord Message received is not an IP");
	    }
	}
	
	public static void sendIPRequest(DataOutputStream output) throws IOException {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("IP");
		
		PacketPlayOutPluginMessaging packet = new PacketPlayOutPluginMessaging(BUNGEECORD_MAIN, out.toByteArray());
		byte[] packetByte = packet.serializePacket();
		DataTypeIO.writeVarInt(output, packetByte.length);
		output.write(packetByte);
	}
	
	public static InetAddress readIPResponse(byte[] data) throws UnknownHostException {
		ByteArrayDataInput in = ByteStreams.newDataInput(data);
	    String subchannel = in.readUTF();
	    if (subchannel.equals("IP")) {
	    	String ip = in.readUTF();
	    	in.readInt();
	    	return InetAddress.getByName(ip);
	    } else {
	    	throw new RuntimeException("Bungeecord Message received is not an IP");
	    }
	}

}

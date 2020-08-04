package com.loohp.limbo.Utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.loohp.limbo.Server.Packets.PacketPlayOutPluginMessaging;

public class BungeeLoginMessageUtils {

	public static void sendUUIDRequest(DataOutputStream output) throws IOException {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("UUID");
		
		PacketPlayOutPluginMessaging packet = new PacketPlayOutPluginMessaging(new NamespacedKey("bungeecord", "main"), out.toByteArray());
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
	    	throw new RuntimeException("Bungeecord Message receieved is not an IP");
	    }
	}
	
	public static void sendIPRequest(DataOutputStream output) throws IOException {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("IP");
		
		PacketPlayOutPluginMessaging packet = new PacketPlayOutPluginMessaging(new NamespacedKey("bungeecord", "main"), out.toByteArray());
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
	    	throw new RuntimeException("Bungeecord Message receieved is not an IP");
	    }
	}

}

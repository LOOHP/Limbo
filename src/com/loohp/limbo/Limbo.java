package com.loohp.limbo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loohp.limbo.File.ServerProperties;
import com.loohp.limbo.Location.Location;
import com.loohp.limbo.Server.ServerConnection;
import com.loohp.limbo.Server.Packets.Packet;
import com.loohp.limbo.Server.Packets.PacketHandshakingIn;
import com.loohp.limbo.Server.Packets.PacketLoginInLoginStart;
import com.loohp.limbo.Server.Packets.PacketLoginOutLoginSuccess;
import com.loohp.limbo.Server.Packets.PacketPlayInChat;
import com.loohp.limbo.Server.Packets.PacketPlayInKeepAlive;
import com.loohp.limbo.Server.Packets.PacketPlayInPosition;
import com.loohp.limbo.Server.Packets.PacketPlayInPositionAndLook;
import com.loohp.limbo.Server.Packets.PacketPlayOutChat;
import com.loohp.limbo.Server.Packets.PacketPlayOutKeepAlive;
import com.loohp.limbo.Server.Packets.PacketPlayOutLogin;
import com.loohp.limbo.Server.Packets.PacketPlayOutMapChunk;
import com.loohp.limbo.Server.Packets.PacketPlayOutPlayerAbilities;
import com.loohp.limbo.Server.Packets.PacketPlayOutPlayerInfo;
import com.loohp.limbo.Server.Packets.PacketPlayOutPositionAndLook;
import com.loohp.limbo.Server.Packets.PacketPlayOutShowPlayerSkins;
import com.loohp.limbo.Server.Packets.PacketPlayOutSpawnPosition;
import com.loohp.limbo.Server.Packets.PacketPlayOutUpdateViewPosition;
import com.loohp.limbo.Server.Packets.PacketStatusInPing;
import com.loohp.limbo.Server.Packets.PacketStatusInRequest;
import com.loohp.limbo.Server.Packets.PacketStatusOutPong;
import com.loohp.limbo.Server.Packets.PacketStatusOutResponse;
import com.loohp.limbo.Utils.ImageUtils;
import com.loohp.limbo.World.Schematic;
import com.loohp.limbo.World.World;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;

public class Limbo {
	
	private static Limbo instance;
	
	public static void main(String args[]) throws IOException {
		new Limbo();
	}
	
	public static Limbo getInstance() {
		return instance;
	}
	
	//===========================
	
	private ServerConnection server;
	private Console console;
	
	private List<World> worlds = new ArrayList<>();
	
	private ServerProperties properties;
	
	public Limbo() throws IOException {
		instance = this;
		
		console = new Console(System.in, System.out);
		
		String spName = "server.properties";
        File sp = new File(spName);
        if (!sp.exists()) {
        	try (InputStream in = getClass().getClassLoader().getResourceAsStream(spName)) {
                Files.copy(in, sp.toPath());
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        properties = new ServerProperties(sp);
		
		Map<Integer, Class<? extends Packet>> HandshakeIn = new HashMap<>();
		HandshakeIn.put(0x00, PacketHandshakingIn.class);
		Packet.setHandshakeIn(HandshakeIn);
		
		Map<Integer, Class<? extends Packet>> StatusIn = new HashMap<>();
		StatusIn.put(0x00, PacketStatusInRequest.class);
		StatusIn.put(0x01, PacketStatusInPing.class);
		Packet.setStatusIn(StatusIn);
		
		Map<Class<? extends Packet>, Integer> StatusOut = new HashMap<>();
		StatusOut.put(PacketStatusOutResponse.class, 0x00);
		StatusOut.put(PacketStatusOutPong.class, 0x01);
		Packet.setStatusOut(StatusOut);
		
		Map<Integer, Class<? extends Packet>> LoginIn = new HashMap<>();
		LoginIn.put(0x00, PacketLoginInLoginStart.class);
		Packet.setLoginIn(LoginIn);
		
		Map<Class<? extends Packet>, Integer> LoginOut = new HashMap<>();
		LoginOut.put(PacketLoginOutLoginSuccess.class, 0x02);
		Packet.setLoginOut(LoginOut);
		
		Map<Integer, Class<? extends Packet>> PlayIn = new HashMap<>();
		PlayIn.put(0x10, PacketPlayInKeepAlive.class);
		PlayIn.put(0x12, PacketPlayInPosition.class);
		PlayIn.put(0x13, PacketPlayInPositionAndLook.class);
		PlayIn.put(0x03, PacketPlayInChat.class);
		Packet.setPlayIn(PlayIn);
		
		Map<Class<? extends Packet>, Integer> PlayOut = new HashMap<>();
		PlayOut.put(PacketPlayOutLogin.class, 0x25);
		PlayOut.put(PacketPlayOutSpawnPosition.class, 0x42);
		PlayOut.put(PacketPlayOutPositionAndLook.class, 0x35);
		PlayOut.put(PacketPlayOutMapChunk.class, 0x21);
		PlayOut.put(PacketPlayOutKeepAlive.class, 0x20);
		PlayOut.put(PacketPlayOutUpdateViewPosition.class, 0x40);
		PlayOut.put(PacketPlayOutPlayerInfo.class, 0x33);
		PlayOut.put(PacketPlayOutShowPlayerSkins.class, 0x44);
		PlayOut.put(PacketPlayOutPlayerAbilities.class, 0x31);
		PlayOut.put(PacketPlayOutChat.class, 0x0E);
		Packet.setPlayOut(PlayOut);
		
		worlds.add(loadDefaultWorld());
		Location spawn = properties.getWorldSpawn();
		properties.setWorldSpawn(new Location(getWorld(properties.getLevelName().getKey()), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch()));
		
		server = new ServerConnection(properties.getServerIp(), properties.getServerPort());	
		
		console.run();
	}
	
	private World loadDefaultWorld() throws IOException {		
		File schem = new File(properties.getSchemFileName());
		
		if (!schem.exists()) {
			System.out.println("Schemetic file " + properties.getSchemFileName() + " for world " + properties.getLevelName() + " not found!");
			return null;
		}
		
		World world = Schematic.toWorld(properties.getLevelName().getKey(), (CompoundTag) NBTUtil.read(schem).getTag());
		
		System.out.println("Loaded world " + properties.getLevelName() + " with the schematic file " + properties.getSchemFileName());
		
		return world;		
	}
	
	public ServerProperties getServerProperties() {
		return properties;
	}
	
	public ServerConnection getServerConnection() {
		return server;
	}

	public Console getConsole() {
		return console;
	}
	
	public List<World> getWorlds() {
		return new ArrayList<>(worlds);
	}
	
	public World getWorld(String name) {
		for (World world : worlds) {
			if (world.getName().equalsIgnoreCase(name)) {
				return world;
			}
		}
		return null;
	}
	
	public String getServerListResponseJson() throws IOException {
		String base = ServerProperties.JSON_BASE_RESPONSE;
		base = base.replace("%VERSION%", properties.getVersionString());
		base = base.replace("%PROTOCOL%", String.valueOf(properties.getProtocol()));
		base = base.replace("%MOTD%", properties.getMotdJson());
		base = base.replace("%MAXPLAYERS%", String.valueOf(properties.getMaxPlayers()));
		base = base.replace("%ONLINECLIENTS%", String.valueOf(server.getClients().size()));
		
		if (properties.getFavicon().isPresent()) {
			String icon = "\"favicon\":\"data:image/png;base64," + ImageUtils.imgToBase64String(properties.getFavicon().get(), "png") + "\",";
			base = base.replace("%FAVICON%", icon);
		} else {
			base = base.replace("%FAVICON%", "");
		}
		
		return base;
	}

}

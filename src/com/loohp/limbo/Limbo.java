package com.loohp.limbo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.loohp.limbo.File.ServerProperties;
import com.loohp.limbo.Location.Location;
import com.loohp.limbo.Player.Player;
import com.loohp.limbo.Server.ServerConnection;
import com.loohp.limbo.Server.Packets.Packet;
import com.loohp.limbo.Server.Packets.PacketIn;
import com.loohp.limbo.Server.Packets.PacketOut;
import com.loohp.limbo.Utils.ImageUtils;
import com.loohp.limbo.World.Schematic;
import com.loohp.limbo.World.World;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;

public class Limbo {
	
	private static Limbo instance;
	
	public static void main(String args[]) throws IOException, ParseException, NumberFormatException, ClassNotFoundException {
		new Limbo();
	}
	
	public static Limbo getInstance() {
		return instance;
	}
	
	//===========================
	
	private ServerConnection server;
	private Console console;
	
	private List<World> worlds = new ArrayList<>();
	private Map<String, Player> playersByName = new HashMap<>();
	private Map<UUID, Player> playersByUUID = new HashMap<>();
	
	private ServerProperties properties;
	
	public AtomicInteger entityCount = new AtomicInteger();
	
	@SuppressWarnings("unchecked")
	public Limbo() throws IOException, ParseException, NumberFormatException, ClassNotFoundException {
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
		
        String mappingName = "mapping.json";
        File mappingFile = new File(mappingName);
        if (!mappingFile.exists()) {
        	try (InputStream in = getClass().getClassLoader().getResourceAsStream(mappingName)) {
                Files.copy(in, mappingFile.toPath());
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        
        JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(mappingFile));
        
        String classPrefix = Packet.class.getName().substring(0, Packet.class.getName().lastIndexOf(".") + 1);
        
		Map<Integer, Class<? extends PacketIn>> HandshakeIn = new HashMap<>();
		for (Object key : ((JSONObject) json.get("HandshakeIn")).keySet()) {
			int packetId = Integer.decode((String) key);
			HandshakeIn.put(packetId, (Class<? extends PacketIn>) Class.forName(classPrefix + (String) ((JSONObject) json.get("HandshakeIn")).get(key)));
		}
		Packet.setHandshakeIn(HandshakeIn);
		
		Map<Integer, Class<? extends PacketIn>> StatusIn = new HashMap<>();
		for (Object key : ((JSONObject) json.get("StatusIn")).keySet()) {
			int packetId = Integer.decode((String) key);
			StatusIn.put(packetId, (Class<? extends PacketIn>) Class.forName(classPrefix + (String) ((JSONObject) json.get("StatusIn")).get(key)));
		}
		Packet.setStatusIn(StatusIn);
		
		Map<Class<? extends PacketOut>, Integer> StatusOut = new HashMap<>();
		for (Object key : ((JSONObject) json.get("StatusOut")).keySet()) {
			Class<? extends PacketOut> packetClass = (Class<? extends PacketOut>) Class.forName(classPrefix + (String) key);
			StatusOut.put(packetClass, Integer.decode((String) ((JSONObject) json.get("StatusOut")).get(key)));
		}
		Packet.setStatusOut(StatusOut);
		
		Map<Integer, Class<? extends PacketIn>> LoginIn = new HashMap<>();
		for (Object key : ((JSONObject) json.get("LoginIn")).keySet()) {
			int packetId = Integer.decode((String) key);
			LoginIn.put(packetId, (Class<? extends PacketIn>) Class.forName(classPrefix + (String) ((JSONObject) json.get("LoginIn")).get(key)));
		}
		Packet.setLoginIn(LoginIn);
		
		Map<Class<? extends PacketOut>, Integer> LoginOut = new HashMap<>();
		for (Object key : ((JSONObject) json.get("LoginOut")).keySet()) {
			Class<? extends PacketOut> packetClass = (Class<? extends PacketOut>) Class.forName(classPrefix + (String) key);
			LoginOut.put(packetClass, Integer.decode((String) ((JSONObject) json.get("LoginOut")).get(key)));
		}
		Packet.setLoginOut(LoginOut);
		
		Map<Integer, Class<? extends PacketIn>> PlayIn = new HashMap<>();
		for (Object key : ((JSONObject) json.get("PlayIn")).keySet()) {
			int packetId = Integer.decode((String) key);
			PlayIn.put(packetId, (Class<? extends PacketIn>) Class.forName(classPrefix + (String) ((JSONObject) json.get("PlayIn")).get(key)));
		}
		Packet.setPlayIn(PlayIn);
		
		Map<Class<? extends PacketOut>, Integer> PlayOut = new HashMap<>();
		for (Object key : ((JSONObject) json.get("PlayOut")).keySet()) {
			Class<? extends PacketOut> packetClass = (Class<? extends PacketOut>) Class.forName(classPrefix + (String) key);
			PlayOut.put(packetClass, Integer.decode((String) ((JSONObject) json.get("PlayOut")).get(key)));
		}
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
	
	public Set<Player> getPlayers() {
		return new HashSet<>(playersByUUID.values());
	}
	
	public Player getPlayer(String name) {
		return playersByName.get(name);
	}
	
	public Player getPlayer(UUID uuid) {
		return playersByUUID.get(uuid);
	}
	
	public void addPlayer(Player player) {
		playersByName.put(player.getName(), player);
		playersByUUID.put(player.getUUID(), player);
	}
	
	public void removePlayer(Player player) {
		playersByName.remove(player.getName());
		playersByUUID.remove(player.getUUID());
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
	
	public void stopServer() {
		Limbo.getInstance().getConsole().sendMessage("Stopping Server...");
		for (Player player : getPlayers()) {
			player.disconnect("Server closed");
		}
		while (!getPlayers().isEmpty()) {
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

}

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

import com.loohp.limbo.Commands.CommandSender;
import com.loohp.limbo.Events.EventsManager;
import com.loohp.limbo.File.ServerProperties;
import com.loohp.limbo.Location.Location;
import com.loohp.limbo.Permissions.PermissionsManager;
import com.loohp.limbo.Player.Player;
import com.loohp.limbo.Plugins.LimboPlugin;
import com.loohp.limbo.Plugins.PluginManager;
import com.loohp.limbo.Server.ServerConnection;
import com.loohp.limbo.Server.Packets.Packet;
import com.loohp.limbo.Server.Packets.PacketIn;
import com.loohp.limbo.Server.Packets.PacketOut;
import com.loohp.limbo.Utils.CustomStringUtils;
import com.loohp.limbo.Utils.ImageUtils;
import com.loohp.limbo.Utils.NetworkUtils;
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
	
	private PluginManager pluginManager;
	private EventsManager eventsManager;
	private PermissionsManager permissionManager;
	private File pluginFolder;
	
	public AtomicInteger entityIdCount = new AtomicInteger();
	
	@SuppressWarnings("unchecked")
	public Limbo() throws IOException, ParseException, NumberFormatException, ClassNotFoundException {
		instance = this;
		
		console = new Console(System.in, System.out, System.err);
		
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
        
        if (!properties.isBungeecord()) {
        	console.sendMessage("If you are using bungeecord, consider turning that on in the settings!");
        } else {
        	console.sendMessage("Starting Limbo server in bungeecord mode!");
        }
		
        String mappingName = "mapping.json";
        File mappingFile = new File(mappingName);
        if (!mappingFile.exists()) {
        	try (InputStream in = getClass().getClassLoader().getResourceAsStream(mappingName)) {
                Files.copy(in, mappingFile.toPath());
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        
        console.sendMessage("Loading packet id mappings from mapping.json ...");
        
        JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(mappingFile));
        
        String classPrefix = Packet.class.getName().substring(0, Packet.class.getName().lastIndexOf(".") + 1);
        int mappingsCount = 0;
        
		Map<Integer, Class<? extends PacketIn>> HandshakeIn = new HashMap<>();
		for (Object key : ((JSONObject) json.get("HandshakeIn")).keySet()) {
			int packetId = Integer.decode((String) key);
			HandshakeIn.put(packetId, (Class<? extends PacketIn>) Class.forName(classPrefix + (String) ((JSONObject) json.get("HandshakeIn")).get(key)));
		}
		Packet.setHandshakeIn(HandshakeIn);
		mappingsCount += HandshakeIn.size();
		
		Map<Integer, Class<? extends PacketIn>> StatusIn = new HashMap<>();
		for (Object key : ((JSONObject) json.get("StatusIn")).keySet()) {
			int packetId = Integer.decode((String) key);
			StatusIn.put(packetId, (Class<? extends PacketIn>) Class.forName(classPrefix + (String) ((JSONObject) json.get("StatusIn")).get(key)));
		}
		Packet.setStatusIn(StatusIn);
		mappingsCount += StatusIn.size();
		
		Map<Class<? extends PacketOut>, Integer> StatusOut = new HashMap<>();
		for (Object key : ((JSONObject) json.get("StatusOut")).keySet()) {
			Class<? extends PacketOut> packetClass = (Class<? extends PacketOut>) Class.forName(classPrefix + (String) key);
			StatusOut.put(packetClass, Integer.decode((String) ((JSONObject) json.get("StatusOut")).get(key)));
		}
		Packet.setStatusOut(StatusOut);
		mappingsCount += StatusOut.size();
		
		Map<Integer, Class<? extends PacketIn>> LoginIn = new HashMap<>();
		for (Object key : ((JSONObject) json.get("LoginIn")).keySet()) {
			int packetId = Integer.decode((String) key);
			LoginIn.put(packetId, (Class<? extends PacketIn>) Class.forName(classPrefix + (String) ((JSONObject) json.get("LoginIn")).get(key)));
		}
		Packet.setLoginIn(LoginIn);
		mappingsCount += LoginIn.size();
		
		Map<Class<? extends PacketOut>, Integer> LoginOut = new HashMap<>();
		for (Object key : ((JSONObject) json.get("LoginOut")).keySet()) {
			Class<? extends PacketOut> packetClass = (Class<? extends PacketOut>) Class.forName(classPrefix + (String) key);
			LoginOut.put(packetClass, Integer.decode((String) ((JSONObject) json.get("LoginOut")).get(key)));
		}
		Packet.setLoginOut(LoginOut);
		mappingsCount += LoginOut.size();
		
		Map<Integer, Class<? extends PacketIn>> PlayIn = new HashMap<>();
		for (Object key : ((JSONObject) json.get("PlayIn")).keySet()) {
			int packetId = Integer.decode((String) key);
			PlayIn.put(packetId, (Class<? extends PacketIn>) Class.forName(classPrefix + (String) ((JSONObject) json.get("PlayIn")).get(key)));
		}
		Packet.setPlayIn(PlayIn);
		mappingsCount += PlayIn.size();
		
		Map<Class<? extends PacketOut>, Integer> PlayOut = new HashMap<>();
		for (Object key : ((JSONObject) json.get("PlayOut")).keySet()) {
			Class<? extends PacketOut> packetClass = (Class<? extends PacketOut>) Class.forName(classPrefix + (String) key);
			PlayOut.put(packetClass, Integer.decode((String) ((JSONObject) json.get("PlayOut")).get(key)));
		}
		Packet.setPlayOut(PlayOut);
		mappingsCount += PlayOut.size();
		
		console.sendMessage("Loaded all " + mappingsCount + " packet id mappings!");
		
		worlds.add(loadDefaultWorld());
		Location spawn = properties.getWorldSpawn();
		properties.setWorldSpawn(new Location(getWorld(properties.getLevelName().getKey()), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch()));
		
		if (!NetworkUtils.available(properties.getServerPort())) {
			console.sendMessage("");
			console.sendMessage("*****FAILED TO BIND PORT [" + properties.getServerPort() + "]*****");
			console.sendMessage("*****PORT ALREADY IN USE*****");
			console.sendMessage("*****PERHAPS ANOTHER INSTANCE OF THE SERVER IS ALREADY RUNNING?*****");
			console.sendMessage("");
			System.exit(2);
		}
		
		String permissionName = "permission.yml";
        File permissionFile = new File(permissionName);
        if (!permissionFile.exists()) {
        	try (InputStream in = getClass().getClassLoader().getResourceAsStream(permissionName)) {
                Files.copy(in, permissionFile.toPath());
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        
        permissionManager = new PermissionsManager();
        permissionManager.loadDefaultPermissionFile(permissionFile);     
        
        eventsManager = new EventsManager();
		
		pluginFolder = new File("plugins");
		pluginFolder.mkdirs();
		
		pluginManager = new PluginManager(pluginFolder);
		
		for (LimboPlugin plugin : Limbo.getInstance().getPluginManager().getPlugins()) {
			console.sendMessage("Enabling plugin " + plugin.getName() + " " + plugin.getInfo().getVersion());
			plugin.onEnable();
		}
		
		server = new ServerConnection(properties.getServerIp(), properties.getServerPort());
		
		console.run();
	}
	
	public EventsManager getEventsManager() {
		return eventsManager;
	}
	
	public PermissionsManager getPermissionsManager() {
		return permissionManager;
	}
	
	public File getPluginFolder() {
		return pluginFolder;
	}
	
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	private World loadDefaultWorld() throws IOException {
		console.sendMessage("Loading world " + properties.getLevelName() + " with the schematic file " + properties.getSchemFileName() + " ...");
		
		File schem = new File(properties.getSchemFileName());
		
		if (!schem.exists()) {
			console.sendMessage("Schemetic file " + properties.getSchemFileName() + " for world " + properties.getLevelName() + " not found!");
			console.sendMessage("Server will exit!");
			System.exit(1);
			return null;
		}
		
		World world = Schematic.toWorld(properties.getLevelName().getKey(), (CompoundTag) NBTUtil.read(schem).getTag());
		
		console.sendMessage("Loaded world " + properties.getLevelName() + "!");
		
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
		base = base.replace("%ONLINECLIENTS%", String.valueOf(getPlayers().size()));
		
		if (properties.getFavicon().isPresent()) {
			String icon = "\"favicon\":\"data:image/png;base64," + ImageUtils.imgToBase64String(properties.getFavicon().get(), "png") + "\",";
			base = base.replace("%FAVICON%", icon);
		} else {
			base = base.replace("%FAVICON%", "");
		}
		
		return base;
	}
	
	public void stopServer() {
		console.sendMessage("Stopping Server...");
		
		for (LimboPlugin plugin : Limbo.getInstance().getPluginManager().getPlugins()) {
			console.sendMessage("Disabling plugin " + plugin.getName() + " " + plugin.getInfo().getVersion());
			plugin.onDisable();
		}
		
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
		console.logs.close();
		System.exit(0);
	}
	
	public int getNextEntityId() {
		if (entityIdCount.get() == Integer.MAX_VALUE) {
			return entityIdCount.getAndSet(0);
		} else {
			return entityIdCount.getAndIncrement();
		}
	}
	
	public void dispatchCommand(CommandSender sender, String str) {
		String[] command;
		if (str.startsWith("/")) {
			command = CustomStringUtils.splitStringToArgs(str.substring(1));
		} else {
			command = CustomStringUtils.splitStringToArgs(str);
		}
		dispatchCommand(sender, command);
	}
	
	public void dispatchCommand(CommandSender sender, String... args) {
		try {
			Limbo.getInstance().getPluginManager().fireExecutors(sender, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
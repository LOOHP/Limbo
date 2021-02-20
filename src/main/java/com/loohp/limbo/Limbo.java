package com.loohp.limbo;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loohp.limbo.Commands.CommandSender;
import com.loohp.limbo.Events.EventsManager;
import com.loohp.limbo.File.ServerProperties;
import com.loohp.limbo.GUI.GUI;
import com.loohp.limbo.Location.Location;
import com.loohp.limbo.Metrics.Metrics;
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
import com.loohp.limbo.World.DimensionRegistry;
import com.loohp.limbo.World.Environment;
import com.loohp.limbo.World.Schematic;
import com.loohp.limbo.World.World;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;

public class Limbo {
	
	private static Limbo instance;
	public static boolean noGui = false;
	
	public static void main(String args[]) throws IOException, ParseException, NumberFormatException, ClassNotFoundException, InterruptedException {
		for (String flag : args) {
			if (flag.equals("--nogui") || flag.equals("nogui")) {
				noGui = true;
			} else if (flag.equals("--help")) {
				System.out.println("Accepted flags:");
				System.out.println(" --nogui <- Disable the GUI");
				System.exit(0);
			} else {
				System.out.println("Unknown flag: \"" + flag + "\". Ignoring...");
			}
		}
		if (GraphicsEnvironment.isHeadless()) {
			noGui = true;
		}
		if (!noGui) {
			System.out.println("Launching Server GUI.. Add \"--nogui\" in launch arguments to disable");
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
			    	GUI.main();
			    }
			});  
			t1.start();
		}
		
		new Limbo();
	}
	
	public static Limbo getInstance() {
		return instance;
	}
	
	//===========================
	
	public final String serverImplementationVersion = "1.16.5";
	public final int serverImplmentationProtocol = 754;
	public final String limboImplementationVersion;
	
	private AtomicBoolean isRunning;
	
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
	
	private File internalDataFolder;
	
	private DimensionRegistry dimensionRegistry;
	
	@SuppressWarnings("unused")
	private Tick tick;
	
	private Metrics metrics;
	
	public AtomicInteger entityIdCount = new AtomicInteger();
	
	@SuppressWarnings("deprecation")
	private Unsafe unsafe = new Unsafe();
	
	@SuppressWarnings("unchecked")
	public Limbo() throws IOException, ParseException, NumberFormatException, ClassNotFoundException, InterruptedException {
		instance = this;
		isRunning = new AtomicBoolean(true);
		
		if (!noGui) {
			while (!GUI.loadFinish) {
				TimeUnit.MILLISECONDS.sleep(500);
			}
			console = new Console(null, System.out, System.err);
		} else {
			console = new Console(System.in, System.out, System.err);
		}
				
		limboImplementationVersion = getLimboVersion();
		console.sendMessage("Loading Limbo Version " + limboImplementationVersion + " on Minecraft " + serverImplementationVersion);
		
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
        
        internalDataFolder = new File("internal_data");
        if (!internalDataFolder.exists()) {
        	internalDataFolder.mkdirs();
        }
		
        String mappingName = "mapping.json";
        File mappingFile = new File(internalDataFolder, mappingName);
        if (!mappingFile.exists()) {
        	try (InputStream in = getClass().getClassLoader().getResourceAsStream(mappingName)) {
                Files.copy(in, mappingFile.toPath());
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        
        console.sendMessage("Loading packet id mappings from mapping.json ...");
        
        InputStreamReader reader = new InputStreamReader(new FileInputStream(mappingFile), StandardCharsets.UTF_8);
        JSONObject json = (JSONObject) new JSONParser().parse(reader);
        reader.close();
        
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
		
		dimensionRegistry = new DimensionRegistry();
		
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
        
        File defaultCommandsJar = new File(pluginFolder, "LimboDefaultCmd.jar");
        defaultCommandsJar.delete();
        console.sendMessage("Downloading limbo default commands module from github...");
        ReadableByteChannel rbc = Channels.newChannel(new URL("https://github.com/LOOHP/Limbo/raw/master/modules/LimboDefaultCmd.jar").openStream());
	    FileOutputStream fos = new FileOutputStream(defaultCommandsJar);
	    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	    fos.close();
		
	    pluginManager = new PluginManager(pluginFolder);
	    try {
			Method loadPluginsMethod = PluginManager.class.getDeclaredMethod("loadPlugins");
			loadPluginsMethod.setAccessible(true);
			loadPluginsMethod.invoke(pluginManager);
			loadPluginsMethod.setAccessible(false);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		for (LimboPlugin plugin : Limbo.getInstance().getPluginManager().getPlugins()) {
			console.sendMessage("Enabling plugin " + plugin.getName() + " " + plugin.getInfo().getVersion());
			plugin.onEnable();
		}
		
		server = new ServerConnection(properties.getServerIp(), properties.getServerPort());
		
		tick = new Tick(this);
		
		metrics = new Metrics();
		
		console.run();
	}

	@Deprecated
	public Unsafe getUnsafe() {
		return unsafe;
	}

	public DimensionRegistry getDimensionRegistry() {
		return dimensionRegistry;
	}

	public PermissionsManager getPermissionsManager() {
		return permissionManager;
	}

	public File getInternalDataFolder() {
		return internalDataFolder;
	}

	public EventsManager getEventsManager() {
		return eventsManager;
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
		
		World world = Schematic.toWorld(properties.getLevelName().getKey(), Environment.fromNamespacedKey(properties.getLevelDimension()), (CompoundTag) NBTUtil.read(schem).getTag());
		
		console.sendMessage("Loaded world " + properties.getLevelName() + "!");
		
		return world;		
	}
	
	public void registerWorld(World world) {
		if (!worlds.contains(world)) {
			worlds.add(world);
		} else {
			throw new RuntimeException("World already registered");
		}
	}
	
	public void unregisterWorld(World world) {
		if (worlds.indexOf(world) == 0) {
			throw new RuntimeException("World already registered");
		} else if (!worlds.contains(world)) {
			throw new RuntimeException("World not registered");
		} else {
			for (Player player : world.getPlayers()) {
				player.teleport(properties.getWorldSpawn());
			}
			worlds.remove(world);
		}
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
	
	public Metrics getMetrics() {
		return metrics;
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
		playersByUUID.put(player.getUniqueId(), player);
	}
	
	public void removePlayer(Player player) {
		playersByName.remove(player.getName());
		playersByUUID.remove(player.getUniqueId());
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
	
	@SuppressWarnings("unchecked")
	public String buildServerListResponseJson(String version, int protocol, BaseComponent[] motd, int maxPlayers, int playersOnline, BufferedImage favicon) throws IOException {
		JSONObject json = new JSONObject();

		JSONObject versionJson = new JSONObject();
		versionJson.put("name", version);
		versionJson.put("protocol", protocol);
		json.put("version", versionJson);
		
		JSONObject playersJson = new JSONObject();
		playersJson.put("max", maxPlayers);
		playersJson.put("online", playersOnline);
		json.put("players", playersJson);
		
		json.put("description", "%MOTD%");
		
		if (favicon != null) {
			if (favicon.getWidth() == 64 && favicon.getHeight() == 64) {
				String base64 = "data:image/png;base64," + ImageUtils.imgToBase64String(favicon, "png");
				json.put("favicon", base64);
			} else {
				console.sendMessage("Server List Favicon must be 64 x 64 in size!");
			}
		}
		
		JSONObject modInfoJson = new JSONObject();
		modInfoJson.put("type", "FML");
		modInfoJson.put("modList", new JSONArray());
		json.put("modinfo", modInfoJson);
		
		
		TreeMap<String, Object> treeMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
    	treeMap.putAll(json);
    	
    	Gson g = new GsonBuilder().create();

    	return g.toJson(treeMap).replace("\"%MOTD%\"", ComponentSerializer.toString(motd));
	}
	
	public String buildLegacyPingResponse(String version, BaseComponent[] motd, int maxPlayers, int playersOnline) {
		String begin = "§1";
		return String.join("\00", begin, "127", version, String.join("", Arrays.asList(motd).stream().map(each -> each.toLegacyText()).collect(Collectors.toList())), String.valueOf(playersOnline), String.valueOf(maxPlayers));
	}
	
	public void stopServer() {
		isRunning.set(false);
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
		
		console.sendMessage("Server closed");
		console.logs.close();
		System.exit(0);
	}
	
	public boolean isRunning() {
		return isRunning.get();
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
	
	private String getLimboVersion() throws IOException {
		Enumeration<URL> manifests = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
		while (manifests.hasMoreElements()) {
			URL url = manifests.nextElement();
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			Optional<String> line = br.lines().filter(each -> each.startsWith("Limbo-Version:")).findFirst();
			br.close();
			if (line.isPresent()) {
				return line.get().substring(14).trim();
			}
		}
		return "Unknown";
	}

}

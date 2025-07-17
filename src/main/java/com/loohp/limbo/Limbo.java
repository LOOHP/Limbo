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

package com.loohp.limbo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loohp.limbo.bossbar.KeyedBossBar;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.commands.DefaultCommands;
import com.loohp.limbo.consolegui.GUI;
import com.loohp.limbo.events.EventsManager;
import com.loohp.limbo.file.ServerProperties;
import com.loohp.limbo.inventory.AnvilInventory;
import com.loohp.limbo.inventory.CustomInventory;
import com.loohp.limbo.inventory.Inventory;
import com.loohp.limbo.inventory.InventoryHolder;
import com.loohp.limbo.inventory.InventoryType;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.metrics.Metrics;
import com.loohp.limbo.network.ServerConnection;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutBoss;
import com.loohp.limbo.permissions.PermissionsManager;
import com.loohp.limbo.player.Player;
import com.loohp.limbo.plugins.LimboPlugin;
import com.loohp.limbo.plugins.PluginManager;
import com.loohp.limbo.scheduler.LimboScheduler;
import com.loohp.limbo.scheduler.Tick;
import com.loohp.limbo.utils.CustomStringUtils;
import com.loohp.limbo.utils.ImageUtils;
import com.loohp.limbo.utils.NetworkUtils;
import com.loohp.limbo.world.Environment;
import com.loohp.limbo.world.Schematic;
import com.loohp.limbo.world.World;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.swing.UnsupportedLookAndFeelException;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class Limbo {

	public static final String LIMBO_BRAND = "Limbo";

	private static Limbo instance;
	public static boolean noGui = false;
	
	public static void main(String[] args) throws IOException, ParseException, NumberFormatException, ClassNotFoundException, InterruptedException {
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
			Thread t1 = new Thread(() -> {
				try {
					GUI.main();
				} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
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
	
	public final String SERVER_IMPLEMENTATION_VERSION = "1.21.8";
	public final int SERVER_IMPLEMENTATION_PROTOCOL = 772;
	public final String LIMBO_IMPLEMENTATION_VERSION;
	
	private final AtomicBoolean isRunning;
	
	private final ServerConnection server;
	private final Console console;
	
	private final List<World> worlds = new CopyOnWriteArrayList<>();
	final Map<String, Player> playersByName = new ConcurrentHashMap<>();
	final Map<UUID, Player> playersByUUID = new ConcurrentHashMap<>();
	private final Map<Key, KeyedBossBar> bossBars = new ConcurrentHashMap<>();
	
	private final ServerProperties properties;
	
	private final PluginManager pluginManager;
	private final EventsManager eventsManager;
	private final PermissionsManager permissionManager;
	private final File pluginFolder;
	
	private final Tick tick;
	private final LimboScheduler scheduler;
	
	private final Metrics metrics;
	
	public final AtomicInteger entityIdCount = new AtomicInteger();
	
	@SuppressWarnings("deprecation")
	private Unsafe unsafe;
	
	public Limbo() throws IOException, ParseException, NumberFormatException, ClassNotFoundException, InterruptedException {
		instance = this;
		unsafe = new Unsafe(this);
		isRunning = new AtomicBoolean(true);
		
		if (!noGui) {
			while (!GUI.loadFinish) {
				TimeUnit.MILLISECONDS.sleep(500);
			}
			console = new Console(null, System.out, System.err);
		} else {
			console = new Console(System.in, System.out, System.err);
		}
				
		LIMBO_IMPLEMENTATION_VERSION = getLimboVersion();
		console.sendMessage("Loading Limbo Version " + LIMBO_IMPLEMENTATION_VERSION + " on Minecraft " + SERVER_IMPLEMENTATION_VERSION);
		
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
		
		worlds.add(loadDefaultWorld());
		Location spawn = properties.getWorldSpawn();
		properties.setWorldSpawn(new Location(getWorld(properties.getLevelName().value()), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch()));
		
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

        scheduler = new LimboScheduler();
		tick = new Tick(this);
        
        permissionManager = new PermissionsManager();
        permissionManager.loadDefaultPermissionFile(permissionFile);     
        
        eventsManager = new EventsManager();
        
        pluginFolder = new File("plugins");
        pluginFolder.mkdirs();
		
	    pluginManager = new PluginManager(new DefaultCommands(), pluginFolder);
	    try {
			Method loadPluginsMethod = PluginManager.class.getDeclaredMethod("loadPlugins");
			loadPluginsMethod.setAccessible(true);
			loadPluginsMethod.invoke(pluginManager);
			loadPluginsMethod.setAccessible(false);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		for (LimboPlugin plugin : Limbo.getInstance().getPluginManager().getPlugins()) {
			try {
				console.sendMessage("Enabling plugin " + plugin.getName() + " " + plugin.getInfo().getVersion());
				plugin.onEnable();
			} catch (Throwable e) {
				new RuntimeException("Error while enabling " + plugin.getName() + " " + plugin.getInfo().getVersion(), e).printStackTrace();
			}
		}
		
		server = new ServerConnection(properties.getServerIp(), properties.getServerPort(), false);
		
		metrics = new Metrics();
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Limbo.getInstance().terminate();
		}));

		console.run();
	}

	@Deprecated
	public Unsafe getUnsafe() {
		return unsafe;
	}
	
	public Tick getHeartBeat() {
		return tick;
	}
	
	public LimboScheduler getScheduler() {
		return scheduler;
	}

	public PermissionsManager getPermissionsManager() {
		return permissionManager;
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
			console.sendMessage("Creating default world...");
	        try (InputStream in = Limbo.class.getClassLoader().getResourceAsStream("spawn.schem")) {
	        	Files.copy(in, schem.toPath());
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
		}
		
		try {
			World world = Schematic.toWorld(properties.getLevelName().value(), Environment.fromKey(properties.getLevelDimension()), (CompoundTag) NBTUtil.read(schem).getTag());
			console.sendMessage("Loaded world " + properties.getLevelName() + "!");		
			return world;
		} catch (Throwable e) {
			console.sendMessage("Unable to load world " + properties.getSchemFileName() + "!");
			e.printStackTrace();
			console.sendMessage("Server will exit!");
			System.exit(1);
			return null;
		}
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

	public KeyedBossBar createBossBar(Key Key, Component name, float progress, BossBar.Color color, BossBar.Overlay overlay, BossBar.Flag... flags) {
		KeyedBossBar keyedBossBar = com.loohp.limbo.bossbar.Unsafe.a(Key, BossBar.bossBar(name, progress, color, overlay, new HashSet<>(Arrays.asList(flags))));
		bossBars.put(Key, keyedBossBar);
		return keyedBossBar;
	}

	public void removeBossBar(Key Key) {
		KeyedBossBar keyedBossBar = bossBars.remove(Key);
		keyedBossBar.getProperties().removeListener(keyedBossBar.getUnsafe().a());
		keyedBossBar.getUnsafe().b();
		PacketPlayOutBoss packetPlayOutBoss = new PacketPlayOutBoss(keyedBossBar, PacketPlayOutBoss.BossBarAction.REMOVE);
		for (Player player : keyedBossBar.getPlayers()) {
			try {
				player.clientConnection.sendPacket(packetPlayOutBoss);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Map<Key, KeyedBossBar> getBossBars() {
		return Collections.unmodifiableMap(bossBars);
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
	public String buildServerListResponseJson(String version, int protocol, Component motd, int maxPlayers, int playersOnline, BufferedImage favicon) throws IOException {
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
		
		
		TreeMap<String, Object> treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    	treeMap.putAll(json);
    	
    	Gson g = new GsonBuilder().create();

    	return g.toJson(treeMap).replace("\"%MOTD%\"", GsonComponentSerializer.gson().serialize(motd));
	}
	
	public String buildLegacyPingResponse(String version, Component motd, int maxPlayers, int playersOnline) {
		String begin = "�1";
		return String.join("\00", begin, "127", version, String.join("", Arrays.asList(motd).stream().map(each -> LegacyComponentSerializer.legacySection().serialize(each)).collect(Collectors.toList())), String.valueOf(playersOnline), String.valueOf(maxPlayers));
	}
	
	protected void terminate() {
		isRunning.set(false);
		console.sendMessage("Stopping Server...");
		
		for (LimboPlugin plugin : Limbo.getInstance().getPluginManager().getPlugins()) {
			try {
				console.sendMessage("Disabling plugin " + plugin.getName() + " " + plugin.getInfo().getVersion());
				plugin.onDisable();
			} catch (Throwable e) {
				new RuntimeException("Error while disabling " + plugin.getName() + " " + plugin.getInfo().getVersion(), e).printStackTrace();
			}
		}
		
		tick.waitAndKillThreads(5000);
		
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
	}
	
	public void stopServer() {
		System.exit(0);
	}
	
	public boolean isRunning() {
		return isRunning.get();
	}
	
	public int getNextEntityId() {
		return entityIdCount.getAndUpdate(i -> i == Integer.MAX_VALUE ? 0 : ++i);
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
			try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
				Optional<String> line = br.lines().filter(each -> each.startsWith("Limbo-Version:")).findFirst();
				if (line.isPresent()) {
					return line.get().substring(14).trim();
				}
			}
		}
		return "Unknown";
	}

	public Inventory createInventory(Component title, int slots, InventoryHolder holder) {
		return CustomInventory.create(title, slots, holder);
	}

	public Inventory createInventory(InventoryType type, InventoryHolder holder) {
		return createInventory(null, type, holder);
	}

	public Inventory createInventory(Component title, InventoryType type, InventoryHolder holder) {
		if (!type.isCreatable()) {
			throw new UnsupportedOperationException("This InventoryType cannot be created.");
		}
		switch (type) {
			case ANVIL:
				return new AnvilInventory(title, holder);
			default:
				throw new UnsupportedOperationException("This InventoryType has not been implemented yet.");
		}
	}

}

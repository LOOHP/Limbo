package com.loohp.limbo.File;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import javax.imageio.ImageIO;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.Location.Location;
import com.loohp.limbo.Utils.GameMode;
import com.loohp.limbo.Utils.NamespacedKey;
import com.loohp.limbo.World.World;

public class ServerProperties {

	public static final String JSON_BASE_RESPONSE = "{\"version\":{\"name\":\"%VERSION%\",\"protocol\":%PROTOCOL%},\"players\":{\"max\":%MAXPLAYERS%,\"online\":%ONLINECLIENTS%},\"description\":%MOTD%,%FAVICON%\"modinfo\":{\"type\":\"FML\",\"modList\":[]}}";

	File file;
	int maxPlayers;
	int serverPort;
	String serverIp;
	NamespacedKey levelName;
	String schemFileName;
	NamespacedKey levelDimension;
	GameMode defaultGamemode;
	Location worldSpawn;
	boolean reducedDebugInfo;
	boolean allowFlight;
	String motdJson;
	String versionString;
	int protocol;
	
	Optional<BufferedImage> favicon;

	public ServerProperties(File file) throws IOException {
		this.file = file;
		Properties prop = new Properties();
		prop.load(new FileInputStream(file));

		protocol = 736;

		maxPlayers = Integer.parseInt(prop.getProperty("max-players"));
		serverPort = Integer.parseInt(prop.getProperty("server-port"));
		serverIp = prop.getProperty("server-ip");
		String[] level = prop.getProperty("level-name").split(";");
		levelName = new NamespacedKey(level[0]);
		schemFileName = level[1];
		levelDimension = new NamespacedKey(prop.getProperty("level-dimension"));
		defaultGamemode = GameMode.fromName(new NamespacedKey(prop.getProperty("default-gamemode")).getKey());
		String[] locStr = prop.getProperty("world-spawn").split(";");
		World world = Limbo.getInstance().getWorld(locStr[0]);
		double x = Double.parseDouble(locStr[1]);
		double y = Double.parseDouble(locStr[2]);
		double z = Double.parseDouble(locStr[3]);
		float yaw = Float.parseFloat(locStr[4]);
		float pitch = Float.parseFloat(locStr[5]);
		worldSpawn = new Location(world, x, y, z, yaw, pitch);
		reducedDebugInfo = Boolean.parseBoolean(prop.getProperty("reduced-debug-info"));
		allowFlight = Boolean.parseBoolean(prop.getProperty("allow-flight"));
		motdJson = prop.getProperty("motd");
		versionString = prop.getProperty("version");
		
		File png = new File("server-icon.png");
		if (png.exists()) {
			try {
				BufferedImage image = ImageIO.read(png);
				if (image.getHeight() == 64 && image.getWidth() == 64) {
					favicon = Optional.of(image);
				} else {
					System.out.println("Unable to load server-icon.png! The image is not 64 x 64 in size!");
				}
			} catch (Exception e) {
				System.out.println("Unable to load server-icon.png! Is it a png image?");
			}
		} else {
			System.out.println("No server-icon.png found");
			favicon = Optional.empty();
		}

		System.out.println("Loaded server.properties");
	}

	public Optional<BufferedImage> getFavicon() {
		return favicon;
	}

	public File getFile() {
		return file;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public int getServerPort() {
		return serverPort;
	}

	public String getServerIp() {
		return serverIp;
	}

	public NamespacedKey getLevelName() {
		return levelName;
	}

	public String getSchemFileName() {
		return schemFileName;
	}

	public NamespacedKey getLevelDimension() {
		return levelDimension;
	}

	public GameMode getDefaultGamemode() {
		return defaultGamemode;
	}

	public Location getWorldSpawn() {
		return worldSpawn;
	}

	public void setWorldSpawn(Location location) {
		this.worldSpawn = location;
	}

	public boolean isReducedDebugInfo() {
		return reducedDebugInfo;
	}

	public boolean isAllowFlight() {
		return allowFlight;
	}

	public static String getJsonBaseResponse() {
		return JSON_BASE_RESPONSE;
	}

	public String getMotdJson() {
		return motdJson;
	}

	public String getVersionString() {
		return versionString;
	}

	public int getProtocol() {
		return protocol;
	}

}

package com.loohp.limbo.plugins;

import java.io.File;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.file.FileConfiguration;

public class LimboPlugin {

	private String name;
	private File dataFolder;
	private PluginInfo info;
	private File pluginJar;
	
	protected final void setInfo(FileConfiguration file, File pluginJar) {
		this.info = new PluginInfo(file);
		this.name = info.getName();
		this.dataFolder = new File(Limbo.getInstance().getPluginFolder(), name);
		this.pluginJar = pluginJar;
	}
	
	protected final File getPluginJar() {
		return pluginJar;
	}

	public void onLoad() {

	}

	public void onEnable() {

	}

	public void onDisable() {

	}

	public final String getName() {
		return name;
	}

	public final File getDataFolder() {
		return new File(dataFolder.getAbsolutePath());
	}
	
	public final PluginInfo getInfo() {
		return info;
	}
	
	public final Limbo getServer() {
		return Limbo.getInstance();
	}

}

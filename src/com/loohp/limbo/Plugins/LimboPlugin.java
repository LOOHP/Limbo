package com.loohp.limbo.Plugins;

import java.io.File;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.File.FileConfiguration;

public class LimboPlugin {

	private String name;
	private File dataFolder;
	private PluginInfo info;
	
	protected final void setInfo(FileConfiguration file) {
		info = new PluginInfo(file);
		name = info.getName();
		dataFolder = new File(Limbo.getInstance().getPluginFolder(), name);
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

package com.loohp.limbo.Plugins;

import java.io.File;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.File.FileConfiguration;

public class LimboPlugin {

	private String name;
	private File dataFolder;
	private PluginInfo info;
	
	public final void setInfo(FileConfiguration file) {
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

	public String getName() {
		return name;
	}

	public File getDataFolder() {
		return new File(dataFolder.getAbsolutePath());
	}
	
	public PluginInfo getInfo() {
		return info;
	}
	
	public Limbo getServer() {
		return Limbo.getInstance();
	}

}

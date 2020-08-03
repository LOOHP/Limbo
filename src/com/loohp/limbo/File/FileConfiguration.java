package com.loohp.limbo.File;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class FileConfiguration {
	
	File file;
	Map<String, Object> mapping;
	
	public FileConfiguration(File file) throws FileNotFoundException {
		this.file = file;
		reloadConfig();
	}
	
	public FileConfiguration reloadConfig() {
		try {
			Yaml yml = new Yaml();
			mapping = yml.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> returnType) {
		try {
			String[] tree = key.split("\\.");
			Map<String, Object> map = mapping;
			for (int i = 0; i < tree.length - 1; i++) {
				map = (Map<String, Object>) map.get(tree[i]);
			}
			return returnType.cast(map.get(tree[tree.length - 1]));
		} catch (Exception e) {
			return null;
		}
	}

}

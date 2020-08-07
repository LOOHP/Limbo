package com.loohp.limbo.File;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class FileConfiguration {
	
	Map<String, Object> mapping;
	
	public FileConfiguration(File file) throws FileNotFoundException {
		reloadConfig(new FileInputStream(file));
	}
	
	public FileConfiguration(InputStream input){
		reloadConfig(input);
	}
	
	public FileConfiguration reloadConfig(InputStream input) {
		Yaml yml = new Yaml();
		mapping = yml.load(input);
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
			if (returnType.equals(String.class)) {
				return (T) map.get(tree[tree.length - 1]).toString();
			}
			return returnType.cast(map.get(tree[tree.length - 1]));
		} catch (Exception e) {
			return null;
		}
	}

}

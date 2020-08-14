package com.loohp.limbo.World;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.Utils.CustomNBTUtils;

import net.querz.nbt.tag.CompoundTag;

public class DimensionRegistry {
	
	private CompoundTag defaultTag;
	private CompoundTag codec;
	private File reg;
	
	public DimensionRegistry() {
		this.defaultTag = new CompoundTag();
		
		String name = "dimension_registry.json";
        File file = new File(Limbo.getInstance().getInternalDataFolder(), name);
        if (!file.exists()) {
        	try (InputStream in = Limbo.class.getClassLoader().getResourceAsStream(name)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        
        this.reg = file;
        
        try {
			JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(reg));
			CompoundTag tag = CustomNBTUtils.getCompoundTagFromJson((JSONObject) json.get("value"));
			defaultTag = tag;
			codec = defaultTag.clone();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	public File getFile() {
		return reg;
	}
	
	public void resetCodec() {
		codec = defaultTag.clone();
	}
	
	public CompoundTag getCodec() {		
		return codec;
	}

}

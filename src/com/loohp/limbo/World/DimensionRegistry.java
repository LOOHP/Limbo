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
	
	public static CompoundTag defaultTag;
	private static File reg;
	
	static {
		String reg = "dimension_registry.json";
        File file = new File(Limbo.getInstance().getInternalDataFolder(), reg);
        if (!file.exists()) {
        	try (InputStream in = Limbo.class.getClassLoader().getResourceAsStream(reg)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        
        DimensionRegistry.reg = file;

		resetTag();
	}
	
	public static void resetTag() {		
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(reg));
			CompoundTag tag = CustomNBTUtils.getCompoundTagFromJson((JSONObject) json.get("value"));
			defaultTag = tag;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static CompoundTag getCodec() {		
		return defaultTag;
	}

}

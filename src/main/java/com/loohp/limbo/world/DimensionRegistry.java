package com.loohp.limbo.world;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.utils.CustomNBTUtils;
import net.querz.nbt.tag.CompoundTag;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class DimensionRegistry {

    private CompoundTag defaultTag;
    private CompoundTag codec;
    private final File reg;

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

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(reg), StandardCharsets.UTF_8)) {
            JSONObject json = (JSONObject) new JSONParser().parse(reader);
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

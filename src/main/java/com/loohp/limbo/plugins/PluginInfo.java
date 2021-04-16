package com.loohp.limbo.plugins;

import com.loohp.limbo.file.FileConfiguration;

public class PluginInfo {

    private final String name;
    private final String description;
    private final String author;
    private final String version;
    private final String main;

    public PluginInfo(FileConfiguration file) {
        name = file.get("name", String.class);
        description = file.get("description", String.class) == null ? "" : file.get("description", String.class);
        author = file.get("author", String.class);
        version = file.get("version", String.class);
        main = file.get("main", String.class);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getVersion() {
        return version;
    }

    public String getMainClass() {
        return main;
    }

}

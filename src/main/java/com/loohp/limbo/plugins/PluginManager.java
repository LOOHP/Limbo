package com.loohp.limbo.plugins;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandExecutor;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.commands.TabCompletor;
import com.loohp.limbo.file.FileConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PluginManager {

    private final Map<String, LimboPlugin> plugins;
    private final List<Executor> executors;
    private final File pluginFolder;

    public PluginManager(File pluginFolder) {
        this.pluginFolder = pluginFolder;
        this.executors = new ArrayList<>();
        this.plugins = new LinkedHashMap<>();
    }

    protected void loadPlugins() {
        for (File file : pluginFolder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                boolean found = false;
                try (ZipInputStream zip = new ZipInputStream(new FileInputStream(file))) {
                    while (true) {
                        ZipEntry entry = zip.getNextEntry();
                        if (entry == null) {
                            break;
                        }
                        String name = entry.getName();
                        if (name.endsWith("plugin.yml") || name.endsWith("limbo.yml")) {
                            found = true;

                            FileConfiguration pluginYaml = new FileConfiguration(zip);
                            String main = pluginYaml.get("main", String.class);
                            String pluginName = pluginYaml.get("name", String.class);

                            if (plugins.containsKey(pluginName)) {
                                System.err.println("Ambiguous plugin name in " + file.getName() + " with the plugin \"" + plugins.get(pluginName).getClass().getName() + "\"");
                                break;
                            }
                            URLClassLoader child = new URLClassLoader(new URL[]{file.toURI().toURL()}, Limbo.getInstance().getClass().getClassLoader());
                            Class<?> clazz = Class.forName(main, true, child);
                            LimboPlugin plugin = (LimboPlugin) clazz.getDeclaredConstructor().newInstance();
                            plugin.setInfo(pluginYaml, file);
                            plugins.put(plugin.getName(), plugin);
                            plugin.onLoad();
                            Limbo.getInstance().getConsole().sendMessage("Loading plugin " + file.getName() + " " + plugin.getInfo().getVersion() + " by " + plugin.getInfo().getAuthor());
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Unable to load plugin \"" + file.getName() + "\"");
                    e.printStackTrace();
                }
                if (!found) {
                    System.err.println("Jar file " + file.getName() + " has no plugin.yml!");
                }
            }
        }
    }

    public List<LimboPlugin> getPlugins() {
        return new ArrayList<>(plugins.values());
    }

    public LimboPlugin getPlugin(String name) {
        return plugins.get(name);
    }

    public void fireExecutors(CommandSender sender, String[] args) throws Exception {
        Limbo.getInstance().getConsole().sendMessage(sender.getName() + " executed server command: /" + String.join(" ", args));
        for (Executor entry : executors) {
            try {
                entry.executor.execute(sender, args);
            } catch (Exception e) {
                System.err.println("Error while passing command \"" + args[0] + "\" to the plugin \"" + entry.plugin.getName() + "\"");
                e.printStackTrace();
            }
        }
    }

    public List<String> getTabOptions(CommandSender sender, String[] args) {
        List<String> options = new ArrayList<>();
        for (Executor entry : executors) {
            if (entry.tab.isPresent()) {
                try {
                    options.addAll(entry.tab.get().tabComplete(sender, args));
                } catch (Exception e) {
                    System.err.println("Error while passing tab completion to the plugin \"" + entry.plugin.getName() + "\"");
                    e.printStackTrace();
                }
            }
        }
        return options;
    }

    public void registerCommands(LimboPlugin plugin, CommandExecutor executor) {
        executors.add(new Executor(plugin, executor));
    }

    public void unregsiterAllCommands(LimboPlugin plugin) {
        executors.removeIf(each -> each.plugin.equals(plugin));
    }

    public File getPluginFolder() {
        return new File(pluginFolder.getAbsolutePath());
    }

    protected static class Executor {
        public LimboPlugin plugin;
        public CommandExecutor executor;
        public Optional<TabCompletor> tab;

        public Executor(LimboPlugin plugin, CommandExecutor executor) {
            this.plugin = plugin;
            this.executor = executor;
            if (executor instanceof TabCompletor) {
                this.tab = Optional.of((TabCompletor) executor);
            } else {
                this.tab = Optional.empty();
            }
        }
    }

}

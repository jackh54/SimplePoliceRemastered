package com.voidcity.simplepolice.config;

import com.voidcity.simplepolice.SimplePolicePlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {
    private final SimplePolicePlugin plugin;
    private final File configFile;
    private CommentedConfigurationNode config;
    private YamlConfigurationLoader loader;

    // Default configuration values
    private boolean enableJail = true;
    private int jailTime = 300;
    private double fine = 100.0;
    private List<String> policeCommands = Arrays.asList(
        "police add",
        "police remove",
        "police list"
    );
    private List<String> bannedItems = Arrays.asList(
        "DIAMOND_SWORD",
        "NETHERITE_SWORD"
    );

    public ConfigManager(SimplePolicePlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    public void loadConfig() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            if (!configFile.exists()) {
                Files.copy(plugin.getResource("config.yml"), configFile.toPath());
            }

            this.loader = YamlConfigurationLoader.builder()
                .file(configFile)
                .build();

            this.config = loader.load();
            loadValues();
        } catch (IOException e) {
            plugin.getLogger().severe("Could not load config.yml: " + e.getMessage());
        }
    }

    private void loadValues() {
        try {
            this.enableJail = config.node("settings", "enable-jail").getBoolean(true);
            this.jailTime = config.node("settings", "jail-time").getInt(300);
            this.fine = config.node("settings", "fine").getDouble(100.0);
            this.policeCommands = config.node("commands", "police").getList(String.class, policeCommands);
            this.bannedItems = config.node("items", "banned").getList(String.class, bannedItems);
        } catch (SerializationException e) {
            plugin.getLogger().severe("Error loading configuration values: " + e.getMessage());
        }
    }

    public void saveConfig() {
        try {
            config.node("settings", "enable-jail").set(enableJail);
            config.node("settings", "jail-time").set(jailTime);
            config.node("settings", "fine").set(fine);
            config.node("commands", "police").set(policeCommands);
            config.node("items", "banned").set(bannedItems);

            loader.save(config);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config.yml: " + e.getMessage());
        }
    }

    public ConfigurationNode getNode(String... path) {
        return config.node((Object[]) path);
    }

    // Getters and setters
    public boolean isJailEnabled() {
        return enableJail;
    }

    public void setJailEnabled(boolean enableJail) {
        this.enableJail = enableJail;
        saveConfig();
    }

    public int getJailTime() {
        return jailTime;
    }

    public void setJailTime(int jailTime) {
        this.jailTime = jailTime;
        saveConfig();
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
        saveConfig();
    }

    public List<String> getPoliceCommands() {
        return policeCommands;
    }

    public List<String> getBannedItems() {
        return bannedItems;
    }
} 
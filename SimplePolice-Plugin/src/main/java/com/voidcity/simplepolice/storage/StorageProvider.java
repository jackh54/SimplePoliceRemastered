package com.voidcity.simplepolice.storage;

import com.voidcity.simplepolice.SimplePolicePlugin;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class StorageProvider {
    private final SimplePolicePlugin plugin;
    private final File dataFile;
    private YamlConfiguration data;

    public StorageProvider(SimplePolicePlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        loadData();
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) {
                plugin.saveResource("data.yml", false);
            }
            this.data = YamlConfiguration.loadConfiguration(dataFile);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load data file", e);
            this.data = new YamlConfiguration();
        }
    }

    private void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save data file", e);
        }
    }

    public Set<UUID> loadPoliceOfficers() {
        Set<UUID> officers = new HashSet<>();
        for (String uuidStr : data.getStringList("police-officers")) {
            try {
                officers.add(UUID.fromString(uuidStr));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in data file: " + uuidStr);
            }
        }
        return officers;
    }

    public void savePoliceOfficer(UUID officerId) {
        Set<String> officers = new HashSet<>(data.getStringList("police-officers"));
        officers.add(officerId.toString());
        data.set("police-officers", new ArrayList<>(officers));
        saveData();
    }

    public void removePoliceOfficer(UUID officerId) {
        Set<String> officers = new HashSet<>(data.getStringList("police-officers"));
        officers.remove(officerId.toString());
        data.set("police-officers", new ArrayList<>(officers));
        saveData();
    }

    public Location loadJailLocation() {
        if (!data.contains("jail-location")) {
            return null;
        }

        try {
            return new Location(
                plugin.getServer().getWorld(data.getString("jail-location.world")),
                data.getDouble("jail-location.x"),
                data.getDouble("jail-location.y"),
                data.getDouble("jail-location.z"),
                (float) data.getDouble("jail-location.yaw"),
                (float) data.getDouble("jail-location.pitch")
            );
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load jail location: " + e.getMessage());
            return null;
        }
    }

    public void saveJailLocation(Location location) {
        data.set("jail-location.world", location.getWorld().getName());
        data.set("jail-location.x", location.getX());
        data.set("jail-location.y", location.getY());
        data.set("jail-location.z", location.getZ());
        data.set("jail-location.yaw", location.getYaw());
        data.set("jail-location.pitch", location.getPitch());
        saveData();
    }

    public void close() {
        saveData();
    }
} 
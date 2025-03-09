package com.voidcity.simplepolice.service;

import com.voidcity.simplepolice.SimplePolicePlugin;
import com.voidcity.simplepolice.storage.StorageProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PoliceService {
    private final SimplePolicePlugin plugin;
    private final StorageProvider storageProvider;
    private final Map<UUID, Long> arrestCooldowns;
    private final Set<UUID> policeOfficers;
    private Location jailLocation;

    public PoliceService(SimplePolicePlugin plugin, StorageProvider storageProvider) {
        this.plugin = plugin;
        this.storageProvider = storageProvider;
        this.arrestCooldowns = new ConcurrentHashMap<>();
        this.policeOfficers = ConcurrentHashMap.newKeySet();
        loadPoliceOfficers();
        loadJailLocation();
    }

    private void loadPoliceOfficers() {
        policeOfficers.clear();
        policeOfficers.addAll(storageProvider.loadPoliceOfficers());
    }

    private void loadJailLocation() {
        this.jailLocation = storageProvider.loadJailLocation();
    }

    public boolean isPoliceOfficer(UUID playerId) {
        return policeOfficers.contains(playerId);
    }

    public void addPoliceOfficer(Player player) {
        UUID playerId = player.getUniqueId();
        if (policeOfficers.add(playerId)) {
            storageProvider.savePoliceOfficer(playerId);
            givePoliceEquipment(player);
            
            plugin.adventure().player(player).sendMessage(
                Component.text("You are now a police officer!")
                    .color(NamedTextColor.GREEN)
            );
        }
    }

    public void removePoliceOfficer(Player player) {
        UUID playerId = player.getUniqueId();
        if (policeOfficers.remove(playerId)) {
            storageProvider.removePoliceOfficer(playerId);
            
            plugin.adventure().player(player).sendMessage(
                Component.text("You are no longer a police officer.")
                    .color(NamedTextColor.RED)
            );
        }
    }

    public void arrestPlayer(Player officer, Player criminal) {
        if (!canArrest(officer, criminal)) {
            return;
        }

        // Apply arrest cooldown
        arrestCooldowns.put(criminal.getUniqueId(), 
            System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(
                plugin.getConfigManager().getJailTime()
            )
        );

        // Handle fine
        if (plugin.getConfigManager().getFine() > 0) {
            // Integrate with Vault for economy
            // Implementation depends on economy plugin
        }

        // Teleport to jail if enabled and location is set
        if (plugin.getConfigManager().isJailEnabled() && jailLocation != null) {
            criminal.teleport(jailLocation);
        }

        // Confiscate banned items
        confiscateBannedItems(criminal);

        // Notify players
        plugin.adventure().player(officer).sendMessage(
            Component.text("You have arrested ")
                .color(NamedTextColor.GREEN)
                .append(Component.text(criminal.getName()))
                .append(Component.text("!"))
        );

        plugin.adventure().player(criminal).sendMessage(
            Component.text("You have been arrested by ")
                .color(NamedTextColor.RED)
                .append(Component.text(officer.getName()))
                .append(Component.text("!"))
        );

        // Broadcast if enabled
        ConfigurationNode broadcastNode = plugin.getConfigManager().getNode("broadcast", "arrest");
        if (broadcastNode.getBoolean(true)) {
            plugin.adventure().all().sendMessage(
                Component.text(criminal.getName())
                    .color(NamedTextColor.RED)
                    .append(Component.text(" has been arrested by "))
                    .append(Component.text(officer.getName()))
                    .append(Component.text("!"))
            );
        }
    }

    private boolean canArrest(Player officer, Player criminal) {
        if (!isPoliceOfficer(officer.getUniqueId())) {
            plugin.adventure().player(officer).sendMessage(
                Component.text("You are not a police officer!")
                    .color(NamedTextColor.RED)
            );
            return false;
        }

        if (isPoliceOfficer(criminal.getUniqueId())) {
            plugin.adventure().player(officer).sendMessage(
                Component.text("You cannot arrest other police officers!")
                    .color(NamedTextColor.RED)
            );
            return false;
        }

        Long cooldownTime = arrestCooldowns.get(criminal.getUniqueId());
        if (cooldownTime != null && cooldownTime > System.currentTimeMillis()) {
            plugin.adventure().player(officer).sendMessage(
                Component.text("This player was recently arrested!")
                    .color(NamedTextColor.RED)
            );
            return false;
        }

        return true;
    }

    private void confiscateBannedItems(Player criminal) {
        List<String> bannedItems = plugin.getConfigManager().getBannedItems();
        ItemStack[] inventory = criminal.getInventory().getContents();
        
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && bannedItems.contains(item.getType().name())) {
                criminal.getInventory().setItem(i, null);
            }
        }
    }

    private void givePoliceEquipment(Player officer) {
        ConfigurationNode equipmentNode = plugin.getConfigManager().getNode("police.equipment");
        try {
            if (!equipmentNode.node("enabled").getBoolean(true)) {
                return;
            }

            List<String> equipment = equipmentNode.node("items").getList(String.class, new ArrayList<>());

            for (String item : equipment) {
                String[] parts = item.split(":");
                String itemName = parts[0];
                int amount = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;

                ItemStack itemStack = new ItemStack(
                    org.bukkit.Material.valueOf(itemName),
                    amount
                );
                
                officer.getInventory().addItem(itemStack);
            }
        } catch (SerializationException e) {
            plugin.getLogger().warning("Error reading equipment configuration: " + e.getMessage());
        }
    }

    public void setJailLocation(Location location) {
        this.jailLocation = location;
        storageProvider.saveJailLocation(location);
        
        plugin.adventure().all().sendMessage(
            Component.text("Jail location has been set!")
                .color(NamedTextColor.GREEN)
        );
    }

    public Location getJailLocation() {
        return jailLocation;
    }

    public Set<UUID> getPoliceOfficers() {
        return Collections.unmodifiableSet(policeOfficers);
    }
} 
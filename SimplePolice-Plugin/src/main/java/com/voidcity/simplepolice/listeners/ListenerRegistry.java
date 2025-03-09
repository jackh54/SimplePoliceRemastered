package com.voidcity.simplepolice.listeners;

import com.voidcity.simplepolice.SimplePolicePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class ListenerRegistry implements Listener {
    private final SimplePolicePlugin plugin;

    public ListenerRegistry(SimplePolicePlugin plugin) {
        this.plugin = plugin;
    }

    public void registerListeners() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || !(event.getRightClicked() instanceof Player target)) {
            return;
        }

        Player officer = event.getPlayer();
        if (!plugin.getPoliceService().isPoliceOfficer(officer.getUniqueId())) {
            return;
        }

        // Check if the officer is holding the arrest tool (configurable)
        ConfigurationNode itemsNode = plugin.getConfigManager().getNode("items");
        String arrestTool = itemsNode.node("arrest-tool").getString("STICK");
        if (officer.getInventory().getItemInMainHand().getType().name().equals(arrestTool)) {
            event.setCancelled(true);
            plugin.getPoliceService().arrestPlayer(officer, target);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager) || !(event.getEntity() instanceof Player victim)) {
            return;
        }

        // Check friendly fire between police officers
        ConfigurationNode policeNode = plugin.getConfigManager().getNode("police");
        if (plugin.getPoliceService().isPoliceOfficer(damager.getUniqueId()) &&
            plugin.getPoliceService().isPoliceOfficer(victim.getUniqueId()) &&
            !policeNode.node("friendly-fire").getBoolean(false)) {
            
            event.setCancelled(true);
            plugin.adventure().player(damager).sendMessage(
                Component.text("You cannot damage other police officers!")
                    .color(NamedTextColor.RED)
            );
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // If player is a police officer, send them a welcome message
        if (plugin.getPoliceService().isPoliceOfficer(player.getUniqueId())) {
            plugin.adventure().player(player).sendMessage(
                Component.text("Welcome back, Officer!")
                    .color(NamedTextColor.GOLD)
            );
        }
    }
} 
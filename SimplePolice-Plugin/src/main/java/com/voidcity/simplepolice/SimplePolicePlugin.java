package com.voidcity.simplepolice;

import cloud.commandframework.CommandManager;
import cloud.commandframework.CommandTree;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.voidcity.simplepolice.commands.CommandRegistry;
import com.voidcity.simplepolice.config.ConfigManager;
import com.voidcity.simplepolice.listeners.ListenerRegistry;
import com.voidcity.simplepolice.service.PoliceService;
import com.voidcity.simplepolice.storage.StorageProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.function.Function;

public class SimplePolicePlugin extends JavaPlugin {
    private static SimplePolicePlugin instance;
    private BukkitAudiences adventure;
    private ConfigManager configManager;
    private CommandManager<CommandSender> commandManager;
    private PoliceService policeService;
    private StorageProvider storageProvider;

    @Override
    public void onEnable() {
        instance = this;
        
        try {
            // Initialize Adventure for modern text components
            this.adventure = BukkitAudiences.create(this);
            
            // Load configuration
            this.configManager = new ConfigManager(this);
            this.configManager.loadConfig();
            
            // Initialize storage
            this.storageProvider = new StorageProvider(this);
            
            // Initialize services
            this.policeService = new PoliceService(this, storageProvider);
            
            // Setup command manager
            Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                CommandExecutionCoordinator.simpleCoordinator();
            this.commandManager = new PaperCommandManager<>(
                this,
                executionCoordinatorFunction,
                Function.identity(),
                Function.identity()
            );
            new CommandRegistry(this).registerCommands();
            
            // Register event listeners
            new ListenerRegistry(this).registerListeners();
            
            getLogger().info("SimplePolice has been enabled!");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to enable SimplePolice", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        
        if (this.storageProvider != null) {
            this.storageProvider.close();
        }
        
        instance = null;
    }

    public static SimplePolicePlugin getInstance() {
        return instance;
    }

    public BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    public PoliceService getPoliceService() {
        return policeService;
    }

    public StorageProvider getStorageProvider() {
        return storageProvider;
    }
} 
package com.voidcity.simplepolice.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.DoubleArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import com.voidcity.simplepolice.SimplePolicePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRegistry {
    private final SimplePolicePlugin plugin;
    private final CommandManager<CommandSender> manager;

    public CommandRegistry(SimplePolicePlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getCommandManager();
    }

    public void registerCommands() {
        registerPoliceCommands();
        registerArrestCommand();
        registerJailCommands();
        registerFineCommand();
        registerReloadCommand();
    }

    private void registerPoliceCommands() {
        Command.Builder<CommandSender> builder = manager.commandBuilder("police", 
            ArgumentDescription.of("Main police command"))
            .permission("simplepolice.police");

        manager.command(builder.literal("add")
            .permission("simplepolice.police.add")
            .argument(PlayerArgument.of("player"))
            .handler(context -> {
                Player target = context.get("player");
                plugin.getPoliceService().addPoliceOfficer(target);
            }));

        manager.command(builder.literal("remove")
            .permission("simplepolice.police.remove")
            .argument(PlayerArgument.of("player"))
            .handler(context -> {
                Player target = context.get("player");
                plugin.getPoliceService().removePoliceOfficer(target);
            }));

        manager.command(builder.literal("list")
            .permission("simplepolice.police")
            .handler(context -> {
                CommandSender sender = context.getSender();
                plugin.adventure().sender(sender).sendMessage(
                    Component.text("Police Officers:", NamedTextColor.GOLD)
                );
                
                plugin.getPoliceService().getPoliceOfficers().stream()
                    .map(plugin.getServer()::getOfflinePlayer)
                    .forEach(player -> 
                        plugin.adventure().sender(sender).sendMessage(
                            Component.text("- ", NamedTextColor.GRAY)
                                .append(Component.text(player.getName(), NamedTextColor.YELLOW))
                        )
                    );
            }));
    }

    private void registerArrestCommand() {
        manager.command(manager.commandBuilder("arrest")
            .permission("simplepolice.arrest")
            .argument(PlayerArgument.of("player"))
            .handler(context -> {
                if (!(context.getSender() instanceof Player officer)) {
                    context.getSender().sendMessage("This command can only be used by players");
                    return;
                }

                Player target = context.get("player");
                plugin.getPoliceService().arrestPlayer(officer, target);
            }));
    }

    private void registerJailCommands() {
        Command.Builder<CommandSender> builder = manager.commandBuilder("jail")
            .permission("simplepolice.jail");

        manager.command(builder.literal("set")
            .permission("simplepolice.jail.set")
            .handler(context -> {
                if (!(context.getSender() instanceof Player player)) {
                    context.getSender().sendMessage("This command can only be used by players");
                    return;
                }

                plugin.getPoliceService().setJailLocation(player.getLocation());
            }));

        manager.command(builder.literal("delete")
            .permission("simplepolice.jail.delete")
            .handler(context -> {
                plugin.getPoliceService().setJailLocation(null);
                plugin.adventure().sender(context.getSender()).sendMessage(
                    Component.text("Jail location has been deleted!", NamedTextColor.GREEN)
                );
            }));

        manager.command(builder.literal("tp")
            .permission("simplepolice.jail")
            .handler(context -> {
                if (!(context.getSender() instanceof Player player)) {
                    context.getSender().sendMessage("This command can only be used by players");
                    return;
                }

                var location = plugin.getPoliceService().getJailLocation();
                if (location == null) {
                    plugin.adventure().player(player).sendMessage(
                        Component.text("No jail location has been set!", NamedTextColor.RED)
                    );
                    return;
                }

                player.teleport(location);
                plugin.adventure().player(player).sendMessage(
                    Component.text("Teleported to jail!", NamedTextColor.GREEN)
                );
            }));
    }

    private void registerFineCommand() {
        manager.command(manager.commandBuilder("fine")
            .permission("simplepolice.fine")
            .argument(PlayerArgument.of("player"))
            .argument(DoubleArgument.<CommandSender>newBuilder("amount")
                .withMin(0)
                .build())
            .handler(context -> {
                if (!(context.getSender() instanceof Player officer)) {
                    context.getSender().sendMessage("This command can only be used by players");
                    return;
                }

                Player target = context.get("player");
                double amount = context.get("amount");

                // TODO: Implement economy integration
                plugin.adventure().player(officer).sendMessage(
                    Component.text("Fined ")
                        .color(NamedTextColor.GREEN)
                        .append(Component.text(target.getName()))
                        .append(Component.text(String.format(" $%.2f", amount)))
                );
            }));
    }

    private void registerReloadCommand() {
        manager.command(manager.commandBuilder("spreload")
            .permission("simplepolice.reload")
            .handler(context -> {
                plugin.getConfigManager().loadConfig();
                plugin.adventure().sender(context.getSender()).sendMessage(
                    Component.text("Configuration reloaded!", NamedTextColor.GREEN)
                );
            }));
    }
} 
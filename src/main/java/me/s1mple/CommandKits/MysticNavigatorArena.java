package me.s1mple.CommandKits;

import com.sk89q.worldedit.bukkit.selections.Selection;
import me.s1mple.GameMode.Arena;
import me.s1mple.util.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class MysticNavigatorArena implements CommandExecutor {
    private final me.s1mple.MysticNavigator plugin;

    public MysticNavigatorArena(me.s1mple.MysticNavigator plugin) {
        this.plugin = plugin;

        // Register Commands
        plugin.getCommand("mna").setExecutor(this);
        plugin.getCommand("mysticnavigatorarena").setExecutor(this);
        plugin.getCommand("naviarena").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player;

        if (!commandLabel.equalsIgnoreCase("mna") && !commandLabel.equalsIgnoreCase("mysticnavigaorarena") && !commandLabel.equalsIgnoreCase("naviarena")) {
            return false;
        }

        if (sender instanceof Player) {
            player = (Player) sender;

            // /mna
            if (args.length == 0) {
                player.sendMessage(plugin.getUtil().getHelpArenaMessage());
            } else if (args.length == 1) {

                // /mna help
                if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage(plugin.getUtil().getHelpArenaMessage());
                }

                // /mna arenas
                else if (args[0].equalsIgnoreCase("arenas")) {

                    if (!Permissions.MNA_ARENAS.hasPerm(player) || !player.hasPermission("mna.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    player.sendMessage(ChatColor.DARK_BLUE + "====================");
                    player.sendMessage(ChatColor.BLUE + "Arenas: ");
                    for (Arena arena : plugin.getArenas()) {
                        player.sendMessage(ChatColor.AQUA + " * " + arena.getName());
                    }
                    player.sendMessage(ChatColor.DARK_BLUE + "====================");
                } else {
                    player.sendMessage(ChatColor.RED + "Unknown command. Do /mna help for help !");
                    return false;
                }

            } else if (args.length == 2) {

                // /mna create <Arena>
                if (args[0].equalsIgnoreCase("create")) {

                    if (!Permissions.MNA_CREATE.hasPerm(player) || !player.hasPermission("mna.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    if (args[1].isEmpty() || plugin.getArenasByName().contains(args[1])) {
                        player.sendMessage(ChatColor.RED + "Name of Arena is not provided or arena already exists!");
                        return false;
                    }
                    Selection selection;
                    Location locationFirst;
                    Location locationSecond;

                    try {
                        selection = plugin.getWorldEdit().getSelection(player);
                        locationFirst = selection.getMinimumPoint();
                        locationSecond = selection.getMaximumPoint();
                    } catch (Exception ex) {
                        player.sendMessage(ChatColor.RED + "You did not make a WorldEdit selection!");
                        return false;
                    }

                    try {
                        plugin.createArena(args[1], locationFirst, locationSecond);
                        player.sendMessage(ChatColor.GREEN + "Arena created successfully!");
                    } catch (IOException e) {
                        player.sendMessage(ChatColor.RED + "Arena could not be created!");
                        plugin.getLogger().info(e.getMessage());
                    }
                }

                // /mna reset <Arena>
                else if (args[0].equalsIgnoreCase("reset")) {

                    if (!Permissions.MNA_RESET.hasPerm(player) || !player.hasPermission("mna.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    if (args[1].isEmpty() || !plugin.getArenasByName().contains(args[1])) {
                        player.sendMessage(ChatColor.RED + "Name of Arena is not provided or arena does not exist!");
                        return false;
                    }

                    try {
                        plugin.getArena(args[1]).pasteSchem();
                        player.sendMessage(ChatColor.GREEN + "Arena successfuly resetted!");
                    } catch (IOException e) {
                        player.sendMessage(ChatColor.RED + "Arena failed resetting!");
                        plugin.getLogger().info(e.getMessage());
                    }
                }

                // /mna remove <Arena>
                else if (args[0].equalsIgnoreCase("remove")) {

                    if (!Permissions.MNA_REMOVE.hasPerm(player) || !player.hasPermission("mna.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    if (args[1].isEmpty() || !plugin.getArenasByName().contains(args[1])) {
                        player.sendMessage(ChatColor.RED + "Name of Arena is not provided or arena does not exist!");
                        return false;
                    }

                    if (plugin.removeArena(args[1])) {
                        player.sendMessage(ChatColor.GREEN + "Arena successfuly removed!");
                    } else {
                        player.sendMessage(ChatColor.RED + "Arena could not be removed!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Unknown command. Do /mna help for help !");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Unknown command. Do /mna help for help !");
            }

        }
        return false;
    }
}
package me.s1mple.CommandKits;

import me.s1mple.GameMode.Arena;
import me.s1mple.GameMode.GameMode;
import me.s1mple.util.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class MysticNavigator implements CommandExecutor {
    private final me.s1mple.MysticNavigator plugin;
    private final String dataFolder;

    public MysticNavigator(me.s1mple.MysticNavigator plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder().toString();

        // Register Commands
        plugin.getCommand("mn").setExecutor(this);
        plugin.getCommand("mysticnavigator").setExecutor(this);
        plugin.getCommand("navi").setExecutor(this);
    }

    /**
     * onCommand method
     *
     * @return if the command was successfully applied.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player;

        if (!commandLabel.equalsIgnoreCase("mn") && !commandLabel.equalsIgnoreCase("navi") && !commandLabel.equalsIgnoreCase("mysticnavigator")) {
            return false;
        }

        if (sender instanceof Player) {
            player = (Player) sender;

            // /mn
            if (args.length == 0) {
                player.sendMessage(plugin.getUtil().getHelpMessage());
            } else if (args.length == 1) {
                // /mn help
                if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage(plugin.getUtil().getHelpMessage());
                }

                // /mn setspawn
                else if (args[0].equalsIgnoreCase("setspawn")) {
                    //Check for permissions
                    if (!Permissions.MN_SETSPAWN.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    plugin.getConfigFile().set(plugin.getConfigFile().DEFAULT_LOCATION, player.getLocation());
                    player.sendMessage(ChatColor.GREEN + "Spawn was successfully set!");
                }

                // /mn backup
                else if (args[0].equalsIgnoreCase("backup")) {

                    //Check for permissions
                    if (!Permissions.MN_BACKUP.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    try {
                        plugin.getUtil().backupDatabase(dataFolder); //Back the database up
                        player.sendMessage(ChatColor.GREEN + "Database was successfully backupped.");
                    } catch (Exception ex) {
                        player.sendMessage(ChatColor.RED + "Database was not backuped.");
                    }
                }

                // /mn leave
                else if (args[0].equalsIgnoreCase("leave")) {
                    if (!Permissions.MN_LEAVE.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    player.teleport((Location) plugin.getConfig().get(plugin.getConfigFile().DEFAULT_LOCATION));
                    player.sendMessage(ChatColor.GREEN + "Successfully left the GameMode!");
                } else {
                    player.sendMessage(ChatColor.RED + "Unknown command! Do /mn help for help.");
                    return false;
                }
            } else if (args.length == 2) {

                // /mn add <GameMode>
                if (args[0].equalsIgnoreCase("add") && !args[1].isEmpty()) {
                    //Check for permissions
                    if (!Permissions.MN_ADD.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    // Resetting Arenas Inconvinience
                    if (args[1].equalsIgnoreCase("Arenas")) {
                        player.sendMessage(ChatColor.RED + "Unvalid name!");
                        return false;
                    }

                    if (plugin.gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "GameMode already exists");
                        return false;
                    }

                    plugin.addGameMode(args[1]); // Create GameMode
                    player.sendMessage(ChatColor.GREEN + "GameMode created successfully.");
                }

                // /mn remove <GameMode>
                else if (args[0].equalsIgnoreCase("remove") && !args[1].isEmpty()) {
                    //Check for permissions
                    if (!Permissions.MN_REMOVE.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    // Resetting Arenas Inconvinience
                    if (args[1].equalsIgnoreCase("Arenas")) {
                        player.sendMessage(ChatColor.RED + "Unvalid name!");
                        return false;
                    }

                    if (!plugin.gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "Unknown GameMode.");
                        return false;
                    }

                    plugin.removeGameMode(plugin.getGameMode(args[1])); // Remove GameMode
                    player.sendMessage(ChatColor.GREEN + "GameMode successfully deleted!");
                }


                // /mn Join <Name of GameMode>
                else if (args[0].equalsIgnoreCase("join") && !args[1].isEmpty()) {
                    //Check for permissions
                    if (!Permissions.MN_JOIN.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    if (!plugin.gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "Unknown GameMode.");
                        return false;
                    }

                    try {
                        player.teleport(plugin.getGameMode(args[1]).getSpawnPointLocation(1)); // Teleport player to GameMode, default arena
                        player.sendMessage(ChatColor.GREEN + "Successfully joined " + args[1]);
                    } catch (Exception ex) { // NullPointerexception no arenas
                        player.sendMessage(ChatColor.RED + "GameMode does not have any arenas.");
                        ex.printStackTrace();
                        return false;
                    }
                }

                // /mn Arenas <Name of GameMode>
                else if (args[0].equalsIgnoreCase("arenas") && !args[1].isEmpty()) {
                    GameMode gameMode;

                    //Check for permissions
                    if (!Permissions.MN_ARENAS.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    if (!plugin.gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "Unknown GameMode.");
                        return false;
                    }

                    gameMode = plugin.getGameMode(args[1]);

                    // Check if GameMode has any arenas
                    if (gameMode.getSpawnPointsNames().isEmpty()) {
                        player.sendMessage(ChatColor.RED + "GameMode has no Arenas.");
                        return false;
                    }

                    player.sendMessage(ChatColor.GREEN + "Arenas of " + ChatColor.DARK_GREEN + args[1]);
                    // List arenas
                    for (String arena : gameMode.getSpawnPointsNames()) {
                        player.sendMessage(ChatColor.DARK_GREEN + " * " + ChatColor.GREEN + arena);
                    }


                } else {
                    player.sendMessage(ChatColor.RED + "Unknown command! Do /mn help for help.");
                    return false;
                }

            } else if (args.length == 3) {


                // /mn AddArena <Name of GameMode> <Name of Arena>
                if (args[0].equalsIgnoreCase("addarena") && !args[1].isEmpty() && !args[2].isEmpty()) {
                    //Check for permissions
                    if (!Permissions.MN_ADDARENA.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    GameMode gameMode;
                    Location location;
                    String spawnPointName;

                    if (!plugin.gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "Unknown GameMode.");
                        return false;
                    }

                    gameMode = plugin.getGameMode(args[1]);
                    spawnPointName = args[2];

                    if (gameMode.hasSpawnPoint(spawnPointName)) { // Check if SpawnPoint exists
                        player.sendMessage(ChatColor.RED + "Arena already exists.");
                        return false;
                    }

                    location = player.getLocation();
                    gameMode.setSpawnPoint(location, spawnPointName); // Set a SpawnPoint
                    player.sendMessage(ChatColor.GREEN + "Successfully added Arena.");

                }

                // /mn RemoveArena <Name of GameMode> <Name of Arena> :
                else if (args[0].equalsIgnoreCase("removearena") && !args[1].isEmpty() && !args[2].isEmpty()) {
                    //Check for permissions
                    if (!Permissions.MN_REMOVEARENA.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    GameMode gameMode;
                    String spawnPointName;

                    if (!plugin.gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "Unknown GameMode.");
                        return false;
                    }

                    gameMode = plugin.getGameMode(args[1]);
                    spawnPointName = args[2];

                    if (!gameMode.hasSpawnPoint(spawnPointName)) { // Check if SpawnPoint exists
                        player.sendMessage(ChatColor.RED + "Arena doesn´t exist.");
                        return false;
                    }

                    gameMode.deleteSpawnPoint(spawnPointName); // Delete Spawn Point
                    player.sendMessage(ChatColor.GREEN + "Successfully removed Arena.");
                }

                // /mn Join <Name of GameMode> [Name of Arena]
                else if (args[0].equalsIgnoreCase("join") && !args[1].isEmpty() && !args[2].isEmpty()) {
                    GameMode gameMode;
                    String spawnPointName;

                    //Check for permissions
                    if (!Permissions.MN_JOIN.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    if (!plugin.gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "Unknown GameMode.");
                        return false;
                    }

                    gameMode = plugin.getGameMode(args[1]);
                    spawnPointName = args[2];

                    if (!gameMode.hasSpawnPoint(spawnPointName)) { // Check if SpawnPoint exists
                        player.sendMessage(ChatColor.RED + "Arena doesn´t exist.");
                        return false;
                    }

                    player.teleport(gameMode.getSpawnPointLocation(spawnPointName)); // Teleport Player to GameMode, Arena spawnPointName
                    player.sendMessage(ChatColor.GREEN + "Successfully teleported!");

                } else {
                    player.sendMessage(ChatColor.RED + "Unknown command! Do /mn help for help.");
                    return false;
                }

            } else {
                player.sendMessage(ChatColor.RED + "Unknown command! Do /mn help for help.");
                return false;
            }

            return true;
        } else
            try {
                //Arena arena = new Arena(plugin, "Bending", new Location(plugin.getServer().getWorlds().get(0), (double) 78, (double) 80, (double) 77), new Location(plugin.getServer().getWorlds().get(0), (double) 80, (double) 80, (double) 999));
                Arena arena = plugin.getArena("bending");
                plugin.getLogger().info(arena.getName());
                plugin.getLogger().info(plugin.getArenaLocationFirst(arena.getName()).toString());
                plugin.getLogger().info(plugin.getArenaLocationSecond(arena.getName()).toString());
                arena.pasteSchem();

            } catch (IOException e) {
                e.printStackTrace();
            }
        sender.sendMessage("Only players can use this command!");

        return false;
    }
}

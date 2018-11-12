/*
 * Author: S1mple
 * Class: MysticNavigator
 * Date of Creation: 08.11.2018
 */

package me.s1mple;

import me.s1mple.GameMode.GameMode;
import me.s1mple.util.Config;
import me.s1mple.util.Permissions;
import me.s1mple.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MysticNavigator extends JavaPlugin {
    private String dataFolder;
    private List<Location> locationList;
    private List<GameMode> gameModes;
    private MysticNavigator plugin;
    private Util util;
    private Config configFile;

    /**
     * onEnable() method
     */
    @Override
    public void onEnable() {
        this.locationList = new ArrayList<>();
        this.dataFolder = getDataFolder().toString();
        this.plugin = this;
        this.util = new Util(plugin);
        this.gameModes = new ArrayList<>();
        this.configFile = new Config(plugin);

        // Set the DataFolder up
        setupDataFolder();

        initializeGameModes();
        // Initialize GameModes
        initializeGameModes();
    }

    /**
     * onDisable() method
     */
    @Override
    public void onDisable() {
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
                player.sendMessage(getUtil().getHelpMessage());
            } else if (args.length == 1) {
                // /mn help
                if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage(getUtil().getHelpMessage());
                }

                // /mn setspawn
                else if (args[0].equalsIgnoreCase("setspawn")) {
                    //Check for permissions
                    if (!Permissions.MN_SETSPAWN.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    getConfigFile().set(getConfigFile().DEFAULT_LOCATION, player.getLocation());
                    player.sendMessage(ChatColor.GREEN + "Spawn was successfully set!");
                }

                // /mn backup
                else if(args[0].equalsIgnoreCase("backup")) {

                    //Check for permissions
                    if(!Permissions.MN_BACKUP.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    try {
                        getUtil().backupDatabase(dataFolder); //Back the database up
                        player.sendMessage(ChatColor.GREEN + "Database was successfully backupped.");
                    }
                    catch(Exception ex) {
                        player.sendMessage(ChatColor.RED + "Database was not backuped.");
                    }
                }

                // /mn leave
                else if (args[0].equalsIgnoreCase("leave")) {
                    if (!Permissions.MN_LEAVE.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    player.teleport((Location) getConfig().get(getConfigFile().DEFAULT_LOCATION));
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

                    if (gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "GameMode already exists");
                        return false;
                    }

                    addGameMode(args[1]); // Create GameMode
                    player.sendMessage(ChatColor.GREEN + "GameMode created successfully.");
                }

                // /mn remove <GameMode>
                else if (args[0].equalsIgnoreCase("remove") && !args[1].isEmpty()) {
                    //Check for permissions
                    if (!Permissions.MN_REMOVE.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    if (!gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "Unknown GameMode.");
                        return false;
                    }

                    removeGameMode(getGameMode(args[1])); // Remove GameMode
                    player.sendMessage(ChatColor.GREEN + "GameMode successfully deleted!");
                }


                // /mn Join <Name of GameMode>
                else if (args[0].equalsIgnoreCase("join") && !args[1].isEmpty()) {
                    //Check for permissions
                    if (!Permissions.MN_JOIN.hasPerm(player) || !player.hasPermission("mn.*") || !player.hasPermission("*")) {
                        player.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
                        return false;
                    }

                    if (!gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "Unknown GameMode.");
                        return false;
                    }

                    try {
                        player.teleport(getGameMode(args[1]).getSpawnPointLocation(1)); // Teleport player to GameMode, default arena
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

                    if (!gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "Unknown GameMode.");
                        return false;
                    }

                    gameMode = getGameMode(args[1]);

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

                    if (!gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "Unknown GameMode.");
                        return false;
                    }

                    gameMode = getGameMode(args[1]);
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

                    if (!gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "Unknown GameMode.");
                        return false;
                    }

                    gameMode = getGameMode(args[1]);
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

                    if (!gameModeExists(args[1])) { //Check if GameMode already exists
                        player.sendMessage(ChatColor.RED + "Unknown GameMode.");
                        return false;
                    }

                    gameMode = getGameMode(args[1]);
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
            sender.sendMessage("Only players can use this command!");

        return false;
    }


    /**
     * @param name Name of GameMode
     * @return Weather if GameMode exists
     */
    public boolean gameModeExists(String name) {
        return (plugin.getGameMode(name) != null);
    }


    /**
     * @return Weather if a Game Mode exists or not.
     */
    public boolean existGameModes() {
        return (getGameModes().isEmpty()) ? false : true;
    }

    /**
     * Get the Util class
     *
     * @return Util class
     */
    public Util getUtil() {
        return util;
    }

    /**
     * Initialize all Game Modes
     */
    public void initializeGameModes() {
        // Check if there are any GameModes
        if ((getUtil().getGameModes(dataFolder) == null))
            return;

        // Initialize GameModes
        for (String gameMode : getUtil().getGameModes(dataFolder)) {
            if (!gameModes.contains(new GameMode(gameMode, plugin, dataFolder)) && !gameMode.equals("default"))
                gameModes.add(new GameMode(gameMode, plugin, dataFolder));
        }
    }

    /**
     * List all GameModes
     *
     * @return A list with all Game Modes.
     */
    public List<GameMode> getGameModes() {
        return gameModes;
    }

    /**
     * Get a GameMode by name.
     *
     * @param name Name of GameMode
     * @return GameMode
     */
    public GameMode getGameMode(String name) {
        if (getGameModes().isEmpty())
            return null;

        for (GameMode g : getGameModes()) {
            if (g.getName().equalsIgnoreCase(name))
                return g;
        }
        return null;
    }

    /**
     * Remove a GameMode
     *
     * @param gameMode GameMode
     * @return If the GameMode was successfully deleted.
     */
    public boolean removeGameMode(GameMode gameMode) {
        try {
            gameMode.delete();
            gameModes.remove(gameMode);
            return true;
        } catch (Exception ignored) {}

        return false;
    }

    /**
     * Add a new GameMode
     *
     * @param name Name of the GameMode
     * @return If the GameMode was successfully added.
     */
    public GameMode addGameMode(String name) {
            return (gameModes.add(new GameMode(name, plugin, dataFolder))) ? (new GameMode(name, plugin, dataFolder)) : null;
    }

    /**
     * @return Configuration class to make editing config easier.
     */
    public Config getConfigFile() {
        return this.configFile;
    }

    // Set the data Folder up
    private void setupDataFolder() {
        // Check if Config File Exists, if not create one
        if (!new File(getConfig().getCurrentPath()).exists()) {
            new File(getConfig().getCurrentPath());
            getConfigFile().setupConfig();
        }

        // Check if database exists, if not create one
        if (!this.util.dbExists(dataFolder)) {
            try (Connection conn = this.util.getDatabase(dataFolder)) {
                if (conn != null) {
                    getLogger().info("A new database has been created.");
                }

            } catch (SQLException e) {
                getLogger().info(e.getMessage());
            }
        }

    }


}

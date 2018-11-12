/*
 * Author: S1mple
 * Class: Util
 * Date of Creation: 08.11.2018
 */
package me.s1mple.util;

import me.s1mple.GameMode.GameMode;
import me.s1mple.MysticNavigator;
import org.bukkit.ChatColor;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.sql.*;
import java.util.List;

public class Util {
    final private MysticNavigator plugin;

    public Util(MysticNavigator plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the Database.
     *
     * @param dataFolder Folder in which to search for database.db \n (Data Folder of plugin)
     * @return Database
     * @throws SQLException SQL connection
     */
    public Connection getDatabase(String dataFolder) throws SQLException {
        return DriverManager.getConnection(getDbFileName(dataFolder));
    }

    /**
     * Path name of the Database.
     *
     * @param dataFolder Data Folder of Plugin.
     * @return Path of database as string.
     */
    public String getDbFileName(String dataFolder) {
        return "jdbc:sqlite:" + dataFolder + "\\database.db";
    }

    /**
     * Check if database exists.
     *
     * @param dataFolder Data Folder of Plugin.
     * @return Database exists
     */
    public boolean dbExists(String dataFolder) {
        try {
            return getDatabase(dataFolder) != null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get a list of all gamemodes, GameMode default will be by default in the list.
     *
     * @param dataFolder Data Folder of Plugin.
     * @return List<String> of all gamemodes
     */
    public List<String> getGameModes(String dataFolder) {
        return (List<String>) plugin.getConfig().get(plugin.getConfigFile().GAME_MODES);
    }

    /**
     * Remove the spawnpoints of a game mode.
     *
     * @param dataFolder Data Folder of Plugin.
     * @param gameMode   GameMode to reset.
     * @return Weather if the GameMode has been resetted or not.
     */
    public boolean resetGameMode(String dataFolder, GameMode gameMode) {
        for (String gameModeName : getGameModes(dataFolder)) {
            if (gameModeName.equals(gameMode.getName())) {
                gameMode.delete();
                GameMode gm = new GameMode(gameModeName, plugin, dataFolder);
                return true;
            }
        }
        return false;
    }

    /**
     * Backup the data base file.
     *
     * @param dataFolder Data Folder of Plugin
     * @return If the file has been copied.
     */
    public boolean backupDatabase(String dataFolder) {
        return FileUtil.copy((new File(getDbFileName(dataFolder + "_Backup" + System.currentTimeMillis()))), (new File(getDbFileName(dataFolder))));
    }

    /**
     * @return The Message that´s used in /help.
     */
    public String getHelpMessage() {
        return  ChatColor.DARK_BLUE + "----------------------------------------------\n" +
                ChatColor.DARK_BLUE + " * " + ChatColor.AQUA + "<Argument> must exist. [Argument] is optional\n" +
                ChatColor.DARK_BLUE + " * " + ChatColor.BLUE + "/mn Help : " + ChatColor.AQUA + " Display help message. \n" +
                ChatColor.DARK_BLUE + " * " + ChatColor.BLUE + "/mn Add <GameMode> : " + ChatColor.AQUA + " Create a new Game Mode. \n" +
                ChatColor.DARK_BLUE + " * " + ChatColor.BLUE + "/mn Remove <GameMode> : " + ChatColor.AQUA + " Delete a Game Mode.  \n" +
                ChatColor.DARK_BLUE + " * " + ChatColor.BLUE + "/mn SetSpawn : " + ChatColor.AQUA + " Set your location as the default spawn Hub. \n" +
                ChatColor.DARK_BLUE + " * " + ChatColor.BLUE + "/mn AddArena <GameMode> <Arena>: " + ChatColor.AQUA + " Set your location as an Arena of <GameMode>\n"
                + String.format("%80s", "with the name <Arena>. \n") +
                ChatColor.DARK_BLUE + " * " + ChatColor.BLUE + "/mn RemoveArena <GameMode> <Arena> : " + ChatColor.AQUA + " Remove <Arena> from <GameMode>. \n" +
                ChatColor.DARK_BLUE + " * " + ChatColor.BLUE + "/mn Join <GameMode> [Arena]: " + ChatColor.AQUA + " Teleport to the Default Spawn of <GameMode> or to " +
                String.format("%80s", "<Arena> \n") +
                ChatColor.DARK_BLUE + " * " + ChatColor.BLUE + "/mn Leave : " + ChatColor.AQUA + " Teleport yourself to the Hub \n" +
                ChatColor.DARK_BLUE + " * " + ChatColor.BLUE + "/mn Backup : " + ChatColor.AQUA + " Back the DataBase up. \n" +
                ChatColor.DARK_BLUE + " * " + ChatColor.BLUE + "/mn Arenas <GameMode> : " + ChatColor.AQUA + " List Arenas of <GameMode>\n" +
                ChatColor.DARK_BLUE + "----------------------------------------------";
    }


}

/*
 * Author: S1mple
 * Class: MysticNavigator
 * Date of Creation: 08.11.2018
 */

package me.s1mple;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.s1mple.CommandKits.MysticNavigatorArena;
import me.s1mple.GameMode.Arena;
import me.s1mple.GameMode.GameMode;
import me.s1mple.util.Config;
import me.s1mple.util.Util;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MysticNavigator extends JavaPlugin {
    private String dataFolder;
    private List<GameMode> gameModes;
    private List<Arena> arenas;
    private MysticNavigator plugin;
    private Util util;
    private Config configFile;
    private WorldEditPlugin worldEdit;

    /**
     * onEnable() method
     */
    @Override
    public void onEnable() {
        List<Location> locationList = new ArrayList<>();
        this.dataFolder = getDataFolder().toString();
        this.plugin = this;
        this.util = new Util(plugin);
        this.gameModes = new ArrayList<>();
        this.arenas = new ArrayList<>();
        this.configFile = new Config(plugin);
        this.worldEdit = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");

        setupDataFolder();

        initializeGameModes();

        initializeCommands();
    }

    /**
     * onDisable() method
     */
    @Override
    public void onDisable() {
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
        return (getGameModes().isEmpty());
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
    private void initializeGameModes() {
        // Check if there are any GameModes
        if ((getUtil().getGameModes() == null))
            return;

        // Initialize GameModes
        for (String gameMode : getUtil().getGameModes()) {
            GameMode gm = new GameMode(gameMode, plugin, dataFolder);

            if (!gameModes.contains(gm) && !gameMode.equals("default"))
                gameModes.add(gm);
        }

        // Initialize Arenas
        if (!getUtil().getArenas().isEmpty()) {
            for (String arenaName : getUtil().getArenas()) {
                try {
                    arenas.add(new Arena(plugin, arenaName, getArenaLocationFirst(arenaName), getArenaLocationSecond(arenaName)));
                    getLogger().info("Arena " + arenaName + " has been loaded.");
                } catch (IOException e) {
                    getLogger().info(e.getMessage());
                }
            }
        } else {
            getLogger().info("No arenas Found!");


            String sql = "CREATE TABLE IF NOT EXISTS Arenas (\n"
                    + "	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
                    + "	xFirst real,\n"
                    + "	yFirst real,\n"
                    + "	zFirst real,\n"
                    + "	xSecond real,\n"
                    + "	ySecond real,\n"
                    + "	zSecond real,\n"
                    + "name text, \n"
                    + "resetTime int, \n"
                    + "	world text"
                    + ");";

            try (Connection conn = plugin.getUtil().getDatabase(dataFolder);
                 Statement state = conn.createStatement()) {
                state.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Initialize the CommandKits
     */
    private void initializeCommands() {
        me.s1mple.CommandKits.MysticNavigator mn = new me.s1mple.CommandKits.MysticNavigator(plugin);
        MysticNavigatorArena mna = new MysticNavigatorArena(plugin);
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
     */
    public void removeGameMode(GameMode gameMode) {
        try {
            gameMode.delete();
            gameModes.remove(gameMode);
        } catch (Exception ignored) {
        }

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

    /**
     * Get First location of an Arena
     *
     * @param name Name of Arena.
     * @return First Location of the Arena.
     */
    public Location getArenaLocationFirst(String name) {
        Location loc = null;
        String world;
        double x;
        double y;
        double z;

        String sql = "SELECT world, xFirst, yFirst, zFirst FROM Arenas WHERE name='" + name + "'";
        try (Connection conn = plugin.getUtil().getDatabase(dataFolder)) {
            ResultSet res = conn.createStatement().executeQuery(sql);

            world = res.getString("world");
            x = res.getDouble("xFirst");
            y = res.getDouble("yFirst");
            z = res.getDouble("zFirst");
            res.close();

            loc = new Location(plugin.getServer().getWorld(world), x, y, z);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return loc;
    }

    /**
     * @param name Name of the Arena
     * @return Seond Location of the arena.
     */
    public Location getArenaLocationSecond(String name) {
        Location loc = null;
        String world;
        double x;
        double y;
        double z;

        String sql = "SELECT world, xSecond, ySecond, zSecond FROM Arenas WHERE name='" + name + "'";
        try (Connection conn = plugin.getUtil().getDatabase(dataFolder)) {
            ResultSet res = conn.createStatement().executeQuery(sql);

            world = res.getString("world");
            x = res.getDouble("xSecond");
            y = res.getDouble("ySecond");
            z = res.getDouble("zSecond");
            res.close();

            loc = new Location(plugin.getServer().getWorld(world), x, y, z);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return loc;
    }

    /**
     * @param name Name of the Arena you want to get.
     * @return An Arena
     */
    public Arena getArena(String name) {
        Arena arenaFound = null;

        for (Arena arena : arenas) {
            if (arena.getName().equalsIgnoreCase(name)) {
                arenaFound = arena;
            }
        }

        return arenaFound;
    }

    /**
     * @return List of the Names of all arenas that exist
     */
    public List<String> getArenasByName() {
        return plugin.getUtil().getArenas();
    }

    /**
     * @return List of all Arenas that exist
     */
    public List<Arena> getArenas() {
        return arenas;
    }

    /**
     * @return Instance of the WorldEdit plugin.
     */
    public WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }

    /**
     * Create a new Arena
     *
     * @param name           Name of the Arena
     * @param locationFirst  Lowest Location of the arena
     * @param locationSecond Highest Location of the arena
     */
    public void createArena(String name, Location locationFirst, Location locationSecond) throws IOException {
        new Arena(plugin, name, locationFirst, locationSecond);
        getUtil().getArenas();
    }

    /**
     * Remove an Arena
     *
     * @param name Name of arena
     * @return If arena was successfully removed
     */
    public boolean removeArena(String name) {
        Arena arena = getArena(name);
        arena.remove();
        return arenas.remove(arena);
    }

}

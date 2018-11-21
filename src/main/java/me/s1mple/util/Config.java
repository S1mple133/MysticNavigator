package me.s1mple.util;

import me.s1mple.MysticNavigator;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class Config {
    final public String DEFAULT_LOCATION = "MysticNavigator.DefaultSpawnpoint";
    final public String GAME_MODES = "MysticNavigator.GameModes";
    final public String ENABLE_GAME_MODES = "MysticNavigator.Options.GameModes";
    final public String ENABLE_ARENAS = "MysticNavigator.Options.Arenas";
    final public String GAME_MODE_ARENAS = "MysticNavigator.GameModeArenas";
    final public String ARENAS_RESET_SCHEM_ALLOWUNDO = "MysticNavigator.ArenasResetSchem.AllowUndo";
    final private MysticNavigator plugin;
    final private FileConfiguration config;

    public Config(MysticNavigator plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    /**
     * Set an Object in the config and Save it.
     *
     * @param configName Path, where the Object should be saved.
     * @param value      Object
     */
    public void set(String configName, Object value) {
        config.set(configName, value);
        plugin.saveConfig();
    }

    /**
     * Set the default Config up.
     */
    public void setupConfig() {
        config.addDefault(DEFAULT_LOCATION, new Location(plugin.getServer().getWorlds().get(0), 0, 0, 0));
        config.addDefault(GAME_MODES, new ArrayList<String>());
        config.addDefault(GAME_MODE_ARENAS, new ArrayList<String>());
        config.addDefault(ENABLE_ARENAS, true);
        config.addDefault(ENABLE_GAME_MODES, true);
        config.addDefault(ARENAS_RESET_SCHEM_ALLOWUNDO, false);
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }
}

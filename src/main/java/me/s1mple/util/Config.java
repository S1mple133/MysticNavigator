package me.s1mple.util;

import me.s1mple.MysticNavigator;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class Config {
    final public String DEFAULT_LOCATION = "MysticNavigator.DefaultSpawnpoint";
    final public String GAME_MODES = "MysticNavigator.GameModes";
    final private MysticNavigator plugin;
    final private FileConfiguration config;

    public Config(MysticNavigator plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void set(String configName, Object value) {
        config.set(configName, value);
        plugin.saveConfig();
    }

    public void setupConfig() {
        config.addDefault(DEFAULT_LOCATION, new Location(plugin.getServer().getWorld("world"), 0, 0, 0));
        config.addDefault(GAME_MODES, new ArrayList<String>());
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }
}

package me.s1mple.GameMode;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.s1mple.MysticNavigator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@SuppressWarnings("FinalizeCalledExplicitly")
public class Arena {
    final private String name;
    final private MysticNavigator plugin;
    final private String dataFolder;
    final private Location locFirst;
    final private Location locSecond;
    private int scheduleTaskID;

    public Arena(MysticNavigator plugin, String name, Location locFirst, Location locSecond) throws IOException {
        this.plugin = plugin;
        this.name = name;
        this.dataFolder = plugin.getDataFolder().toString();
        this.locFirst = locFirst;
        this.locSecond = locSecond;
        this.scheduleTaskID=0;

        List<String> arenas = plugin.getUtil().getArenas();

        // Check if arena exists
        if (!arenas.contains(getName())) {
            arenas.add(getName());
            plugin.getConfigFile().set(plugin.getConfigFile().GAME_MODE_ARENAS, arenas); // Add Arena to Config
            saveToDb();
            createSchem();
            plugin.getArenas().add(this);
        }

        if(getResetTime() != 0) {
            scheduleResetArena();
        }
    }

    /**
     * @return Name of Arena.
     */
    public String getName() {
        return name.toLowerCase();
    }

    /**
     * Create Schem (Schematic) to make it possible to reset the Arena
     *
     * @throws IOException
     */
    private void createSchem() throws IOException {
        String fileName = plugin.getDataFolder().toString() + "\\Schematic";

        if (!(new File(fileName).exists())) {
            new File(fileName);
        }

        File schemFile = new File(fileName + "\\" + getName() + ".schematic");

        Vector vecFirst = new Vector(locFirst.getBlockX(), locFirst.getBlockY(), locFirst.getBlockZ());
        Vector vecSecond = new Vector(locSecond.getBlockX(), locSecond.getBlockY(), locSecond.getBlockZ());

        CuboidRegion cubReg = new CuboidRegion(vecFirst, vecSecond);
        cubReg.setWorld(new BukkitWorld(locFirst.getWorld()));
        Schematic schem = new Schematic(cubReg);
        schem.save(schemFile, ClipboardFormat.SCHEMATIC);
    }

    /**
     * Reset Arena by pasting the Schematic
     */
    public void pasteSchem() throws IOException {
        File schemFile = new File(plugin.getDataFolder().toString() + "\\Schematic\\" + getName() + ".schematic");
        BukkitWorld worldBukkit = new BukkitWorld(locFirst.getWorld());
        new Schematic(ClipboardFormat.SCHEMATIC.getReader(new FileInputStream(schemFile)).read(worldBukkit.getWorldData())).paste(worldBukkit, new Vector(locFirst.getBlockX(), locFirst.getBlockY(), locFirst.getBlockZ()), (boolean) plugin.getConfig().get(plugin.getConfigFile().ARENAS_RESET_SCHEM_ALLOWUNDO), false, null);
    }

    /**
     * Reset an Arena every "time" minutes and change the default resetting time to "time"
     * @param time Minutes
     */
    public void scheduleResetArena(int time) {
        setResetTime(time);

        this.scheduleTaskID = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {

            public void run() {
                String name = getName();

                try {
                    pasteSchem();

                    for(Player player : plugin.getServer().getOnlinePlayers()) {
                        player.sendMessage(ChatColor.AQUA + "Arena " + name + " was reset!");
                    }

                    plugin.getLogger().info("Arena " + name + " has been successfully resetted!");

                } catch (IOException e) {
                    plugin.getLogger().info("Arena " + name + " could not be resetted!");
                }
            }
        }, 20*60L, time*20*60);
    }

    /**
     * Reset an Arena every x min
     */
    public void scheduleResetArena() {

        this.scheduleTaskID = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {

            public void run() {
                String name = getName();

                try {
                    pasteSchem();

                    for(Player player : plugin.getServer().getOnlinePlayers()) {
                        player.sendMessage(ChatColor.AQUA + "Arena " + name + " was reset!");
                    }

                    plugin.getLogger().info("Arena " + name + " has been successfully resetted!");
                } catch (IOException e) {
                    plugin.getLogger().info("Arena " + name + " could not be resetted!");
                }
            }
        }, 20*60L, getResetTime()*20*60);
    }

    /**
     * Get the amount of time between each automatical arena reset
     * @return Time of Reset in min
     */
    public int getResetTime() {
        int resetTime;
        String sql = "SELECT resetTime FROM Arenas WHERE name='" + getName() + "'";

        try (Connection conn = plugin.getUtil().getDatabase(dataFolder)) {
            ResultSet res = conn.createStatement().executeQuery(sql);

            resetTime=res.getInt("resetTime");
            res.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            resetTime=0;
        }
        return resetTime;
    }

    /**
     * Set the time between each automatical arena reset
     * @param time Minutes
     */
    public void setResetTime(int time) {
        String sql = "UPDATE Arenas SET resetTime = ? WHERE name = ? ";

        try (Connection conn = plugin.getUtil().getDatabase(dataFolder);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, time);
            pstmt.setString(2, name.toLowerCase());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Save Arena to DataBase
     */
    private void saveToDb() {
        String sql = "INSERT INTO Arenas(world,xFirst,yFirst,zFirst,xSecond,ySecond,zSecond,name) VALUES(?,?,?,?,?,?,?,?)";
        String world = locFirst.getWorld().getName();
        double xFirst = locFirst.getX();
        double yFirst = locFirst.getY();
        double zFirst = locFirst.getZ();

        double xSecond = locSecond.getX();
        double ySecond = locSecond.getY();
        double zSecond = locSecond.getZ();

        //Save the Locations in the database
        try (Connection conn = plugin.getUtil().getDatabase(dataFolder);
             PreparedStatement pstate = conn.prepareStatement(sql)) {
            pstate.setString(1, world);
            pstate.setDouble(2, xFirst);
            pstate.setDouble(3, yFirst);
            pstate.setDouble(4, zFirst);
            pstate.setDouble(5, xSecond);
            pstate.setDouble(6, ySecond);
            pstate.setDouble(7, zSecond);
            pstate.setString(8, name.toLowerCase());
            pstate.executeUpdate();
            plugin.getLogger().info("Successfully saved to database!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Remove an Arena. Should be done with {@link me.s1mple.MysticNavigator#removeArena(String)}
     */
    public void remove() {
        String sql = "DELETE FROM Arenas WHERE name='" + getName() + "'";
        List<String> arenas = plugin.getUtil().getArenas();
        File schemFile = new File(plugin.getDataFolder().toString() + "\\Schematic\\" + getName() + ".schematic");

        try (Connection conn = plugin.getUtil().getDatabase(dataFolder);
             PreparedStatement pstate = conn.prepareStatement(sql)) {
            pstate.executeUpdate();


            if (arenas.contains(getName())) {
                arenas.remove(getName());
                plugin.getConfigFile().set(plugin.getConfigFile().GAME_MODE_ARENAS, arenas); // Remove Arena from config
            }

            this.finalize();

            // Stop Scheduler from running
            if(scheduleTaskID!=0) {
                plugin.getServer().getScheduler().cancelTask(this.scheduleTaskID);
            }
            if (schemFile.exists()) {
                schemFile.delete();
            }

        } catch (Throwable throwable) {
            System.out.println(throwable.getMessage());
        }
    }
}

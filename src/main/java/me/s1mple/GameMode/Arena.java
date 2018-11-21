package me.s1mple.GameMode;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.s1mple.MysticNavigator;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@SuppressWarnings("FinalizeCalledExplicitly")
public class Arena {
    final private String name;
    final private MysticNavigator plugin;
    final private String dataFolder;
    final private Location locFirst;
    final private Location locSecond;

    public Arena(MysticNavigator plugin, String name, Location locFirst, Location locSecond) throws IOException {
        this.plugin = plugin;
        this.name = name;
        this.dataFolder = plugin.getDataFolder().toString();
        this.locFirst = locFirst;
        this.locSecond = locSecond;

        List<String> arenas = plugin.getUtil().getArenas();

        // Check if arena exists
        if (!arenas.contains(getName())) {
            arenas.add(getName());
            plugin.getConfigFile().set(plugin.getConfigFile().GAME_MODE_ARENAS, arenas); // Add Arena to Config
            saveToDb();
            createSchem();
            plugin.getArenas().add(this);
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

            if (schemFile.exists()) {
                schemFile.delete();
            }

        } catch (Throwable throwable) {
            System.out.println(throwable.getMessage());
        }
    }
}

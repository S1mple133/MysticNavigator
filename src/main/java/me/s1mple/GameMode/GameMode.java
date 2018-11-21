/*
 * Author: S1mple
 * Class: GameMode
 * Date of Creation: 08.11.2018
 */

package me.s1mple.GameMode;

import me.s1mple.MysticNavigator;
import org.bukkit.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameMode {
    final private String name;
    final private MysticNavigator plugin;
    final private String dataFolder;

    /**
     * Initialize a new Game Mode
     *
     * @param gameModeName The name of the gamemode
     * @param pl           The MysticNavigator plugin
     * @param dataFolder   The data folder of the plugin
     **/
    public GameMode(String gameModeName, MysticNavigator pl, String dataFolder) {
        this.name = gameModeName;
        this.plugin = pl;
        this.dataFolder = dataFolder;
        List<String> gameModes = (List<String>) plugin.getConfig().get(plugin.getConfigFile().GAME_MODES);

        String sql = "CREATE TABLE IF NOT EXISTS " + getName() + " (\n"
                + "	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
                + "	x real,\n"
                + "	y real,\n"
                + "	z real,\n"
                + "name text, \n"
                + "	world text"
                + ");";

        if (!gameModes.contains(getName())) {
            gameModes.add(getName());
            plugin.getConfigFile().set(plugin.getConfigFile().GAME_MODES, gameModes); // Add GameMode to Config
        }

        try (Connection conn = pl.getUtil().getDatabase(dataFolder);
             Statement state = conn.createStatement()) {
            state.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Number of spawn points.
     */
    public int getSpawnPlaces() {
        int i = 0;
        String sql = "SELECT id FROM " + getName();

        try (Connection conn = plugin.getUtil().getDatabase(dataFolder);
             Statement state = conn.createStatement()) {
            ResultSet rs = state.executeQuery(sql);

            while (rs.next()) {
                i++;
            }

            rs.close();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * @return SpawnPointsNames as a String List.
     */
    public List<String> getSpawnPointsNames() {
        ResultSet rs;
        List<String> spawnPoints = new ArrayList<>();
        String sql = "SELECT name from " + getName();

        try (Connection conn = plugin.getUtil().getDatabase(dataFolder);
             Statement state = conn.createStatement()) {
            rs = state.executeQuery(sql);

            while (rs.next()) {
                spawnPoints.add(rs.getString("name"));
            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spawnPoints;
    }

    /**
     * Set a spawnpoint
     *
     * @param loc  Location of spawnpoint
     * @param name Name of spawnpoint
     */
    public void setSpawnPoint(Location loc, String name) {
        String sql = "INSERT INTO " + getName() + "(world,x,y,z,name) VALUES(?,?,?,?,?)";
        String world = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        // Return false if spawnpoint exists
        if (hasSpawnPoint(loc)) {
            return;
        }

        try (Connection conn = plugin.getUtil().getDatabase(dataFolder);
             PreparedStatement pstate = conn.prepareStatement(sql)) {
            pstate.setString(1, world);
            pstate.setDouble(2, x);
            pstate.setDouble(3, y);
            pstate.setDouble(4, z);
            pstate.setString(5, name.toLowerCase());
            pstate.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Change spawn point
     *
     * @param locNew New location
     * @param name   Name of old SpawnPoint
     */
    public void changeSpawnPoint(Location locNew, String name) {
        String sql = "UPDATE " + getName() + " SET world = ? , "
                + "x = ? , "
                + "y = ? , "
                + "z = ? "
                + "WHERE name = ? ";
        String worldNew = locNew.getWorld().getName();
        double xNew = locNew.getX();
        double yNew = locNew.getY();
        double zNew = locNew.getZ();

        try (Connection conn = plugin.getUtil().getDatabase(dataFolder);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, worldNew);
            pstmt.setDouble(2, xNew);
            pstmt.setDouble(3, yNew);
            pstmt.setDouble(4, zNew);
            pstmt.setString(5, name.toLowerCase());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Returns the id of a spawn point.
     *
     * @param loc Location of spawnpoint
     * @return Spawn point´s id as an int
     */
    public int getId(Location loc) {
        int id;
        String world = loc.getWorld().getName();
        Double x = loc.getX();
        Double y = loc.getY();
        Double z = loc.getZ();
        String sql = "SELECT id FROM " + getName() + " where x=" + x + " AND world=" + world + " AND y=" + y + " AND z=" + z + ";\n";

        try (Connection conn = plugin.getUtil().getDatabase(dataFolder);
             Statement state = conn.createStatement()) {
            ResultSet rs = state.executeQuery(sql);
            id = rs.getInt("id");
            rs.close();

            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Returns the id of a spawn point.
     *
     * @param spawnPointName Name of SpawnPoint
     * @return Spawn point´s id as an int
     */
    public int getId(String spawnPointName) {
        int id;
        String sql = "SELECT id FROM " + getName() + " where name='" + spawnPointName.toLowerCase() + "';\n";

        try (Connection conn = plugin.getUtil().getDatabase(dataFolder);
             Statement state = conn.createStatement()) {
            ResultSet rs = state.executeQuery(sql);
            id = rs.getInt("id");
            rs.close();

            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get location of a spawn point.
     *
     * @param spawnPoint Name of the spawn point
     * @return Location of Spawnpoint, null if it does not exist
     */
    public Location getSpawnPointLocation(String spawnPoint) {
        Location loc = null;

        String sql = "SELECT world, x, y, z FROM " + getName() + " where name='" + spawnPoint + "' ;";
        try (Connection conn = plugin.getUtil().getDatabase(dataFolder);
             Statement state = conn.createStatement()) {
            ResultSet rs = state.executeQuery(sql);
            loc = new Location(plugin.getServer().getWorld(rs.getString("world")), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loc;
    }

    /**
     * Get location of a spawn point.
     *
     * @param id Id of the spawn point
     * @return Location of Spawnpoint, null if it does not exist
     */
    public Location getSpawnPointLocation(int id) {
        Location loc;
        String world;
        double x;
        double y;
        double z;

        String sql = "SELECT world, x, y, z FROM '" + getName() + "' where id=" + id + ";";
        try (Connection conn = plugin.getUtil().getDatabase(dataFolder)) {
            ResultSet rs = conn.createStatement().executeQuery(sql);

            world = rs.getString("world");
            x = rs.getDouble("x");
            y = rs.getDouble("y");
            z = rs.getDouble("z");
            rs.close();

            loc = new Location(plugin.getServer().getWorld(world), x, y, z);
        } catch (SQLException e) {
            loc = null;
            e.printStackTrace();
        }

        return loc;
    }

    /**
     * @return name of the GameMode.
     */
    public String getName() {
        return name.toLowerCase();
    }

    /**
     * Deletes a spawnpoint.
     *
     * @param name Name of Spawn point
     */
    public void deleteSpawnPoint(String name) {
        String sql = "DELETE FROM " + getName() + " WHERE name=?";

        try (Connection conn = plugin.getUtil().getDatabase(dataFolder);
             PreparedStatement pstate = conn.prepareStatement(sql)) {
            pstate.setString(1, name.toLowerCase());
            pstate.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Delete GameMode
     */
    public void delete() {
        String sql = "DROP TABLE " + getName();
        List<String> gameModes = (List<String>) plugin.getConfig().get(plugin.getConfigFile().GAME_MODES);

        try (Connection conn = plugin.getUtil().getDatabase(dataFolder);
             PreparedStatement pstate = conn.prepareStatement(sql)) {
            pstate.executeUpdate();


            if (gameModes.contains(getName())) {
                gameModes.remove(getName());
                plugin.getConfigFile().set(plugin.getConfigFile().GAME_MODES, gameModes); // Remove GameMode from config
            }

            this.finalize();
        } catch (Throwable throwable) {
            System.out.println(throwable.getMessage());
        }
    }

    /**
     * @param name Name of SpawnPoint
     * @return Wether if spawnpoint exists or not;
     */
    public boolean hasSpawnPoint(String name) {
        for (String arena : getSpawnPointsNames()) {
            if (arena.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param loc Location of SpawnPoint
     * @return Wether if spawnpoint exists or not;
     */
    public boolean hasSpawnPoint(Location loc) {
        for (String arena : getSpawnPointsNames()) {
            if (getSpawnPointLocation(arena) == loc) {
                return true;
            }
        }

        return false;
    }

}

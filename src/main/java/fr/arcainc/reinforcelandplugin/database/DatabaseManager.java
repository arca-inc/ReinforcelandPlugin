/**
 * Manages the SQLite database for the ReinforceLandPlugin, including reinforced blocks,
 * ownership information, and sharing relationships.
 */
package fr.arcainc.reinforcelandplugin.database;

import fr.arcainc.reinforcelandplugin.ReinforceLandPlugin;
import fr.arcainc.reinforcelandplugin.config.HealthDisplay;
import fr.arcainc.reinforcelandplugin.config.ModeDisplay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private final ReinforceLandPlugin plugin;
    private final String databaseURL;

    /**
     * Constructs a DatabaseManager instance for managing the plugin's SQLite database.
     *
     * @param plugin The instance of the ReinforceLandPlugin.
     */
    public DatabaseManager(ReinforceLandPlugin plugin) {
        this.plugin = plugin;
        this.databaseURL = "jdbc:sqlite:" + plugin.getDataFolder() + "/reinforced_blocks.db"; // Change the file path as needed
        File db = new File(plugin.getDataFolder() + "/reinforced_blocks.db");
        if (!db.exists()) {
            try {
                db.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        createTables();
    }

    /**
     * Creates the necessary database tables if they do not exist.
     */
    public void createTables() {
        try (Connection connection = DriverManager.getConnection(databaseURL)) {
            // Create the table for reinforced blocks
            String createTableSQL = "CREATE TABLE IF NOT EXISTS reinforced_blocks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "x INT NOT NULL," +
                    "y INT NOT NULL," +
                    "z INT NOT NULL," +
                    "world VARCHAR(255) NOT NULL," +
                    "health INT NOT NULL," +
                    "owner VARCHAR(255) NOT NULL" +
            ");";

            // Create the table for sharing relationships
            String createRelationTableSQL = "CREATE TABLE IF NOT EXISTS share_relations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "player_id VARCHAR(255) NOT NULL," +
                    "target_player_id VARCHAR(255) NOT NULL," +
                    "share_storage BOOLEAN NOT NULL DEFAULT 0," +
                    "share_beak_bypass BOOLEAN NOT NULL DEFAULT 0," +
                    "share_add_health BOOLEAN NOT NULL DEFAULT 0," +
                    "share_use BOOLEAN NOT NULL DEFAULT 0" +
                    ");";

            // Create the player_config table
            String createPlayerConfigTableSQL = "CREATE TABLE IF NOT EXISTS player_config (" +
                    "player_id VARCHAR(255) PRIMARY KEY," +
                    "health_display VARCHAR(20) DEFAULT 'HOLO'," +
                    "mode_display VARCHAR(20) DEFAULT 'ACTION_BAR'" +
                    ");";

            try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(createRelationTableSQL)) {
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(createPlayerConfigTableSQL)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a block at the specified coordinates is reinforced.
     *
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param z The z-coordinate of the block.
     * @return true if the block is reinforced, false otherwise.
     */
    public boolean isBlockReinforced(int x, int y, int z, String world_name) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM reinforced_blocks WHERE x=? AND y=? AND z=? AND world=?")) {

            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setInt(3, z);
            statement.setString(4, world_name);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Inserts a new reinforced block entry with ownership information into the database.
     *
     * @param x      The x-coordinate of the block.
     * @param y      The y-coordinate of the block.
     * @param z      The z-coordinate of the block.
     * @param health The health of the reinforced block.
     * @param player The player who owns the reinforced block.
     */
    public void insertReinforcedBlockOwned(int x, int y, int z, int health, Player player, String world_name) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO reinforced_blocks (x, y, z, health, owner, world) VALUES (?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setInt(3, z);
            statement.setInt(4, health);
            statement.setString(5, String.valueOf(player.getUniqueId()));
            statement.setString(6, world_name);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds health to a reinforced block at the specified coordinates.
     *
     * @param x            The x-coordinate of the block.
     * @param y            The y-coordinate of the block.
     * @param z            The z-coordinate of the block.
     * @param healthToAdd  The amount of health to add to the reinforced block.
     */
    public void addHealthToReinforcedBlock(int x, int y, int z, int healthToAdd, String world_name) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("UPDATE reinforced_blocks SET health = health + ? WHERE x=? AND y=? AND z=? AND world=?")) {

            statement.setInt(1, healthToAdd);
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);
            statement.setString(5, world_name);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subtracts health from a reinforced block at the specified coordinates.
     *
     * @param x              The x-coordinate of the block.
     * @param y              The y-coordinate of the block.
     * @param z              The z-coordinate of the block.
     * @param healthToSubtract The amount of health to subtract from the reinforced block.
     */
    public void subtractHealthFromReinforcedBlock(int x, int y, int z, int healthToSubtract, String world_name) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("UPDATE reinforced_blocks SET health = health - ? WHERE x=? AND y=? AND z=? AND world=?")) {

            statement.setInt(1, healthToSubtract);
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);
            statement.setString(5, world_name);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the health of a reinforced block at the specified coordinates.
     *
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param z The z-coordinate of the block.
     * @return The health of the reinforced block or 0 if not found or in case of an error.
     */
    public int getHealthForBlock(int x, int y, int z) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("SELECT health FROM reinforced_blocks WHERE x=? AND y=? AND z=?")) {

            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setInt(3, z);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("health");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if the block is not found or there is an error
    }

    /**
     * Removes a reinforced block entry from the database based on its coordinates.
     *
     * @param x The x-coordinate of the block to remove.
     * @param y The y-coordinate of the block to remove.
     * @param z The z-coordinate of the block to remove.
     */
    public void removeReinforcedBlock(int x, int y, int z, String world_name) {



        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM reinforced_blocks WHERE x=? AND y=? AND z=? AND world=?")) {

            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setInt(3, z);
            statement.setString(4, world_name);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the owner for a reinforced block at the specified coordinates.
     *
     * @param x     The x-coordinate of the block.
     * @param y     The y-coordinate of the block.
     * @param z     The z-coordinate of the block.
     * @param owner The UUID of the player who owns the block.
     */
    public void setOwnerForReinforcedBlock(int x, int y, int z, String owner, String world_name) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("UPDATE reinforced_blocks SET owner = ? WHERE x=? AND y=? AND z=? AND world=?")) {

            statement.setString(1, owner);
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);
            statement.setString(5, world_name);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the owner of a reinforced block at the specified coordinates.
     *
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param z The z-coordinate of the block.
     * @return The UUID of the player who owns the block or null if not found or in case of an error.
     */
    public String getOwnerForBlock(int x, int y, int z, String world_name) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("SELECT owner FROM reinforced_blocks WHERE x=? AND y=? AND z=? AND world=?")) {

            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setInt(3, z);
            statement.setString(4, world_name);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("owner");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if the owner is not found or there is an error
    }

    /**
     * Sets sharing relationships between players for specified permissions.
     *
     * @param playerId       The ID of the player initiating the sharing.
     * @param targetPlayerId The ID of the target player with whom sharing is established.
     * @param shareStorage   True to allow storage sharing, false otherwise.
     * @param shareBeakBypass True to allow beak bypass sharing, false otherwise.
     * @param shareAddHealth True to allow health addition sharing, false otherwise.
     * @param shareUse       True to allow usage like door, trapdoor sharing, false otherwise.
     */
    public void setShareRelations(String playerId, String targetPlayerId, boolean shareStorage, boolean shareBeakBypass, boolean shareAddHealth, boolean shareUse) {

        if(!shareAddHealth)
            if (!shareBeakBypass)
                if(!shareStorage)
                    if(!shareUse)
                        return;

        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO share_relations (player_id, target_player_id, share_storage, share_beak_bypass, share_add_health, share_use) VALUES (?, ?, ?, ?, ?, ?)")) {

            statement.setString(1, playerId);
            statement.setString(2, targetPlayerId);
            statement.setBoolean(3, shareStorage);
            statement.setBoolean(4, shareBeakBypass);
            statement.setBoolean(5, shareAddHealth);
            statement.setBoolean(6, shareUse);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes sharing relationships between players.
     *
     * @param playerId       The ID of the player initiating the removal of sharing relationships.
     * @param targetPlayerId The ID of the target player with whom sharing relationships are removed.
     */
    public void removeShareRelations(String playerId, String targetPlayerId) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM share_relations WHERE player_id=? AND target_player_id=?")) {

            statement.setString(1, playerId);
            statement.setString(2, targetPlayerId);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a player has a specified share permission with another player.
     *
     * @param playerId       The ID of the player initiating the query.
     * @param targetPlayerId The ID of the target player.
     * @param permission     The share permission to check.
     * @return true if the sharing relationship is found and the permission is granted, false otherwise or in case of an error.
     */
    public boolean hasSharePermission(String playerId, String targetPlayerId, SharePermission permission) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("SELECT " + permission.getPermissionName() + " FROM share_relations WHERE player_id=? AND target_player_id=?")) {

            statement.setString(1, playerId);
            statement.setString(2, targetPlayerId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(permission.getPermissionName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Returns false if the sharing relationship is not found or in case of an error.
    }

    /**
     * Retrieves a list of players in a sharing relationship with the specified player.
     *
     * @param playerId The ID of the player for whom sharing relationships are queried.
     * @return A list of player IDs in a sharing relationship with the specified player.
     */
    public List<String> getSharingPlayers(String playerId) {
        List<String> sharingPlayers = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("SELECT target_player_id FROM share_relations WHERE player_id=?")) {

            statement.setString(1, playerId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                sharingPlayers.add(resultSet.getString("target_player_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sharingPlayers;
    }

    /**
     * Sets the health_display and mode_display for a player in the player_config table.
     *
     * @param playerId        The ID of the player.
     * @param healthDisplay   The health_display enum.
     * @param modeDisplay     The mode_display enum.
     */
    public void setPlayerConfig(String playerId, HealthDisplay healthDisplay, ModeDisplay modeDisplay) {
        try (Connection connection = DriverManager.getConnection(databaseURL)) {
            // Check if a row with the player_id already exists
            String selectSQL = "SELECT COUNT(*) FROM player_config WHERE player_id = ?";

            try (PreparedStatement selectStatement = connection.prepareStatement(selectSQL)) {
                selectStatement.setString(1, playerId);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // If a row exists, update it
                    String updateSQL = "UPDATE player_config SET health_display=?, mode_display=? WHERE player_id=?";

                    try (PreparedStatement updateStatement = connection.prepareStatement(updateSQL)) {
                        updateStatement.setString(1, healthDisplay.name());
                        updateStatement.setString(2, modeDisplay.name());
                        updateStatement.setString(3, playerId);
                        updateStatement.executeUpdate();
                    }
                } else {
                    // If no row exists, insert a new row
                    String insertSQL = "INSERT INTO player_config (player_id, health_display, mode_display) VALUES (?, ?, ?)";

                    try (PreparedStatement insertStatement = connection.prepareStatement(insertSQL)) {
                        insertStatement.setString(1, playerId);
                        insertStatement.setString(2, healthDisplay.name());
                        insertStatement.setString(3, modeDisplay.name());
                        insertStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the health_display enum for a player from the player_config table.
     *
     * @param playerId The ID of the player.
     * @return The health_display enum for the player.
     */
    public HealthDisplay getPlayerHealthDisplay(String playerId) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("SELECT health_display FROM player_config WHERE player_id=?")) {

            statement.setString(1, playerId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return HealthDisplay.valueOf(resultSet.getString("health_display"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return HealthDisplay.HOLO; // Default value
    }

    /**
     * Retrieves the mode_display enum for a player from the player_config table.
     *
     * @param playerId The ID of the player.
     * @return The mode_display enum for the player.
     */
    public ModeDisplay getPlayerModeDisplay(String playerId) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("SELECT mode_display FROM player_config WHERE player_id=?")) {

            statement.setString(1, playerId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return ModeDisplay.valueOf(resultSet.getString("mode_display"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ModeDisplay.ACTION_BAR; // Default value
    }
}
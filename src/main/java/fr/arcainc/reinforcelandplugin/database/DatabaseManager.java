/**
 * Manages the SQLite database for the ReinforceLandPlugin, including reinforced blocks,
 * ownership information, and sharing relationships.
 */
package fr.arcainc.reinforcelandplugin.database;

import fr.arcainc.reinforcelandplugin.ReinforceLandPlugin;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                    "health INT NOT NULL" +
                    "owner VARCHAR(255) NOT NULL" +
                    ");";

            // Create the table for sharing relationships
            String createRelationTableSQL = "CREATE TABLE IF NOT EXISTS share_relations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "player_id INT NOT NULL," +
                    "target_player_id INT NOT NULL," +
                    "share_storage BOOLEAN NOT NULL DEFAULT 0," +
                    "share_beak_bypass BOOLEAN NOT NULL DEFAULT 0," +
                    "share_add_health BOOLEAN NOT NULL DEFAULT 0," +
                    "share_use BOOLEAN NOT NULL DEFAULT 0," +
                    "FOREIGN KEY (player_id) REFERENCES players(id)," +
                    "FOREIGN KEY (target_player_id) REFERENCES players(id)" +
                    ");";

            try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(createRelationTableSQL)) {
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
    public boolean isBlockReinforced(int x, int y, int z) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM reinforced_blocks WHERE x=? AND y=? AND z=?")) {

            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setInt(3, z);

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
     * Inserts a new reinforced block entry into the database.
     *
     * @param x      The x-coordinate of the block.
     * @param y      The y-coordinate of the block.
     * @param z      The z-coordinate of the block.
     * @param health The health of the reinforced block.
     */
    public void insertReinforcedBlock(int x, int y, int z, int health) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO reinforced_blocks (x, y, z, health) VALUES (?, ?, ?, ?)")) {

            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setInt(3, z);
            statement.setInt(4, health);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    public void insertReinforcedBlockOwned(int x, int y, int z, int health, Player player) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO reinforced_blocks (x, y, z, health, player) VALUES (?, ?, ?, ?, ?)")) {

            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setInt(3, z);
            statement.setInt(4, health);
            statement.setString(5, String.valueOf(player.getUniqueId()));

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
    public void addHealthToReinforcedBlock(int x, int y, int z, int healthToAdd) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("UPDATE reinforced_blocks SET health = health + ? WHERE x=? AND y=? AND z=?")) {

            statement.setInt(1, healthToAdd);
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);

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
    public void subtractHealthFromReinforcedBlock(int x, int y, int z, int healthToSubtract) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("UPDATE reinforced_blocks SET health = health - ? WHERE x=? AND y=? AND z=?")) {

            statement.setInt(1, healthToSubtract);
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);

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
    public void removeReinforcedBlock(int x, int y, int z) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM reinforced_blocks WHERE x=? AND y=? AND z=?")) {

            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setInt(3, z);

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
    public void setOwnerForReinforcedBlock(int x, int y, int z, String owner) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("UPDATE reinforced_blocks SET owner = ? WHERE x=? AND y=? AND z=?")) {

            statement.setString(1, owner);
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);

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
    public String getOwnerForBlock(int x, int y, int z) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("SELECT owner FROM reinforced_blocks WHERE x=? AND y=? AND z=?")) {

            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setInt(3, z);

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
    public void setShareRelations(int playerId, int targetPlayerId, boolean shareStorage, boolean shareBeakBypass, boolean shareAddHealth, boolean shareUse) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO share_relations (player_id, target_player_id, share_storage, share_beak_bypass, share_add_health, share_use) VALUES (?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, playerId);
            statement.setInt(2, targetPlayerId);
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
    public void removeShareRelations(int playerId, int targetPlayerId) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM share_relations WHERE player_id=? AND target_player_id=?")) {

            statement.setInt(1, playerId);
            statement.setInt(2, targetPlayerId);

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
    public boolean hasSharePermission(int playerId, int targetPlayerId, SharePermission permission) {
        try (Connection connection = DriverManager.getConnection(databaseURL);
             PreparedStatement statement = connection.prepareStatement("SELECT " + permission.getPermissionName() + " FROM share_relations WHERE player_id=? AND target_player_id=?")) {

            statement.setInt(1, playerId);
            statement.setInt(2, targetPlayerId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(permission.getPermissionName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Returns false if the sharing relationship is not found or in case of an error.
    }
}
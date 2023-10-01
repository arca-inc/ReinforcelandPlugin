package fr.arcainc.reinforcelandplugin.database;

import fr.arcainc.reinforcelandplugin.ReinforceLandPlugin;

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

    public void createTables() {
        try (Connection connection = DriverManager.getConnection(databaseURL)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS reinforced_blocks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "x INT NOT NULL," +
                    "y INT NOT NULL," +
                    "z INT NOT NULL," +
                    "health INT NOT NULL" +
                    ");";

            try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
}
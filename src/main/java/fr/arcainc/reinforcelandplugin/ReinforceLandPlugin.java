package fr.arcainc.reinforcelandplugin;

import fr.arcainc.reinforcelandplugin.commands.ReinforceCommand;
import fr.arcainc.reinforcelandplugin.config.ConfigManager;
import fr.arcainc.reinforcelandplugin.database.DatabaseManager;
import fr.arcainc.reinforcelandplugin.listener.BlockEvent;
import fr.arcainc.reinforcelandplugin.utils.ArmorStandUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class ReinforceLandPlugin extends JavaPlugin {
    public List<Player> playersInReinforceMode; // A list for players in reinforce mode
    public DatabaseManager database;

    BlockEvent event;

    @Override
    public void onEnable() {
        // Plugin initialization
        // Load the configuration
        ConfigManager.loadConfig(this);

        database = new DatabaseManager(this);

        // Register event handlers
        event = new BlockEvent(this);
        getServer().getPluginManager().registerEvents(event, this);

        // Register the /reinforce command
        getCommand("reinforce").setExecutor(new ReinforceCommand(this));

        getLogger().info("ReinforceLandPlugin is enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        event.cancelUpdateTask();
        ArmorStandUtil.armorStands.forEach(Entity::remove);
        getLogger().info("ReinforceLandPlugin is disabled.");
    }

    // Add methods to check if a player is in reinforce mode
    public boolean isInReinforceMode(Player player) {
        return this.playersInReinforceMode.contains(player);
    }
}
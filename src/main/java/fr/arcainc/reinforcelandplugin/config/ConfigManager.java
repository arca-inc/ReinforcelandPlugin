package fr.arcainc.reinforcelandplugin.config;

import fr.arcainc.reinforcelandplugin.ReinforceLandPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    public static Map<Material, Integer> healthItems = new HashMap<>();

    public static void loadConfig(ReinforceLandPlugin plugin) {
        plugin.saveDefaultConfig();
        healthItems = loadCustomHealthValues(plugin);
    }

    public static Map<Material, Integer> loadCustomHealthValues(ReinforceLandPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        Map<Material, Integer> customHealthValues = new HashMap<>();

        // Read each entry from the reinforce configuration
        ConfigurationSection reinforceSection = config.getConfigurationSection("reinforce");
        if (reinforceSection != null) {
            for (String itemName : reinforceSection.getKeys(false)) {
                Material material = Material.matchMaterial(itemName);

                // Check if the item name matches a valid material
                if (material != null) {
                    int health = config.getInt("reinforce." + itemName);
                    customHealthValues.put(material, health);
                } else {
                    plugin.getLogger().warning("Unconfigured item in config.yml: " + itemName);
                }
            }
        }

        return customHealthValues;
    }

    public static Map<Material, Integer> getHealthItems() {
        return healthItems;
    }

    public static FileConfiguration getConfig(ReinforceLandPlugin plugin) {
        return plugin.getConfig();
    }
}
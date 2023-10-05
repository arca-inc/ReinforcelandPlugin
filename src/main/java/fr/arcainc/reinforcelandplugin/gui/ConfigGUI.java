package fr.arcainc.reinforcelandplugin.gui;

import fr.arcainc.reinforcelandplugin.ReinforceLandPlugin;
import fr.arcainc.reinforcelandplugin.config.HealthDisplay;
import fr.arcainc.reinforcelandplugin.config.ModeDisplay;
import fr.arcainc.reinforcelandplugin.utils.CustomColor;
import fr.arcainc.reinforcelandplugin.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigGUI extends GUI {

    public Map<Player, String> to_trust = new HashMap<>();

    public ConfigGUI() {
        super("Config", 9);
    }

    @Override
    public void initialize() {
        // Initialisation de l'inventaire
        CustomColor greenColor = CustomColor.LIME; // Vert
        ItemStack HealthDisplay = ItemStackUtils.createColoredGlassPane(greenColor);
        ItemMeta metaHealth = ItemStackUtils.createItemMetaData(HealthDisplay, "&aHealth Display", 1, true);
        List<String> loreHealth = new ArrayList<>();
        loreHealth.add("Mode: " + ReinforceLandPlugin.getInstance().database.getPlayerHealthDisplay(String.valueOf(player.getUniqueId())));
        metaHealth.setLore(loreHealth);
        HealthDisplay.setItemMeta(metaHealth);
        inventory.setItem(3, HealthDisplay);

        ItemStack modeDisplay = ItemStackUtils.createColoredGlassPane(greenColor);
        ItemMeta metaMode = ItemStackUtils.createItemMetaData(modeDisplay, "&aMode Display", 1, true);
        List<String> loreMode = new ArrayList<>();
        loreMode.add("Mode: " + ReinforceLandPlugin.getInstance().database.getPlayerModeDisplay(String.valueOf(player.getUniqueId())));
        metaMode.setLore(loreMode);
        modeDisplay.setItemMeta(metaHealth);
        inventory.setItem(4, modeDisplay);

        ItemStack confirm = ItemStackUtils.createColoredGlassPane(greenColor);
        confirm.setItemMeta(ItemStackUtils.createItemMetaData(confirm, "&aConfirm", 1, true));
        inventory.setItem(8, confirm);
    }

    @Override
    public void handleClick(int slot, InventoryClickEvent event) {
        event.setCancelled(true);

        switch (slot) {
            case 3:
                toggleHealthDisplay();
                break;

            case 4:
                toggleModeDisplay();
                break;

            case 8:
                close();
                break;

            default:
                break;
        }
    }


    @Override
    public void update() {
        CustomColor greenColor = CustomColor.LIME; // Vert
        ItemStack HealthDisplay = ItemStackUtils.createColoredGlassPane(greenColor);
        ItemMeta metaHealth = ItemStackUtils.createItemMetaData(HealthDisplay, "&aHealth Display", 1, true);
        List<String> loreHealth = new ArrayList<>();
        loreHealth.add("Mode: " + ReinforceLandPlugin.getInstance().database.getPlayerHealthDisplay(String.valueOf(player.getUniqueId())));
        metaHealth.setLore(loreHealth);
        HealthDisplay.setItemMeta(metaHealth);
        inventory.setItem(3, HealthDisplay);

        ItemStack modeDisplay = ItemStackUtils.createColoredGlassPane(greenColor);
        ItemMeta metaMode = ItemStackUtils.createItemMetaData(modeDisplay, "&aMode Display", 1, true);
        List<String> loreMode = new ArrayList<>();
        loreMode.add("Mode: " + ReinforceLandPlugin.getInstance().database.getPlayerModeDisplay(String.valueOf(player.getUniqueId())));
        metaMode.setLore(loreMode);
        modeDisplay.setItemMeta(metaMode);
        inventory.setItem(4, modeDisplay);
    }

    private void toggleHealthDisplay() {
        HealthDisplay currentHealthDisplay = ReinforceLandPlugin.getInstance().database.getPlayerHealthDisplay(String.valueOf(player.getUniqueId()));

        if (currentHealthDisplay == HealthDisplay.HOLO) {
            ReinforceLandPlugin.getInstance().database.setPlayerConfig(String.valueOf(player.getUniqueId()), HealthDisplay.BOSS_BAR, ReinforceLandPlugin.getInstance().database.getPlayerModeDisplay(String.valueOf(player.getUniqueId())));
        } else if (currentHealthDisplay == HealthDisplay.BOSS_BAR) {
            ReinforceLandPlugin.getInstance().database.setPlayerConfig(String.valueOf(player.getUniqueId()), HealthDisplay.NONE, ReinforceLandPlugin.getInstance().database.getPlayerModeDisplay(String.valueOf(player.getUniqueId())));
        } else if (currentHealthDisplay == HealthDisplay.NONE) {
            ReinforceLandPlugin.getInstance().database.setPlayerConfig(String.valueOf(player.getUniqueId()), HealthDisplay.HOLO, ReinforceLandPlugin.getInstance().database.getPlayerModeDisplay(String.valueOf(player.getUniqueId())));
        }
    }

    private void toggleModeDisplay() {
        ModeDisplay currentModeDisplay = ReinforceLandPlugin.getInstance().database.getPlayerModeDisplay(String.valueOf(player.getUniqueId()));

        if (currentModeDisplay == ModeDisplay.ACTION_BAR) {
            ReinforceLandPlugin.getInstance().database.setPlayerConfig(String.valueOf(player.getUniqueId()), ReinforceLandPlugin.getInstance().database.getPlayerHealthDisplay(String.valueOf(player.getUniqueId())), ModeDisplay.CHAT);
        } else if (currentModeDisplay == ModeDisplay.CHAT) {
            ReinforceLandPlugin.getInstance().database.setPlayerConfig(String.valueOf(player.getUniqueId()), ReinforceLandPlugin.getInstance().database.getPlayerHealthDisplay(String.valueOf(player.getUniqueId())), ModeDisplay.BOSS_BAR);
        } else if (currentModeDisplay == ModeDisplay.BOSS_BAR) {
            ReinforceLandPlugin.getInstance().database.setPlayerConfig(String.valueOf(player.getUniqueId()), ReinforceLandPlugin.getInstance().database.getPlayerHealthDisplay(String.valueOf(player.getUniqueId())), ModeDisplay.ACTION_BAR);
        }
    }
}

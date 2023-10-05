package fr.arcainc.reinforcelandplugin.gui;

import fr.arcainc.reinforcelandplugin.ReinforceLandPlugin;
import fr.arcainc.reinforcelandplugin.utils.CustomColor;
import fr.arcainc.reinforcelandplugin.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TrustGUI extends GUI {

    public Map<Player, String> to_trust = new HashMap<>();

    public TrustGUI() {
        super("Trust Player", 27); // 3 rangées (3x9)
    }


    @Override
    public void openWithVarString(Player player, String to_trust) {
        super.open(player);
        this.to_trust.put(player, to_trust);
    }

    @Override
    public void initialize() {
        // Initialisation de l'inventaire
        CustomColor greenColor = CustomColor.LIME; // Vert
        ItemStack trustAllPermissions = ItemStackUtils.createColoredGlassPane(greenColor);
        trustAllPermissions.setItemMeta(ItemStackUtils.createItemMetaData(trustAllPermissions, "&aTrust all", 1, true));
        inventory.setItem(11, trustAllPermissions); // Placez l'élément dans la case 11

        CustomColor blueColor = CustomColor.BLUE; // Bleu
        ItemStack selectSpecifiedPermissions = ItemStackUtils.createColoredGlassPane(blueColor);
        selectSpecifiedPermissions.setItemMeta(ItemStackUtils.createItemMetaData(selectSpecifiedPermissions, "&aTrust Specified Permissions", 1, true));
        inventory.setItem(13, selectSpecifiedPermissions); // Placez l'élément dans la case 13

        CustomColor redColor = CustomColor.RED; // Rouge
        ItemStack cancel = ItemStackUtils.createColoredGlassPane(redColor);
        cancel.setItemMeta(ItemStackUtils.createItemMetaData(cancel, "&cCancel", 1, true));
        inventory.setItem(15, cancel); // Placez l'élément dans la case 15

        // Remplissez les cases vides par du verre gris clair
        CustomColor lightGrayColor = CustomColor.SILVER; // Gris clair
        ItemStack lightGrayGlassPane = ItemStackUtils.createColoredGlassPane(lightGrayColor);
        lightGrayGlassPane.setItemMeta(ItemStackUtils.createItemMetaData(lightGrayGlassPane, "&a"));
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, lightGrayGlassPane);
            }
        }
    }

    @Override
    public void handleClick(int slot, InventoryClickEvent event) {
        switch (slot) {
            case 11:
                event.setCancelled(true);
                ReinforceLandPlugin.getInstance().database.setShareRelations(String.valueOf(player.getUniqueId()), to_trust.get(player), true, true, true, true);
                close();
                break;
            case 13:
                event.setCancelled(true);
                new AdvancedTrustGUI().openWithVarString(player, to_trust.get(player));
                break;
            case 15:
                event.setCancelled(true);
                close();
                break;
            default:
                event.setCancelled(true);
                break;
        }
    }

    @Override
    public void update() {

    }
}

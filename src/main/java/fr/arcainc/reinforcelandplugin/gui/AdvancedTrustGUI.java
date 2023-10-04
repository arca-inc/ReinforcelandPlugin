package fr.arcainc.reinforcelandplugin.gui;

import fr.arcainc.reinforcelandplugin.ReinforceLandPlugin;
import fr.arcainc.reinforcelandplugin.utils.CustomColor;
import fr.arcainc.reinforcelandplugin.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AdvancedTrustGUI extends GUI {

    public Map<Player, String> to_trust = new HashMap<>();
    boolean storage = false, break_bypass = false, health = false, use = false;
    public AdvancedTrustGUI() {
        super("Modify Trust Player", 27);
    }

    @Override
    public void openWithVarString(Player player, String to_trust) {
        super.open(player);
        this.to_trust.put(player, to_trust);
    }

    public void openModifiedPermissions(Player player, String to_trust, boolean storage, boolean break_bypass, boolean health, boolean use) {
        this.storage = storage;
        this.break_bypass = break_bypass;
        this.health = health;
        this.use = use;
    }

    @Override
    public void initialize() {
        ItemStack storageItem;
        ItemMeta storageMeta;
        if(storage){
            storageItem = ItemStackUtils.createColoredGlassPane(CustomColor.GREEN);
            storageMeta = ItemStackUtils.createItemMetaData(storageItem, ChatColor.GREEN + "Storage", 1, true);
        }
        else{
            storageItem = ItemStackUtils.createColoredGlassPane(CustomColor.RED);
            storageMeta = ItemStackUtils.createItemMetaData(storageItem, ChatColor.RED + "Storage");
        }

        storageMeta.setLore(Collections.singletonList("Allow player to access all protected storage"));
        storageItem.setItemMeta(storageMeta);
        ItemStack bypassItem;
        ItemMeta bypassMeta;
        if(break_bypass){
            bypassItem = ItemStackUtils.createColoredGlassPane(CustomColor.GREEN);
            bypassMeta = ItemStackUtils.createItemMetaData(bypassItem, ChatColor.GREEN + "Break ByPass", 1, true);
        }
        else{
            bypassItem = ItemStackUtils.createColoredGlassPane(CustomColor.RED);
            bypassMeta = ItemStackUtils.createItemMetaData(bypassItem, ChatColor.RED + "Break ByPass");
        }

        bypassMeta.setLore(Collections.singletonList("Allow player to bypass protection while breaking"));
        bypassItem.setItemMeta(bypassMeta);
        ItemStack addHealthItem;
        ItemMeta addHealthMeta;
        if(health){
            addHealthItem = ItemStackUtils.createColoredGlassPane(CustomColor.GREEN);
            addHealthMeta = ItemStackUtils.createItemMetaData(addHealthItem, ChatColor.GREEN + "Add Health", 1, true);
        }
        else{
            addHealthItem = ItemStackUtils.createColoredGlassPane(CustomColor.RED);
            addHealthMeta = ItemStackUtils.createItemMetaData(addHealthItem, ChatColor.RED + "Add Health");
        }

        addHealthMeta.setLore(Collections.singletonList("Allow player to add health to protected blocks"));
        addHealthItem.setItemMeta(addHealthMeta);
        ItemStack useItem;
        ItemMeta useMeta;
        if(use){
            useItem = ItemStackUtils.createColoredGlassPane(CustomColor.GREEN);
            useMeta = ItemStackUtils.createItemMetaData(useItem, ChatColor.GREEN + "Use", 1, true);
        }
        else{
            useItem = ItemStackUtils.createColoredGlassPane(CustomColor.RED);
            useMeta = ItemStackUtils.createItemMetaData(useItem, ChatColor.RED + "Use");
        }

        useMeta.setLore(Collections.singletonList("Allow player to use protected (redstone, door)"));
        useItem.setItemMeta(useMeta);

        ItemStack confirmItem = ItemStackUtils.createColoredGlassPane(CustomColor.GREEN);
        ItemMeta confirmMeta = ItemStackUtils.createItemMetaData(useItem, ChatColor.GREEN + "Confirm");

        confirmItem.setItemMeta(confirmMeta);

        inventory.setItem(10, storageItem);
        inventory.setItem(12, bypassItem);
        inventory.setItem(14, addHealthItem);
        inventory.setItem(16, useItem);
        inventory.setItem(22, confirmItem);

        CustomColor lightGrayColor = CustomColor.SILVER;
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
            case 10:
                event.setCancelled(true);
                storage = !storage;
                break;
            case 12:
                event.setCancelled(true);
                break_bypass = !break_bypass;
                break;
            case 14:
                health = !health;
                event.setCancelled(true);
                break;
            case 16:
                use = !use;
                event.setCancelled(true);
                break;
            case 22:
                ReinforceLandPlugin.getInstance().database.removeShareRelations(String.valueOf(player.getUniqueId()), String.valueOf(Bukkit.getPlayer(to_trust.get(player)).getUniqueId()));
                ReinforceLandPlugin.getInstance().database.setShareRelations(String.valueOf(player.getUniqueId()), String.valueOf(Bukkit.getPlayer(to_trust.get(player)).getUniqueId()), storage, break_bypass, health, use);
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
        ItemStack storageItem;
        ItemMeta storageMeta;
        if(storage){
            storageItem = ItemStackUtils.createColoredGlassPane(CustomColor.GREEN);
            storageMeta = ItemStackUtils.createItemMetaData(storageItem, ChatColor.GREEN + "Storage", 1, true);
        }
        else{
            storageItem = ItemStackUtils.createColoredGlassPane(CustomColor.RED);
            storageMeta = ItemStackUtils.createItemMetaData(storageItem, ChatColor.RED + "Storage");
        }

        storageMeta.setLore(Collections.singletonList("Allow player to access all protected storage"));
        storageItem.setItemMeta(storageMeta);
        ItemStack bypassItem;
        ItemMeta bypassMeta;
        if(break_bypass){
            bypassItem = ItemStackUtils.createColoredGlassPane(CustomColor.GREEN);
            bypassMeta = ItemStackUtils.createItemMetaData(bypassItem, ChatColor.GREEN + "Break ByPass", 1, true);
        }
        else{
            bypassItem = ItemStackUtils.createColoredGlassPane(CustomColor.RED);
            bypassMeta = ItemStackUtils.createItemMetaData(bypassItem, ChatColor.RED + "Break ByPass");
        }

        bypassMeta.setLore(Collections.singletonList("Allow player to bypass protection while breaking"));
        bypassItem.setItemMeta(bypassMeta);
        ItemStack addHealthItem;
        ItemMeta addHealthMeta;
        if(health){
            addHealthItem = ItemStackUtils.createColoredGlassPane(CustomColor.GREEN);
            addHealthMeta = ItemStackUtils.createItemMetaData(addHealthItem, ChatColor.GREEN + "Add Health", 1, true);
        }
        else{
            addHealthItem = ItemStackUtils.createColoredGlassPane(CustomColor.RED);
            addHealthMeta = ItemStackUtils.createItemMetaData(addHealthItem, ChatColor.RED + "Add Health");
        }

        addHealthMeta.setLore(Collections.singletonList("Allow player to add health to protected blocks"));
        addHealthItem.setItemMeta(addHealthMeta);
        ItemStack useItem;
        ItemMeta useMeta;
        if(use){
            useItem = ItemStackUtils.createColoredGlassPane(CustomColor.GREEN);
            useMeta = ItemStackUtils.createItemMetaData(useItem, ChatColor.GREEN + "Use", 1, true);
        }
        else{
            useItem = ItemStackUtils.createColoredGlassPane(CustomColor.RED);
            useMeta = ItemStackUtils.createItemMetaData(useItem, ChatColor.RED + "Use");
        }

        useMeta.setLore(Collections.singletonList("Allow player to use protected (redstone, door)"));
        useItem.setItemMeta(useMeta);

        inventory.setItem(10, storageItem);
        inventory.setItem(12, bypassItem);
        inventory.setItem(14, addHealthItem);
        inventory.setItem(16, useItem);
    }
}

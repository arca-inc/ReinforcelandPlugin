package fr.arcainc.reinforcelandplugin.utils;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackUtils {
    public static ItemStack createColoredGlassPane(CustomColor color) {
        byte dataValue = color.getDataValue();
        ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, dataValue);
        return glassPane;
    }

    public static ItemMeta createItemMetaData(ItemStack itemStack, String customName, int stackSize, boolean hiddenEnchant) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (customName != null) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', customName));
        }

        if (hiddenEnchant) {
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        return itemMeta;
    }

    public static ItemMeta createItemMetaData(ItemStack itemStack, String customName, int stackSize) {
        return createItemMetaData(itemStack, customName, stackSize, false);
    }

    public static ItemMeta createItemMetaData(ItemStack itemStack, String customName) {
        return createItemMetaData(itemStack, customName, 1, false);
    }
}

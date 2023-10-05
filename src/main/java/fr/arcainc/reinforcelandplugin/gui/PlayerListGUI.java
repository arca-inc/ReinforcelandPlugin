package fr.arcainc.reinforcelandplugin.gui;

import fr.arcainc.reinforcelandplugin.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerListGUI extends GUI {
    private List<String> playerList;
    private int currentPage;
    private int maxPages;

    public PlayerListGUI(List<String> playerList) {
        super("Trust Player List", 9 * 3);
        this.playerList = playerList;
        this.currentPage = 0;
        this.maxPages = (int) Math.ceil((double) playerList.size() / 4) - 1;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void handleClick(int slot, InventoryClickEvent event) {
        // Handle button clicks for next and previous pages
        if (slot == inventory.getSize() - 1) {
            // Next page button
            if (currentPage < maxPages - 1) {
                currentPage++;
            }
        } else if (slot == inventory.getSize() - 9) {
            // Previous page button
            if (currentPage > 0) {
                currentPage--;
            }
        } else if (slot >= 11 && slot <= 15) {
            // Player head slot clicked
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                String playerName = ChatColor.stripColor(clickedItem.getItemMeta().getLore().get(0));
                close();
                new AdvancedTrustGUI().openWithVarString(player.getPlayer(), playerName);
            }
        }


        event.setCancelled(true);
    }

    @Override
    public void update() {
        inventory.clear();

        // Add player heads for the current page, starting from slot 11 to 15
        int startSlot = 11;
        int pageSize = 5; // 5 player heads per page
        int startIndex = currentPage * pageSize;
        int endIndex = Math.min((currentPage + 1) * pageSize, playerList.size());

        for (int i = startIndex; i < endIndex; i++) {
            String player = playerList.get(i);
            ItemStack playerHead = createPlayerHead(player);

            // Place the player head in slots 11 to 15
            inventory.setItem(startSlot, playerHead);
            startSlot++;
        }

        // Add page navigation buttons
        if (currentPage > 0) {
            ItemStack prevPageButton = new ItemStack(Material.ARROW);
            prevPageButton.setItemMeta(ItemStackUtils.createItemMetaData(prevPageButton, "&aPrevPage"));
            prevPageButton.setAmount(currentPage);
            inventory.setItem(inventory.getSize() - 9, prevPageButton);
        }

        if (currentPage < maxPages - 1) {
            ItemStack nextPageButton = new ItemStack(Material.ARROW);
            nextPageButton.setItemMeta(ItemStackUtils.createItemMetaData(nextPageButton, "&aNextPage"));
            nextPageButton.setAmount(maxPages - currentPage - 1);
            inventory.setItem(inventory.getSize() - 1, nextPageButton);
        }

        player.updateInventory();
    }

    private ItemStack createPlayerHead(String playerName) {
        ItemStack playerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

        skullMeta.setOwner(Bukkit.getOfflinePlayer(UUID.fromString(playerName)).getName());
        skullMeta.setDisplayName(ChatColor.GREEN + Bukkit.getOfflinePlayer(UUID.fromString(playerName)).getName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + playerName);
        skullMeta.setLore(lore);

        playerHead.setItemMeta(skullMeta);
        return playerHead;
    }
}
package fr.arcainc.reinforcelandplugin.listener;

import fr.arcainc.reinforcelandplugin.ReinforceLandPlugin;
import fr.arcainc.reinforcelandplugin.config.ConfigManager;
import fr.arcainc.reinforcelandplugin.database.SharePermission;
import fr.arcainc.reinforcelandplugin.utils.ArmorStandUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class BlockEvent implements Listener {

    private BukkitTask updateTask;

    private final ReinforceLandPlugin plugin;

    public BlockEvent(ReinforceLandPlugin plugin) {
        this.plugin = plugin;
        startUpdateTask();
    }

    /**
     * Handles the event when a player breaks a reinforced block.
     *
     * @param event The BlockBreakEvent.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block breaked = event.getBlock();

        if (plugin.database.isBlockReinforced(breaked.getX(), breaked.getY(), breaked.getZ())) {
            if (plugin.database.getHealthForBlock(breaked.getX(), breaked.getY(), breaked.getZ()) > 1) {
                plugin.database.subtractHealthFromReinforcedBlock(breaked.getX(), breaked.getY(), breaked.getZ(), 1);
                event.setCancelled(true);
            } else {
                plugin.database.removeReinforcedBlock(breaked.getX(), breaked.getY(), breaked.getZ());
            }
        }
    }

    /**
     * Handles the event when a player punches a reinforced block.
     *
     * @param event The PlayerInteractEvent.
     */
    @EventHandler
    public void onBlockPunch(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (plugin.database.isBlockReinforced(block.getX(), block.getY(), block.getZ())) {
                if (plugin.database.getHealthForBlock(block.getX(), block.getY(), block.getZ()) > 1) {
                    int heal = plugin.database.getHealthForBlock(block.getX(), block.getY(), block.getZ());

                    ArmorStandUtil.sendArmorStandHologram(event.getPlayer(), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.block_health").replaceAll("%health%", String.valueOf(heal-1))), block, plugin);
                }
            }
        }
    }

    List<Player> playerList = new ArrayList<>();

    /**
     * Handles the event when a player right-clicks a block in reinforce mode.
     *
     * @param event The PlayerInteractEvent.
     */
    @EventHandler
    public void onBlockRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.isInReinforceMode(player))
            return;
        else event.setCancelled(true);

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        Material heldItem = player.getInventory().getItemInMainHand().getType();

        if (ConfigManager.getHealthItems().containsKey(heldItem)) {
            if (playerList.contains(player)) return;
            playerList.add(player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> playerList.remove(player), 5);

            int healthToAdd = ConfigManager.getHealthItems().get(heldItem);

            // Check if the block is already reinforced
            if (!plugin.database.isBlockReinforced(block.getX(), block.getY(), block.getZ())) {
                // If not reinforced, insert it into the database
                // Send a message to the player
                if (consumeItem(player, heldItem, 1)) {
                    plugin.database.insertReinforcedBlockOwned(block.getX(), block.getY(), block.getZ(), healthToAdd, player);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.prefix") + " " + ChatColor.RESET + plugin.getConfig().getString("messages.block_reinforce")).replaceAll("%health_to_add%", String.valueOf(healthToAdd)));
                    event.setCancelled(true);
                }
            } else {
                String owner = plugin.database.getOwnerForBlock(block.getX(), block.getY(), block.getZ());
                int health = plugin.database.getHealthForBlock(block.getX(), block.getY(), block.getZ());

                // If reinforced and owned by the player, add more health to the block
                if (owner.equalsIgnoreCase(String.valueOf(player.getUniqueId()))) {
                    if ((healthToAdd + health) > plugin.getConfig().getInt("config.max_health")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.prefix")) + " " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.max_health_reached")).replaceAll("%max_health%", String.valueOf(plugin.getConfig().getInt("config.max_health"))));
                        event.setCancelled(true);
                        return;
                    }

                    if (consumeItem(player, heldItem, 1)) {
                        plugin.database.addHealthToReinforcedBlock(block.getX(), block.getY(), block.getZ(), healthToAdd);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.prefix") + " " + ChatColor.RESET + plugin.getConfig().getString("messages.block_reinforce")).replaceAll("%health_to_add%", String.valueOf(healthToAdd)));
                        event.setCancelled(true);
                    }
                } else if (plugin.database.hasSharePermission(owner, String.valueOf(player.getUniqueId()), SharePermission.SHARE_ADD_HEALTH)) { // Player has permission to add health
                    if (consumeItem(player, heldItem, 1)) {
                        plugin.database.addHealthToReinforcedBlock(block.getX(), block.getY(), block.getZ(), healthToAdd);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.prefix") + " " + ChatColor.RESET + plugin.getConfig().getString("messages.block_reinforce")).replaceAll("%health_to_add%", String.valueOf(healthToAdd)));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /**
     * Handles the event when a player places a block in reinforce mode.
     *
     * @param event The BlockPlaceEvent.
     */
    @EventHandler
    public void blockPlaced(BlockPlaceEvent event) {
        if (plugin.playersInReinforceMode.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles the event when a block explodes to not explode reinforced blocks.
     *
     * @param event The BlockExplodeEvent.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplode(BlockExplodeEvent event) {
        List<Block> blocksToRemove = new ArrayList<>();

        for (Block block : event.blockList()) {
            if (plugin.database.isBlockReinforced(block.getX(), block.getY(), block.getZ())) {
                if (plugin.database.getHealthForBlock(block.getX(), block.getY(), block.getZ()) > 1) {
                    plugin.database.subtractHealthFromReinforcedBlock(block.getX(), block.getY(), block.getZ(), plugin.getConfig().getInt("config.explosion.damage"));
                    blocksToRemove.add(block);
                } else {
                    plugin.database.removeReinforcedBlock(block.getX(), block.getY(), block.getZ());
                    blocksToRemove.add(block);
                }
            }
        }

        event.blockList().removeAll(blocksToRemove);
    }

    /**
     * Handles the event when an entity explodes to not explode reinforced blocks.
     *
     * @param event The EntityExplodeEvent.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blocksToRemove = new ArrayList<>();

        for (Block block : event.blockList()) {
            if (plugin.database.isBlockReinforced(block.getX(), block.getY(), block.getZ())) {
                if (plugin.database.getHealthForBlock(block.getX(), block.getY(), block.getZ()) > 1) {
                    plugin.database.subtractHealthFromReinforcedBlock(block.getX(), block.getY(), block.getZ(), plugin.getConfig().getInt("config.explosion.damage"));
                    blocksToRemove.add(block);
                } else {
                    plugin.database.removeReinforcedBlock(block.getX(), block.getY(), block.getZ());
                    blocksToRemove.add(block);
                }
            }
        }

        event.blockList().removeAll(blocksToRemove);
    }

    /**
     * Starts the task to update player's action bar to view if he's in reinforce mode and display ArmorStand with block health information.
     */
    public void startUpdateTask() {
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (plugin.isInReinforceMode(player)) {

                        Material heldItem = player.getInventory().getItemInMainHand().getType();

                        if (ConfigManager.getHealthItems().containsKey(heldItem)) {
                            int healthToAdd = ConfigManager.getHealthItems().get(heldItem);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.in_modes") + " " + plugin.getConfig().getString("messages.in_hand").replaceAll("%health_to_add%", String.valueOf(healthToAdd)))));
                        } else
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.in_modes"))));

                        if (!player.getTargetBlock(null, 5).isEmpty()) {
                            if (player.getTargetBlock(null, 5).isLiquid()) return;
                            Block block = player.getTargetBlock(null, 5);
                            if (plugin.database.isBlockReinforced(block.getX(), block.getY(), block.getZ())) {
                                int currentHealth = plugin.database.getHealthForBlock(block.getX(), block.getY(), block.getZ());
                                ArmorStandUtil.sendArmorStandHologram(player, ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.block_health").replaceAll("%health%", String.valueOf(currentHealth)))), block, plugin);
                            } else {
                                ArmorStandUtil.sendArmorStandHologram(player, ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.block_health").replaceAll("%health%", String.valueOf(1)))), block, plugin);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20); // Updates every 20 ticks (1 tick = 1/20th of a second)
    }

    /**
     * Cancels the update task to avoid error in reload.
     */
    public void cancelUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }

    /**
     * Consumes items from a player's inventory.
     *
     * @param player   The player whose inventory is being checked and modified.
     * @param material The material of the item to be consumed.
     * @param amount   The amount of the item to be consumed.
     * @return true if the player had enough of the item and it was consumed, false otherwise.
     */
    public boolean consumeItem(Player player, Material material, int amount) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemToRemove = new ItemStack(material, amount);

        // Check if the player has enough of this item
        if (playerInventory.containsAtLeast(itemToRemove, amount)) {
            playerInventory.removeItem(itemToRemove);
            return true; // The player had enough of the item and it has been consumed
        } else {
            return false; // The player did not have enough of the item
        }
    }
}
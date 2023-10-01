package fr.arcainc.reinforcelandplugin.listener;

import fr.arcainc.reinforcelandplugin.ReinforceLandPlugin;
import fr.arcainc.reinforcelandplugin.config.ConfigManager;
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

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block breaked = event.getBlock();

        if(plugin.database.isBlockReinforced(breaked.getX(), breaked.getY(), breaked.getZ())) {
            if(plugin.database.getHealthForBlock(breaked.getX(), breaked.getY(), breaked.getZ()) > 1) {
                plugin.database.subtractHealthFromReinforcedBlock(breaked.getX(), breaked.getY(), breaked.getZ(), 1);
                event.setCancelled(true);
            }else {
                plugin.database.removeReinforcedBlock(breaked.getX(), breaked.getY(), breaked.getZ());
            }
        }
    }

    @EventHandler
    public void onBlockPunch(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if(plugin.database.isBlockReinforced(block.getX(), block.getY(), block.getZ())) {
                if(plugin.database.getHealthForBlock(block.getX(), block.getY(), block.getZ()) > 1) {
                    int heal = plugin.database.getHealthForBlock(block.getX(), block.getY(), block.getZ());

                    ArmorStandUtil.sendActionBar(event.getPlayer(), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.block_health").replaceAll("%health%", String.valueOf(heal))), block, plugin);
                }
            }else {
                ArmorStandUtil.sendActionBar(event.getPlayer(), ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("messages.block_health").replaceAll("%health%", String.valueOf(1))), block, plugin);
            }}
    }

    List<Player> playerList = new ArrayList<>();

    @EventHandler
    public void onBlockRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(!plugin.isInReinforceMode(player))
            return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }



        Block block = event.getClickedBlock();

        Material heldItem = player.getInventory().getItemInMainHand().getType();

        if (ConfigManager.getHealthItems().containsKey(heldItem)) {

            if(playerList.contains(player)) return;

            playerList.add(player);


            Bukkit.getScheduler().runTaskLater(plugin, () -> playerList.remove(player), 5);

            int healthToAdd = ConfigManager.getHealthItems().get(heldItem);

            // Check if the block is already reinforced
            if (!plugin.database.isBlockReinforced(block.getX(), block.getY(), block.getZ())) {
                // If not reinforced, insert it into the database

                // Send a message to the player
                if(consumeItem(player, heldItem, 1)) {
                    plugin.database.insertReinforcedBlock(block.getX(), block.getY(), block.getZ(), healthToAdd);
                    player.sendMessage("The block has been reinforced with "+healthToAdd+" health points.");
                    event.setCancelled(true);
                }

            } else {
                // If reinforced, add more health to the block
                if(consumeItem(player, heldItem, 1)) {
                    plugin.database.addHealthToReinforcedBlock(block.getX(), block.getY(), block.getZ(), healthToAdd);
                    player.sendMessage("The block has gained "+healthToAdd+" health points.");
                    event.setCancelled(true);
                }
                // Send a message to the player

            }
        }
    }

    @EventHandler
    public void blockPlaced(BlockPlaceEvent event) {
        if(plugin.playersInReinforceMode.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplode(BlockExplodeEvent event) {
        List<Block> blocksToRemove = new ArrayList<>();

        for (Block block : event.blockList()) {
            System.out.println(block);
            if (plugin.database.isBlockReinforced(block.getX(), block.getY(), block.getZ())) {
                if (plugin.database.getHealthForBlock(block.getX(), block.getY(), block.getZ()) > 1) {
                    plugin.database.subtractHealthFromReinforcedBlock(block.getX(), block.getY(), block.getZ(), plugin.getConfig().getInt("explosion.damage"));
                    blocksToRemove.add(block);
                } else {
                    plugin.database.removeReinforcedBlock(block.getX(), block.getY(), block.getZ());
                    blocksToRemove.add(block);
                }
            }
        }

        System.out.println(blocksToRemove);

        event.blockList().removeAll(blocksToRemove);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blocksToRemove = new ArrayList<>();

        for (Block block : event.blockList()) {
            if (plugin.database.isBlockReinforced(block.getX(), block.getY(), block.getZ())) {
                if (plugin.database.getHealthForBlock(block.getX(), block.getY(), block.getZ()) > 1) {
                    plugin.database.subtractHealthFromReinforcedBlock(block.getX(), block.getY(), block.getZ(), plugin.getConfig().getInt("explosion.damage"));
                    blocksToRemove.add(block);
                } else {
                    plugin.database.removeReinforcedBlock(block.getX(), block.getY(), block.getZ());
                    blocksToRemove.add(block);
                }
            }
        }

        System.out.println(blocksToRemove);

        event.blockList().removeAll(blocksToRemove);
    }

    public void startUpdateTask() {
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(plugin.isInReinforceMode(player)) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.in_modes"))));
                        if (!player.getTargetBlock(null, 5).isEmpty()) {

                            if(player.getTargetBlock(null, 5).isLiquid()) return;

                            Block block = player.getTargetBlock(null, 5);
                            if (plugin.database.isBlockReinforced(block.getX(), block.getY(), block.getZ())) {
                                int currentHealth = plugin.database.getHealthForBlock(block.getX(), block.getY(), block.getZ());
                                ArmorStandUtil.sendActionBar(player, ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.block_health").replaceAll("%health%", String.valueOf(currentHealth)))), block, plugin);

                            } else {
                                ArmorStandUtil.sendActionBar(player, ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.block_health").replaceAll("%health%", String.valueOf(1)))), block, plugin);
                            }

                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1); // Met à jour chaque tick (1 tick = 1/20e de seconde)
    }

    public void cancelUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }

    public boolean consumeItem(Player player, Material material, int amount) {
        PlayerInventory playerInventory = player.getInventory();

        ItemStack itemToRemove = new ItemStack(material, amount);

        // Vérifiez si le joueur a suffisamment de cet item
        if (playerInventory.containsAtLeast(itemToRemove, amount)) {
            playerInventory.removeItem(itemToRemove);
            return true; // Le joueur avait suffisamment de l'item et il a été consommé
        } else {
            return false; // Le joueur n'avait pas suffisamment de l'item
        }
    }
}
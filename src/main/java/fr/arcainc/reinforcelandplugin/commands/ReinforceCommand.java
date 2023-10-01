package fr.arcainc.reinforcelandplugin.commands;

import fr.arcainc.reinforcelandplugin.ReinforceLandPlugin;
import fr.arcainc.reinforcelandplugin.gui.GuiType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import java.util.List;

public class ReinforceCommand implements CommandExecutor {

    private final ReinforceLandPlugin plugin;

    private String prefix;

    /**
     * Constructor for the ReinforceCommand class.
     *
     * @param plugin The ReinforceLandPlugin instance.
     */
    public ReinforceCommand(ReinforceLandPlugin plugin) {
        this.plugin = plugin;
        prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.prefix")) + " " + ChatColor.RESET;
        this.plugin.playersInReinforceMode = new ArrayList<>(); // Initialize the list
    }

    /**
     * Executes the /reinforce command to toggle reinforce mode for a player.
     *
     * @param sender  The command sender.
     * @param command The command.
     * @param label   The command label.
     * @param args    The command arguments.
     * @return True if the command was successfully executed.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + ChatColor.RED + "This command is reserved for players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // No arguments provided, toggle reinforce mode
            toggleReinforceMode(player);
        } else if (args.length == 1 || args.length <= 2) {
            // Check for sub-commands with arguments
            String subCommand = args[0];

            switch (subCommand.toLowerCase()) {
                case "trust":
                    if (args.length >= 2) {
                        String target = args[1];
                        if(Bukkit.getPlayer(target) == null) {
                            player.sendMessage(prefix + ChatColor.RED + "Usage: /reinforce trust <username>");
                            break;
                        }
                        trustPlayer(player, target);
                    } else {
                        player.sendMessage(prefix + ChatColor.RED + "Usage: /reinforce trust <username>");
                    }
                    break;

                case "trustlist":
                    viewTrustedPlayers(player);
                    break;

                case "untrust":
                    if (args.length >= 2) {
                        String target = args[1];
                        if(Bukkit.getPlayer(target) == null) {
                            player.sendMessage(prefix + ChatColor.RED + "Usage: /reinforce untrust <username>");
                            break;
                        }
                        untrustPlayer(player, target);
                    } else {
                        player.sendMessage(prefix + ChatColor.RED + "Usage: /reinforce untrust <username>");
                    }
                    break;

                case "help":
                    displayHelp(player);
                    break;

                default:
                    player.sendMessage(prefix + ChatColor.RED + "Unknown sub-command. Use '/reinforce help' for a list of commands.");
                    break;
            }
        } else {
            player.sendMessage(prefix + ChatColor.RED + "Usage: /reinforce [sub-command]");
        }

        return true;
    }

    /**
     * Toggles the reinforce mode for a player.
     *
     * @param player The player.
     */
    private void toggleReinforceMode(Player player) {
        // Check if the player is already in reinforce mode
        if (this.plugin.playersInReinforceMode.contains(player)) {
            // The player is already in reinforce mode, disable it
            this.plugin.playersInReinforceMode.remove(player);
            player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.removed")));
        } else {
            // The player is not in reinforce mode, add them to it
            this.plugin.playersInReinforceMode.add(player);
            player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.added")));
        }
    }

    /**
     * Displays the help message for the /reinforce command.
     *
     * @param player The player.
     */
    private void displayHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "---- Reinforce Commands ----");
        player.sendMessage("/reinforce - Toggle reinforce mode.");
        player.sendMessage("/reinforce trust <username> - Trust a player.");
        player.sendMessage("/reinforce trustlist - View your trusted players.");
        player.sendMessage("/reinforce untrust <username> - Untrust a player.");
        player.sendMessage("/reinforce config - Open GUI control all configurations.");
        player.sendMessage("/reinforce help - Display this help message.");
    }

    /**
     * Executes the /reinforce trust <username> command to trust a player.
     *
     * @param player The player executing the command.
     * @param target The player to trust.
     */
    private void trustPlayer(Player player, String target) {
        plugin.guiMap.get(GuiType.TRUST_GUI).open(player);
    }

    /**
     * Executes the /reinforce trustlist command to view trusted players.
     *
     * @param player The player executing the command.
     */
    private void viewTrustedPlayers(Player player) {
        // TODO
    }

    /**
     * Executes the /reinforce untrust <username> command to untrust a player.
     *
     * @param player The player executing the command.
     * @param target The player to untrust.
     */
    private void untrustPlayer(Player player, String target) {
        // TODO
    }
}
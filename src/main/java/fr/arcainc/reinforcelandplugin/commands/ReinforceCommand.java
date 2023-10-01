package fr.arcainc.reinforcelandplugin.commands;

import fr.arcainc.reinforcelandplugin.ReinforceLandPlugin;
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

    public ReinforceCommand(ReinforceLandPlugin plugin) {
        this.plugin = plugin;
        prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.prefix")) + " " + ChatColor.RESET;
        this.plugin.playersInReinforceMode = new ArrayList<>(); // Initialize the list
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + ChatColor.RED + "This command is reserved for players.");
            return true;
        }

        Player player = (Player) sender;

        // Check if the player is already in reinforce mode
        if (this.plugin.playersInReinforceMode.contains(player)) {
            // The player is already in reinforce mode, disable it
            this.plugin.playersInReinforceMode.remove(player);
            player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.removed")));
        } else {
            // The player is not in reinforce mode, add them to it
            this.plugin.playersInReinforceMode.add(player);
            player.sendMessage(prefix +ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.added")));
        }

        return true;
    }


}
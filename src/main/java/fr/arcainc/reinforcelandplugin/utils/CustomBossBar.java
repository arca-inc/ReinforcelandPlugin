package fr.arcainc.reinforcelandplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class CustomBossBar {

    private Map<Player, BossBar> bossBars = new HashMap<>();

    public void createBossBar(Player player, String title, BarColor color, BarStyle style) {
        BossBar bossBar = Bukkit.createBossBar(title, color, style);
        bossBar.addPlayer(player);
        bossBars.put(player, bossBar);
    }

    public void removeBossBar(Player player) {
        if (bossBars.containsKey(player)) {
            BossBar bossBar = bossBars.get(player);
            bossBar.removeAll();
            bossBars.remove(player);
        }
    }

    public void setTitle(Player player, String title) {
        if (bossBars.containsKey(player)) {
            BossBar bossBar = bossBars.get(player);
            bossBar.setTitle(title);
        }
    }

    public void setColor(Player player, BarColor color) {
        if (bossBars.containsKey(player)) {
            BossBar bossBar = bossBars.get(player);
            bossBar.setColor(color);
        }
    }

    public void setStyle(Player player, BarStyle style) {
        if (bossBars.containsKey(player)) {
            BossBar bossBar = bossBars.get(player);
            bossBar.setStyle(style);
        }
    }

    public void setProgress(Player player, double progress) {
        if (bossBars.containsKey(player)) {
            BossBar bossBar = bossBars.get(player);
            bossBar.setProgress(progress);
        }
    }

    public void removeAll() {
        for (BossBar bossBar : bossBars.values()) {
            bossBar.removeAll();
        }
        bossBars.clear();
    }
}
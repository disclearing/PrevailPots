package com.prevailpots.hcf.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by Spirit on 1/18/2017.
 */
public class AntiPexCrash implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPreProcess(PlayerCommandPreprocessEvent e) {
        if((e.getMessage().startsWith("/pex")) && !e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
        else if (e.getMessage().startsWith("//calc") && !e.getPlayer().isOp()) {
            Bukkit.broadcast(ChatColor.BOLD.toString() + ChatColor.RED + "Ban player " + e.getPlayer().getName() + " (E)", "report.see");
            e.setCancelled(true);
        }
    }
}

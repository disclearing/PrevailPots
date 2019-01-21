package com.prevailpots.hcf.fixes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class VoidGlitchFixListener implements Listener {


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player entity = e.getPlayer();
        if(e.getTo().getBlockY() < -2) {
            if (entity.getWorld().getEnvironment() == World.Environment.THE_END) {
                return;
            }
            final Location destination = Bukkit.getWorld("world").getSpawnLocation();
            if (destination == null) {
                return;
            }
            if (entity.teleport(destination, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
                e.setCancelled(true);
                entity.sendMessage(ChatColor.YELLOW + "You were saved from the void.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerDamage(final EntityDamageEvent event) {
        if(event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            final Entity entity = event.getEntity();
            if(entity instanceof Player) {
                if(entity.getWorld().getEnvironment() == World.Environment.THE_END) {
                    return;
                }
                final Location destination = Bukkit.getWorld("world").getSpawnLocation();
                if(destination == null) {
                    return;
                }
                if(entity.teleport(destination, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
                    event.setCancelled(true);
                    ((CommandSender) entity).sendMessage(ChatColor.YELLOW + "You were saved from the void.");
                }
            }
        }
    }
}

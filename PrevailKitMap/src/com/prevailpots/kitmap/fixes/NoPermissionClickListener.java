package com.prevailpots.kitmap.fixes;

import com.customhcf.base.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class NoPermissionClickListener implements Listener{
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        final Player player = e.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE && !player.hasPermission("command.gamemode")  && !BasePlugin.getPlugin().getStaffModeManager().isInStaffMode(e.getPlayer())) {
            e.setCancelled(true);
            player.setGameMode(GameMode.SURVIVAL);
            for (Player admins: Bukkit.getServer().getOnlinePlayers()){
                if (admins.isOp()){
                    admins.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getPlayer().getName() + " was in creative mode without permission!");
                }
            }
        }
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlaceCreative(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE && !player.hasPermission("command.gamemode")&& !BasePlugin.getPlugin().getStaffModeManager().isInStaffMode(event.getPlayer())) {
            event.setCancelled(true);
            player.setGameMode(GameMode.SURVIVAL);
            for (Player admins: Bukkit.getServer().getOnlinePlayers()){
                if (admins.isOp()){
                    admins.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getPlayer().getName() + " was in creative mode without permission!");
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreakCreative(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE && !player.hasPermission("command.gamemode")&& !BasePlugin.getPlugin().getStaffModeManager().isInStaffMode(event.getPlayer())) {
            event.setCancelled(true);
            player.setGameMode(GameMode.SURVIVAL);
            for (Player admins: Bukkit.getServer().getOnlinePlayers()){
                if (admins.isOp()){
                    admins.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getPlayer().getName() + " was in creative mode without permission!");
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryCreative(final InventoryCreativeEvent event) {
        final HumanEntity humanEntity = event.getWhoClicked();
        if(humanEntity instanceof Player && !humanEntity.hasPermission("command.gamemode")&& !BasePlugin.getPlugin().getStaffModeManager().isInStaffMode(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            humanEntity.setGameMode(GameMode.SURVIVAL);
            for (Player admins: Bukkit.getServer().getOnlinePlayers()){
                if (admins.isOp()){
                    admins.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + ((Player) humanEntity).getPlayer().getName() + " was in creative mode without permission!");
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerLoginEvent e){
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE && !e.getPlayer().hasPermission("command.gamemode")){
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            for (Player admins: Bukkit.getServer().getOnlinePlayers()){
                if (admins.isOp()){
                    admins.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + e.getPlayer().getName() + " joined in creative mode without permission!");
                }
            }
        }
    }
}

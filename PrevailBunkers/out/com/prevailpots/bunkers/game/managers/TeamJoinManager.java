package com.prevailpots.bunkers.game.managers;

import com.prevailpots.bunkers.Core;
import com.prevailpots.bunkers.game.GameState;
import com.prevailpots.bunkers.game.Team;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Wool;

/**
 * Created by Spirit  on 09/08/2017.
 */
public class TeamJoinManager implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(Core.getInstance().getGameHandler().getGameState().equals(GameState.LOBBY)) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (e.getPlayer().getItemInHand().getType() == Material.WOOL) {
                    Wool wool = (Wool) e.getPlayer().getItemInHand().getData();
                    // blue green red yellow 
                    if(Core.getInstance().getGameHandler().players.containsKey(e.getPlayer().getName()))
                        Core.getInstance().getGameHandler().players.remove(e.getPlayer().getName());
                   // e.setCancelled(true);
                    if(wool.getColor() == DyeColor.BLUE) {
                        Core.getInstance().getGameHandler().players.put(e.getPlayer().getName(), Team.BLUE);
                        e.getPlayer().sendMessage(ChatColor.YELLOW + "You are now on the " + ChatColor.BLUE + "Blue" + ChatColor.YELLOW + " team.");
                    }
                    if(wool.getColor() == DyeColor.GREEN) {
                        Core.getInstance().getGameHandler().players.put(e.getPlayer().getName(), Team.GREEN);
                        e.getPlayer().sendMessage(ChatColor.YELLOW + "You are now on the " + ChatColor.GREEN + "Green" + ChatColor.YELLOW + " team.");
                    }
                    if(wool.getColor() == DyeColor.RED) {
                        Core.getInstance().getGameHandler().players.put(e.getPlayer().getName(), Team.RED);
                        e.getPlayer().sendMessage(ChatColor.YELLOW + "You are now on the " + ChatColor.RED + "Red" + ChatColor.YELLOW + " team.");
                    }
                    if(wool.getColor() == DyeColor.YELLOW) {
                        Core.getInstance().getGameHandler().players.put(e.getPlayer().getName(), Team.YELLOW);
                        e.getPlayer().sendMessage(ChatColor.YELLOW + "You are now on the " + ChatColor.GOLD + "Yellow" + ChatColor.YELLOW + " team.");
                    }
                    if(wool.getColor() == DyeColor.WHITE) {
                        Core.getInstance().getGameHandler().players.remove(e.getPlayer().getName());
                        e.getPlayer().kickPlayer("§eYou have kicked yourself from the Bunkers game!");
                        
                    }
            }
        }
    }
}
}
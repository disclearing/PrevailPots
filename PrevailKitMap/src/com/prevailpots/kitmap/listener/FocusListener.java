package com.prevailpots.kitmap.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.event.FactionFocusChangeEvent;
import com.prevailpots.kitmap.faction.type.PlayerFaction;

public class FocusListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        for(PlayerFaction playerFaction : HCF.getPlugin().getFactionManager().getPlayerFactions()){
            if(playerFaction.getFocus() != null && playerFaction.getFocus() == e.getPlayer().getUniqueId()){
                Bukkit.getPluginManager().callEvent(new FactionFocusChangeEvent(playerFaction, e.getPlayer(), null));
                playerFaction.setFocus(null);
                return;
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        for(PlayerFaction playerFaction : HCF.getPlugin().getFactionManager().getPlayerFactions()){
            if(playerFaction.getFocus() != null && playerFaction.getFocus() == e.getEntity().getUniqueId()){
                Bukkit.getPluginManager().callEvent(new FactionFocusChangeEvent(playerFaction, e.getEntity(), null));
                playerFaction.setFocus(null);
                return;
            }
        }
    }
}

package com.prevailpots.kitmap.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.classes.ClassType;

public class ItemFilterListener implements Listener {

    private HCF plugin;

    public ItemFilterListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        if(!plugin.getUserManager().getUser(e.getPlayer().getUniqueId()).isMobdrops()){
            if(e.getItemDrop().getItemStack().getType() == Material.BONE || e.getItemDrop().getItemStack().getType() == Material.ROTTEN_FLESH || e.getItemDrop().getItemStack().getType() == Material.STRING || e.getItemDrop().getItemStack().getType() == Material.SPIDER_EYE || e.getItemDrop().getItemStack().getType() == Material.ARROW){
                e.getItemDrop().remove();
            }
        }
        if(!plugin.getUserManager().getUser(e.getPlayer().getUniqueId()).isCobblestone() &&plugin.getPvpClassManager().getEquippedClass(e.getPlayer()) != null && plugin.getPvpClassManager().getEquippedClass(e.getPlayer()).getClassType() == ClassType.MINER){
            if(e.getItemDrop().getItemStack().getType() == Material.COBBLESTONE){
                e.getItemDrop().remove();
           }
        }
    }


    @EventHandler
    public void onPickupFilteredItem(PlayerPickupItemEvent e){
        if(!plugin.getUserManager().getUser(e.getPlayer().getUniqueId()).isCobblestone() &&plugin.getPvpClassManager().getEquippedClass(e.getPlayer()) != null && plugin.getPvpClassManager().getEquippedClass(e.getPlayer()).getClassType() == ClassType.MINER){
            if(e.getItem().getItemStack().getType() == Material.COBBLESTONE){
                e.getItem().remove();
                e.setCancelled(true);
            }
        }
        if(!plugin.getUserManager().getUser(e.getPlayer().getUniqueId()).isMobdrops()){
            if(e.getItem().getItemStack().getType() == Material.BONE || e.getItem().getItemStack().getType() == Material.ROTTEN_FLESH || e.getItem().getItemStack().getType() == Material.STRING || e.getItem().getItemStack().getType() == Material.SPIDER_EYE || e.getItem().getItemStack().getType() == Material.ARROW){
                e.getItem().remove();
                e.setCancelled(true);
            }
        }
    }
}

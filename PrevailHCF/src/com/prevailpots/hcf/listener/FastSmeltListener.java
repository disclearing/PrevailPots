package com.prevailpots.hcf.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Spirit on 04/08/2017.
 */
public class FastSmeltListener implements Listener {
    short cooktime;

    @EventHandler
    public void furnaceBurn(final FurnaceBurnEvent event) {
        this.cooktime = 199;
        final Furnace furnace = (Furnace)event.getBlock().getState();
        final Location l = furnace.getLocation();
        for (final Entity e : this.getNearbyEntities(l, 5)) {
            if (e instanceof Player) {
                final Player p = (Player)e;
                if (!p.getOpenInventory().getType().equals((Object) InventoryType.FURNACE)) {
                    continue;
                }
                if (!p.hasPermission("fastsmelt.nigger")) {
                    continue;
                }
                furnace.setCookTime(this.cooktime);
            }
        }
    }

    @EventHandler
    public void furnaceSmeltEvent(final FurnaceSmeltEvent event) {
        this.cooktime = 199;
        final Furnace furnace = (Furnace)event.getBlock().getState();
        final Location l = furnace.getLocation();
        for (final Entity e : this.getNearbyEntities(l, 5)) {
            if (e instanceof Player) {
                final Player p = (Player)e;
                if (!p.getOpenInventory().getType().equals((Object)InventoryType.FURNACE)) {
                    continue;
                }
                if (!p.hasPermission("fastsmelt.nigger")) {
                    continue;
                }
                furnace.setCookTime(this.cooktime);
            }
        }
    }

    public List<Entity> getNearbyEntities(final Location l, final int size) {
        final List<Entity> entities = new ArrayList<Entity>();
        for (final Entity e : l.getWorld().getEntities()) {
            if (l.distance(e.getLocation()) <= size) {
                entities.add(e);
            }
        }
        return entities;
    }

}

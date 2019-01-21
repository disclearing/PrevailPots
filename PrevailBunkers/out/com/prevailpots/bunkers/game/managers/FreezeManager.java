package com.prevailpots.bunkers.game.managers;

import java.util.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.*;

import com.prevailpots.bunkers.*;
import com.prevailpots.bunkers.task.*;

import org.bukkit.plugin.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;

public class FreezeManager implements Listener
{
    private ArrayList<String> frozen;
    
    public FreezeManager() {
        this.frozen = new ArrayList<String>();
    }
    
    private boolean isFrozen(final Player p) {
        return this.frozen.contains(p.getName());
    }
    
    public void addFrozen(final Player p, final int seconds) {
        if (p == null) {
            return;
        }
        if (this.frozen.contains(p.getName())) {
            new BukkitRunnable() {
                public void run() {
                    if (FreezeManager.this.frozen.contains(p.getName())) {
                        FreezeManager.this.frozen.remove(p.getName());
                        if (p.getInventory() != null) {
                            p.getInventory().clear();
                        }
                    }
                }
            }.runTaskLater((Plugin)Core.getInstance(), (long)(20 * seconds));
            return;
        }
        this.frozen.add(p.getName());
        new BukkitRunnable() {
            public void run() {
                if (FreezeManager.this.frozen.contains(p.getName())) {
                    FreezeManager.this.frozen.remove(p.getName());
                    if (p.getInventory() != null) {
                        p.getInventory().clear();
                    }
                }
            }
        }.runTaskLater((Plugin)Core.getInstance(), (long)(20 * seconds));
    }
    
    public void addFrozen(final Player p, final int seconds, final DynamicTask taskLater) {
        if (p == null) {
            return;
        }
        if (this.frozen.contains(p.getName())) {
            new BukkitRunnable() {
                public void run() {
                    if (FreezeManager.this.frozen.contains(p.getName())) {
                        FreezeManager.this.frozen.remove(p.getName());
                        if (p.getInventory() != null) {
                            p.getInventory().clear();
                        }
                        taskLater.execute();
                    }
                }
            }.runTaskLater((Plugin)Core.getInstance(), (long)(20 * seconds));
            return;
        }
        this.frozen.add(p.getName());
        new BukkitRunnable() {
            public void run() {
                if (FreezeManager.this.frozen.contains(p.getName())) {
                    FreezeManager.this.frozen.remove(p.getName());
                    if (p.getInventory() != null) {
                        p.getInventory().clear();
                    }
                    taskLater.execute();
                }
            }
        }.runTaskLater((Plugin)Core.getInstance(), (long)(20 * seconds));
    }
    

    
    @EventHandler
    public void onAttack(final EntityDamageByEntityEvent e) {
        if (e.getDamager() != null && e.getDamager() instanceof Player && this.isFrozen((Player)e.getDamager())) {
            e.setCancelled(true);
        }
        if (e.getEntity() != null && e.getEntity() instanceof Player && this.isFrozen((Player)e.getEntity())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void dmg(final EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (this.isFrozen((Player)e.getEntity())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void hunger(final FoodLevelChangeEvent e) {
        e.setCancelled(true);
        e.setFoodLevel(20);
    }
    
    @EventHandler
    public void place(final BlockPlaceEvent e) {
        if (e.getPlayer() != null && this.isFrozen(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void kick(final PlayerKickEvent e) {
        if (e.getReason().toLowerCase().contains("flying is not enabled")) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void throwProjEvent(final ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player && this.isFrozen((Player)e.getEntity().getShooter())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void cons(final PlayerItemConsumeEvent e) {
        if (this.isFrozen(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void drop(final PlayerDropItemEvent e) {
        if (this.isFrozen(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void breaak(final BlockBreakEvent e) {
        if (e.getPlayer() != null && this.isFrozen(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}

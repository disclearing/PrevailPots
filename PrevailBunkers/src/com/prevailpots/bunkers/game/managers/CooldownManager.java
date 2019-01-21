package com.prevailpots.bunkers.game.managers;

import java.util.*;
import org.bukkit.scheduler.*;

import com.prevailpots.bunkers.*;

import org.bukkit.plugin.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.*;

public class CooldownManager implements Listener
{
    private Map<String, Long> enderPearl;
    private Map<String, Long> gapple;
    
    public CooldownManager() {
        this.enderPearl = new HashMap<String, Long>();
        this.gapple = new HashMap<String, Long>();
    }
    
    public boolean hasCooldown(final Player p, final Type cooldownType) {
        return (cooldownType == Type.GAPPLE) ? this.gapple.containsKey(p.getName()) : this.enderPearl.containsKey(p.getName());
    }
    
    public long getCooldown(final Player p, final Type cooldownType) {
        switch (cooldownType) {
            case GAPPLE: {
                return this.gapple.containsKey(p.getName()) ? this.gapple.get(p.getName()) : 0L;
            }
            case EPEARL: {
                return this.enderPearl.containsKey(p.getName()) ? this.enderPearl.get(p.getName()) : 0L;
            }
            default: {
                return 0L;
            }
        }
    }
    
    public String getCooldownFormatted(final Player p, final Type cooldownType) {
        return String.valueOf(this.getCooldown(p, cooldownType)) + "s";
    }
    
    private void deincrementCooldown(final Player p, final Type cooldownType) {
        switch (cooldownType) {
            case GAPPLE: {
                long old = 0L;
                if (this.gapple.get(p.getName()) != null) {
                    old = this.gapple.get(p.getName());
                }
                if (old != 0L) {
                    if (this.gapple.containsKey(p.getName())) {
                        this.gapple.remove(p.getName());
                    }
                    this.gapple.put(p.getName(), old - 1L);
                    break;
                }
                if (this.gapple.containsKey(p.getName())) {
                    this.gapple.remove(p.getName());
                    break;
                }
                break;
            }
            case EPEARL: {
                long old = 0L;
                if (this.enderPearl.get(p.getName()) != null) {
                    old = this.enderPearl.get(p.getName());
                }
                if (old != 0L) {
                    if (this.enderPearl.containsKey(p.getName())) {
                        this.enderPearl.remove(p.getName());
                    }
                    this.enderPearl.put(p.getName(), old - 1L);
                    break;
                }
                if (this.enderPearl.containsKey(p.getName())) {
                    this.enderPearl.remove(p.getName());
                    break;
                }
                break;
            }
        }
    }
    
    public void putCooldown(final Player p, final Type cooldownType, long cooldown) {
        ++cooldown;
        switch (cooldownType) {
            case GAPPLE: {
                if (this.gapple.containsKey(p.getName())) {
                    this.gapple.remove(p.getName());
                }
                this.gapple.put(p.getName(), cooldown);
                new BukkitRunnable() {
                    public void run() {
                        if (CooldownManager.this.getCooldown(p, cooldownType) == 0L) {
                            CooldownManager.this.removeCooldown(p, cooldownType);
                            p.sendMessage("§aYou can now gapple.");
                            this.cancel();
                        }
                        CooldownManager.this.deincrementCooldown(p, cooldownType);
                    }
                }.runTaskTimer((Plugin)Core.getInstance(), 0L, 20L);
                break;
            }
            case EPEARL: {
                if (this.enderPearl.containsKey(p.getName())) {
                    this.enderPearl.remove(p.getName());
                }
                this.enderPearl.put(p.getName(), cooldown);
                new BukkitRunnable() {
                    public void run() {
                        if (CooldownManager.this.getCooldown(p, cooldownType) == 0L) {
                            CooldownManager.this.removeCooldown(p, cooldownType);
                            p.sendMessage("§aYou can now enderpearl.");
                            this.cancel();
                        }
                        CooldownManager.this.deincrementCooldown(p, cooldownType);
                    }
                }.runTaskTimer((Plugin)Core.getInstance(), 0L, 20L);
                break;
            }
        }
    }
    
    public void removeCooldown(final Player p, final Type t) {
        switch (t) {
            case GAPPLE: {
                if (this.gapple.containsKey(p.getName())) {
                    this.gapple.remove(p.getName());
                    break;
                }
                break;
            }
            case EPEARL: {
                if (this.enderPearl.containsKey(p.getName())) {
                    this.enderPearl.remove(p.getName());
                    break;
                }
                break;
            }
        }
    }
    
    @EventHandler
    public void pearl(final ProjectileLaunchEvent e) {
        if (e.getEntity() == null) {
            return;
        }
        if (e.getEntity().getShooter() == null) {
            return;
        }
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }
        if (!(e.getEntity() instanceof EnderPearl)) {
            return;
        }
        if (this.hasCooldown((Player)e.getEntity().getShooter(), Type.EPEARL)) {
            e.setCancelled(true);
            ((Player)e.getEntity().getShooter()).sendMessage("§eYou are still on §9Enderpearl§e cooldown for another §9" + this.getCooldownFormatted((Player)e.getEntity().getShooter(), Type.EPEARL));
            return;
        }
        if (e.getEntity() instanceof EnderPearl && !e.isCancelled()) {
            this.putCooldown((Player)e.getEntity().getShooter(), Type.EPEARL, 16L);
        }
    }
    
    @EventHandler
    public void consume(final PlayerItemConsumeEvent e) {
        if (e.getItem() == null) {
            return;
        }
        if (e.getItem().getType() == null) {
            return;
        }
        if (e.getItem().getType() != Material.GOLDEN_APPLE) {
            return;
        }
        if (this.hasCooldown(e.getPlayer(), Type.GAPPLE)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§eYou are still on §6Apple§e cooldown for another §9" + this.getCooldownFormatted(e.getPlayer(), Type.GAPPLE));
            return;
        }
        if (!e.isCancelled()) {
            this.putCooldown(e.getPlayer(), Type.GAPPLE, 4L);
        }
    }
    
    public enum Type
    {
        GAPPLE("GAPPLE", 0), 
        EPEARL("EPEARL", 1);
        
        private Type(final String s, final int n) {
        }
    }
}

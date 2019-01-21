package com.prevailpots.kitmap.timer.type;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.classes.type.archer.ArcherClass;
import com.prevailpots.kitmap.timer.PlayerTimer;
import com.prevailpots.kitmap.timer.event.TimerExpireEvent;

/**
 * Created by HelpMe on 12/13/2015.
 */
public class ArcherTimer extends PlayerTimer implements Listener {
    @Override
    public String getScoreboardPrefix() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD;
    }
    private final HCF plugin;

    private Double ARCHER_DAMAGE;
    public ArcherTimer(final HCF plugin) {
        super("Archer Tag", MoreObjects.firstNonNull(plugin.getConfig().getLong("Timer.Archer"), TimeUnit.SECONDS.toMillis(10L)));
        this.plugin = plugin;
        ARCHER_DAMAGE = .25;
    }


    @EventHandler
    public void onExpire(TimerExpireEvent e){
        if(e.getUserUUID().isPresent()){
            if(e.getTimer().equals(this)){
                UUID userUUID = e.getUserUUID().get();
                final Player player = Bukkit.getPlayer(userUUID);
                if (player == null) {
                    return;
                }
                ArcherClass.TAGGED.remove(player.getUniqueId());
                for(Player players : Bukkit.getOnlinePlayers()){
                    plugin.getScoreboardHandler().getPlayerBoard(players.getUniqueId()).addUpdates(Sets.newHashSet(Bukkit.getOnlinePlayers()));

                }
            }
        }
    }
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e){
        Player entity;
        Entity damager;
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
            entity = (Player) e.getEntity();
            damager = (Player) e.getDamager();
            if(this.getRemaining(entity) > 0){
                Double damage = e.getDamage() * ARCHER_DAMAGE;
                e.setDamage(e.getDamage() + damage);
            }
        }
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Arrow){
            entity = (Player) e.getEntity();
            damager = (Player) ((Arrow) e.getDamager()).getShooter();
            if(damager instanceof Player) {
                if (this.getRemaining(entity) > 0) {
                    if(ArcherClass.TAGGED.get(entity.getUniqueId()).equals(damager.getUniqueId())){
                        this.setCooldown(entity, entity.getUniqueId());
                    }
                    Double damage = e.getDamage() * ARCHER_DAMAGE;
                    e.setDamage(e.getDamage() + damage);
                }
            }
        }
    }


}
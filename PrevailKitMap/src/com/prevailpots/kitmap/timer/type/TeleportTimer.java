package com.prevailpots.kitmap.timer.type;

import com.google.common.cache.CacheBuilder;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.FactionManager;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.timer.PlayerTimer;
import com.prevailpots.kitmap.timer.TimerRunnable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class TeleportTimer extends PlayerTimer implements Listener {
    private final ConcurrentMap<Object, Object> destinationMap;
    private final HCF plugin;

    public TeleportTimer(final HCF plugin) {
        super("Home", TimeUnit.SECONDS.toMillis(10L), false);
        this.plugin = plugin;
        this.destinationMap = CacheBuilder.newBuilder().expireAfterWrite(60000L, TimeUnit.MILLISECONDS).build().asMap();
    }

    public Object getDestination(final Player player) {
        return this.destinationMap.get(player.getUniqueId());
    }

    public String getScoreboardPrefix() {
        return ChatColor.DARK_AQUA.toString() + ChatColor.BOLD;
    }

    @Override
    public TimerRunnable clearCooldown(final UUID uuid) {
        final TimerRunnable runnable = super.clearCooldown(uuid);
        if(runnable != null) {
            this.destinationMap.remove(uuid);
            return runnable;
        }
        return null;
    }

    public int getNearbyEnemies(final Player player, final int distance) {
        final FactionManager factionManager = this.plugin.getFactionManager();
        final Faction playerFaction = factionManager.getPlayerFaction(player.getUniqueId());
        int count = 0;
        final Collection<Entity> nearby = (Collection<Entity>) player.getNearbyEntities((double) distance, (double) distance, (double) distance);
        for(final Entity entity : nearby) {
            if(entity instanceof Player) {
                final Player target = (Player) entity;
                if(!target.canSee(player)) {
                    continue;
                }
                if(!player.canSee(target)) {
                    continue;
                }
                final Faction targetFaction;
                if(playerFaction != null && (targetFaction = factionManager.getPlayerFaction(target.getUniqueId())) != null && targetFaction.equals(playerFaction)) {
                    continue;
                }
                ++count;
            }
        }
        return count;
    }

    public boolean teleport(final Player player, final Location location, final long millis, final String warmupMessage, final PlayerTeleportEvent.TeleportCause cause) {
        this.cancelTeleport(player, null);
        boolean result;
        if(millis <= 0L) {
            result = player.teleport(location, cause);
            this.clearCooldown(player.getUniqueId());
        } else {
            final UUID uuid = player.getUniqueId();
            player.sendMessage(warmupMessage);
            this.destinationMap.put(uuid, location.clone());
            this.setCooldown(player, uuid, millis, true);
            result = true;
        }
        return result;
    }

    public void cancelTeleport(final Player player, final String reason) {
        final UUID uuid = player.getUniqueId();
        if(this.getRemaining(uuid) > 0L) {
            this.clearCooldown(uuid);
            if(reason != null && !reason.isEmpty()) {
                player.sendMessage(reason);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        this.cancelTeleport(event.getPlayer(), ChatColor.YELLOW + "You moved a block, therefore cancelling your teleport.");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(final EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if(entity instanceof Player) {
            this.cancelTeleport((Player) entity, ChatColor.YELLOW + "You took damage, therefore cancelling your teleport.");
        }
    }

    @Override
    public void onExpire(final UUID userUUID) {
        final Player player = Bukkit.getPlayer(userUUID);
        if(player == null) {
            return;
        }
        final Location destination = (Location) this.destinationMap.remove(userUUID);
        if(destination != null) {
            destination.getChunk();
            player.playEffect(player.getLocation().clone().add(.5, 1, .5), Effect.ENDER_SIGNAL, 3);
            player.teleport(destination, PlayerTeleportEvent.TeleportCause.COMMAND);
        }
    }
}

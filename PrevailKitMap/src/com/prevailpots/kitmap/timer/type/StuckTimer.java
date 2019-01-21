package com.prevailpots.kitmap.timer.type;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.combatlog.CombatLogListener;
import com.prevailpots.kitmap.faction.LandMap;
import com.prevailpots.kitmap.timer.PlayerTimer;
import com.prevailpots.kitmap.timer.TimerRunnable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class StuckTimer extends PlayerTimer implements Listener {
    private final ConcurrentMap<Object, Object> startedLocations;

    public StuckTimer() {
        super("Stuck", MoreObjects.firstNonNull(HCF.getPlugin().getConfig().getLong("Timer.Stuck"), TimeUnit.MINUTES.toMillis(3L)), false);
        this.startedLocations = CacheBuilder.newBuilder().expireAfterWrite(this.defaultCooldown + 5000L, TimeUnit.MILLISECONDS).build().asMap();
    }

    public String getScoreboardPrefix() {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD;
    }

    @Override
    public TimerRunnable clearCooldown(final UUID uuid) {
        final TimerRunnable runnable = super.clearCooldown(uuid);
        if(runnable != null) {
            this.startedLocations.remove(uuid);
            return runnable;
        }
        return null;
    }

    @Override
    public boolean setCooldown(@Nullable final Player player, final UUID playerUUID, final long millis, final boolean force) {
        if(player != null && super.setCooldown(player, playerUUID, millis, force)) {
            this.startedLocations.put(playerUUID, player.getLocation());
            return true;
        }
        return false;
    }
    public void run(){

    }

    private void checkMovement(final Player player, final Location from, final Location to) {
        final UUID uuid = player.getUniqueId();
        if(this.getRemaining(uuid) > 0L) {
            if(from == null) {
                this.clearCooldown(uuid);
                return;
            }
            final int xDiff = Math.abs(from.getBlockX() - to.getBlockX());
            final int yDiff = Math.abs(from.getBlockY() - to.getBlockY());
            final int zDiff = Math.abs(from.getBlockZ() - to.getBlockZ());
            if(xDiff > 5 || yDiff > 5 || zDiff > 5) {
                this.clearCooldown(uuid);
                player.sendMessage(ChatColor.RED + "You moved more than " + ChatColor.BOLD + 5 + ChatColor.RED + " blocks. " + this.getDisplayName() + ChatColor.RED + " timer ended.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if(this.getRemaining(uuid) > 0L) {
            final Location from = (Location) this.startedLocations.get(uuid);
            this.checkMovement(player, from, event.getTo());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if(this.getRemaining(uuid) > 0L) {
            final Location from = (Location) this.startedLocations.get(uuid);
            this.checkMovement(player, from, event.getTo());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        if(this.getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            this.clearCooldown(uuid);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        if(this.getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            this.clearCooldown(uuid);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(final EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if(entity instanceof Player) {
            final Player player = (Player) entity;
            if(this.getRemaining(player) > 0L) {
                player.sendMessage(ChatColor.RED + "You were damaged, " + this.getDisplayName() + ChatColor.RED + " timer ended.");
                this.clearCooldown(player);
            }
        }
    }

    @Override
    public void onExpire(final UUID userUUID) {
        final Player player = Bukkit.getPlayer(userUUID);
        if(player == null) {
            return;
        }
        final Location nearest = LandMap.getNearestSafePosition(player, player.getLocation(), 1);
        if(nearest == null || nearest.add(0, 1.5, 0).getBlock().getType() != Material.AIR) {
            player.teleport(MoreObjects.firstNonNull(HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId()).getHome(), player.getWorld().getSpawnLocation()), PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.sendMessage(ChatColor.RED + "We were unable to find a safe location.");
            return;
        }
        if(player.teleport(nearest.add(0, 1.5, 0), PlayerTeleportEvent.TeleportCause.PLUGIN)) {
            player.sendMessage(ChatColor.YELLOW + this.getDisplayName() + ChatColor.YELLOW + " timer has teleported you to the nearest safe area.");
        }
    }

    public void run(final Player player) {
        final long remainingMillis = this.getRemaining(player);
        if(remainingMillis > 0L) {
            player.sendMessage(this.getDisplayName() + ChatColor.BLUE + " timer is teleporting you in " + ChatColor.BOLD + HCF.getRemaining(remainingMillis, true, false) + ChatColor.BLUE + '.');
        }
    }
}

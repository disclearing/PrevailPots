package com.prevailpots.kitmap.timer.type;

import com.customhcf.base.kit.event.KitApplyEvent;
import com.customhcf.util.BukkitUtils;
import com.google.common.base.MoreObjects;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.event.PlayerClaimEnterEvent;
import com.prevailpots.kitmap.faction.event.PlayerJoinFactionEvent;
import com.prevailpots.kitmap.faction.event.PlayerLeaveFactionEvent;
import com.prevailpots.kitmap.timer.PlayerTimer;
import com.prevailpots.kitmap.timer.event.TimerClearEvent;
import com.prevailpots.kitmap.timer.event.TimerStartEvent;
import com.prevailpots.kitmap.visualise.VisualType;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SpawnTagTimer extends PlayerTimer implements Listener {
    private static final long NON_WEAPON_TAG = 5000L;
    private final HCF plugin;

    public SpawnTagTimer(final HCF plugin) {
        super("Spawn Tag", MoreObjects.firstNonNull(plugin.getConfig().getLong("Timer.SpawnTag"), TimeUnit.SECONDS.toMillis(30L)));
        this.plugin = plugin;
    }

    public String getScoreboardPrefix() {
        return ChatColor.RED.toString() + ChatColor.BOLD;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onKitApply(final KitApplyEvent event) {
        final Player player = event.getPlayer();
        final long remaining;
        if(!event.isForce() && (remaining = this.getRemaining(player)) > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot apply kits whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStop(final TimerClearEvent event) {
        if(event.getTimer().equals(this)) {
            final com.google.common.base.Optional<UUID> optionalUserUUID = event.getUserUUID();
            if(optionalUserUUID.isPresent()) {
                this.onExpire((UUID) optionalUserUUID.get());
            }
        }
    }

    @Override
    public void onExpire(final UUID userUUID) {
        final Player player = Bukkit.getPlayer(userUUID);
        if(player == null) {
            return;
        }
        player.sendMessage(ChatColor.GREEN + "You no longer have your " + this.getDisplayName() + "!");
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.SPAWN_BORDER, null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionJoin(final PlayerJoinFactionEvent event) {
        final com.google.common.base.Optional<Player> optional = event.getPlayer();
        if(optional.isPresent()) {
            final Player player = (Player) optional.get();
            final long remaining = this.getRemaining(player);
            if(remaining > 0L) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot join factions whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionLeave(final PlayerLeaveFactionEvent event) {
        final com.google.common.base.Optional<Player> optional = event.getPlayer();
        if(optional.isPresent()) {
            final Player player = (Player) optional.get();
            if(this.getRemaining(player) > 0L) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot join factions whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
            }
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPreventClaimEnter(final PlayerClaimEnterEvent event) {
        if(event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
            return;
        }
        final Player player = event.getPlayer();
        if(!event.getFromFaction().isSafezone() && event.getToFaction().isSafezone() && this.getRemaining(player) > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot enter " + event.getToFaction().getDisplayName((CommandSender) player) + ChatColor.RED + " whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent) event, true);
        final Entity entity;
        if(attacker != null && (entity = event.getEntity()) instanceof Player) {
            final Player attacked = (Player) entity;
            boolean weapon = event.getDamager() instanceof Arrow;
            if(!weapon) {
                final ItemStack stack = attacker.getItemInHand();
                weapon = (stack != null && EnchantmentTarget.WEAPON.includes(stack));
            }
            final long duration = weapon ? this.defaultCooldown : NON_WEAPON_TAG;
            this.setCooldown(attacked, attacked.getUniqueId(), Math.max(this.getRemaining(attacked), duration), true);
            this.setCooldown(attacker, attacker.getUniqueId(), Math.max(this.getRemaining(attacker), duration), true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStart(final TimerStartEvent event) {
        if(event.getTimer().equals(this)) {
            final com.google.common.base.Optional<Player> optional = event.getPlayer();
            if(optional.isPresent()) {
                final Player player = (Player) optional.get();
                player.sendMessage(ChatColor.YELLOW + "You are now spawn-tagged for " + ChatColor.GOLD + DurationFormatUtils.formatDurationWords(event.getDuration(), true, true) + ChatColor.GOLD + '.');
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        this.clearCooldown(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPreventClaimEnterMonitor(final PlayerClaimEnterEvent event) {
        if(event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && !event.getFromFaction().isSafezone() && event.getToFaction().isSafezone()) {
            this.clearCooldown(event.getPlayer());
        }
    }
}

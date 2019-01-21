package com.prevailpots.kitmap.timer.type;

import com.customhcf.util.BukkitUtils;
import com.customhcf.util.Config;
import com.customhcf.util.GenericUtils;
import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.claim.Claim;
import com.prevailpots.kitmap.faction.event.FactionClaimChangedEvent;
import com.prevailpots.kitmap.faction.event.PlayerClaimEnterEvent;
import com.prevailpots.kitmap.faction.event.cause.ClaimChangeCause;
import com.prevailpots.kitmap.faction.type.ClaimableFaction;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.faction.type.PlayerFaction;
import com.prevailpots.kitmap.faction.type.RoadFaction;
import com.prevailpots.kitmap.timer.PlayerTimer;
import com.prevailpots.kitmap.timer.TimerRunnable;
import com.prevailpots.kitmap.timer.event.TimerClearEvent;
import com.prevailpots.kitmap.visualise.VisualType;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class PvpProtectionTimer extends PlayerTimer implements Listener {
    private static final String PVP_COMMAND = "/pvp enable";
    private static final long ITEM_PICKUP_DELAY;
    private static final long ITEM_PICKUP_MESSAGE_DELAY = 1250L;
    private static final String ITEM_PICKUP_MESSAGE_META_KEY = "pickupMessageDelay";

    static {
        ITEM_PICKUP_DELAY = TimeUnit.SECONDS.toMillis(30L);
    }

    private final ConcurrentMap<Object, Object> itemUUIDPickupDelays;
    private final HCF plugin;

    public PvpProtectionTimer(final HCF plugin) {
        super("PvP Timer", TimeUnit.MINUTES.toMillis(30));
        this.plugin = plugin;
        this.itemUUIDPickupDelays = CacheBuilder.newBuilder().expireAfterWrite(PvpProtectionTimer.ITEM_PICKUP_DELAY + 5000L, TimeUnit.MILLISECONDS).build().asMap();
    }

    public String getScoreboardPrefix() {
        return ChatColor.GREEN.toString() + ChatColor.BOLD;
    }

    @Override
    public void onExpire(final UUID userUUID) {
        final Player player = Bukkit.getPlayer(userUUID);
        if(player == null) {
            return;
        }
        if(this.getRemaining(player) <= 0) {
            this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CLAIM_BORDER, null);
            player.sendMessage(ChatColor.WHITE.toString() + "You no longer have "+ this.getDisplayName() + ChatColor.WHITE + '.');
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onClaimChange(final FactionClaimChangedEvent event) {
        if(event.getCause() != ClaimChangeCause.CLAIM) {
            return;
        }
        final Collection<Claim> claims = event.getAffectedClaims();
        for(final Claim claim : claims) {
            final Collection<Player> players = claim.getPlayers();
            for(final Player player : players) {
                if(this.getRemaining(player) > 0L) {
                    setCooldown(player, player.getUniqueId(), 0, true);
                    player.sendMessage(ChatColor.RED + "Land was claimed where you were standing. Because of this your " + this.getName() + ", has been removed.");
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        setCooldown(player, player.getUniqueId());
        this.setPaused(player, player.getUniqueId(), true);
        player.sendMessage(ChatColor.YELLOW + "Once you leave Spawn your "+ net.md_5.bungee.api.ChatColor.GOLD+" 30 minutes "+ net.md_5.bungee.api.ChatColor.YELLOW +" of " + this.getName() + ChatColor.YELLOW + " will start.");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final World world = player.getWorld();
        final Location location = player.getLocation();
        final Iterator<ItemStack> iterator = event.getDrops().iterator();
        while(iterator.hasNext()) {
            this.itemUUIDPickupDelays.put(world.dropItemNaturally(location,  iterator.next()).getUniqueId(), System.currentTimeMillis() + PvpProtectionTimer.ITEM_PICKUP_DELAY);
            iterator.remove();
        }
        this.clearCooldown(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        final long remaining = this.getRemaining(player);
        if(remaining > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot empty buckets as your " + this.getDisplayName() + ChatColor.RED + " is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        final Player player = event.getPlayer();
        if(player == null) {
            return;
        }
        final long remaining = this.getRemaining(player);
        if(remaining > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot ignite blocks as your " + this.getDisplayName() + ChatColor.RED + " is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final TimerRunnable runnable = this.cooldowns.get(player.getUniqueId());
        if(runnable != null && runnable.getRemaining() > 0L) {
            runnable.setPaused(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerSpawnLocation(final PlayerSpawnLocationEvent event) {
        final Player player = event.getPlayer();
            if (!player.hasPlayedBefore()) {
                if (!this.plugin.getEotwHandler().isEndOfTheWorld()) {
                    setCooldown(player, player.getUniqueId());
                    this.setPaused(player, player.getUniqueId(), true);
                    player.sendMessage(ChatColor.YELLOW + "Once you leave Spawn your "+ net.md_5.bungee.api.ChatColor.GOLD+" 30 minutes "+ net.md_5.bungee.api.ChatColor.YELLOW +" of " + this.getName() + ChatColor.YELLOW + " will start.");
                }
            } else if (this.isPaused(player) && this.getRemaining(player) > 0L && !this.plugin.getFactionManager().getFactionAt(event.getSpawnLocation()).isSafezone()) {
                this.setPaused(player, player.getUniqueId(), false);
            }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerClaimEnterMonitor(final PlayerClaimEnterEvent event) {
        final Player player = event.getPlayer();
        if(event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
            this.clearCooldown(player);
            return;
        }
        final Faction toFaction = event.getToFaction();
        final Faction fromFaction = event.getFromFaction();
        if(fromFaction.isSafezone() && !toFaction.isSafezone()) {
            if(isPaused(player.getUniqueId())) {
                setPaused(player, player.getUniqueId(), false);
                player.sendMessage(ChatColor.YELLOW + "Your " + this.getDisplayName() + ChatColor.YELLOW + " has started.");
                return;
            }
            if(this.getRemaining(player) > 0L) {
                this.setPaused(player, player.getUniqueId(), false);
                player.sendMessage(ChatColor.YELLOW + "Your " + this.getDisplayName() + ChatColor.YELLOW + " is no longer paused.");
            }
        } else if(!fromFaction.isSafezone() && toFaction.isSafezone() && this.getRemaining(player) > 0L) {
            player.sendMessage(ChatColor.YELLOW + "Your " + this.getDisplayName() + ChatColor.YELLOW + " is now paused.");
            this.setPaused(player, player.getUniqueId(), true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerClaimEnter(final PlayerClaimEnterEvent event) {
        final Player player = event.getPlayer();
        final Faction toFaction = event.getToFaction();
        final long remaining;
        if(toFaction instanceof ClaimableFaction && (remaining = this.getRemaining(player)) > 0L) {
            final PlayerFaction playerFaction;
            if(event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && toFaction instanceof PlayerFaction && (playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId())) != null && playerFaction.equals(toFaction)) {
                player.sendMessage(ChatColor.YELLOW + "You have entered your own claim, therefore your " + this.getDisplayName() + ChatColor.YELLOW + " has been removed.");
                this.clearCooldown(player);
                return;
            }
            if(!toFaction.isSafezone() && !(toFaction instanceof RoadFaction)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.YELLOW + "You cannot enter " + toFaction.getDisplayName((CommandSender) player) + ChatColor.YELLOW + " whilst your " + this.getDisplayName() + ChatColor.RED + " is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]. " + "Use '" + ChatColor.GOLD + PVP_COMMAND + ChatColor.RED + "' to remove this timer.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if(entity instanceof Player) {
            final Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent) event, true);
            if(attacker == null) {
                return;
            }
            final Player player = (Player) entity;
            long remaining;
            if((remaining = this.getRemaining(player)) > 0L) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.RED + player.getName() + " has their " + this.getDisplayName() + ChatColor.RED + " for another " + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + '.');
                return;
            }
            if((remaining = this.getRemaining(attacker)) > 0L) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.RED + "You cannot attack players whilst your " + this.getDisplayName() + ChatColor.RED + " is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]. Use '" + ChatColor.GOLD + PVP_COMMAND + ChatColor.RED + "' to allow pvp.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPotionSplash(final PotionSplashEvent event) {
        final ThrownPotion potion = event.getPotion();
        if(potion.getShooter() instanceof Player && BukkitUtils.isDebuff(potion)) {
            for(final LivingEntity livingEntity : event.getAffectedEntities()) {
                if(livingEntity instanceof Player && this.getRemaining((Player) livingEntity) > 0L) {
                    event.setIntensity(livingEntity, 0.0);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @Override
    public Long getRemaining(final UUID playerUUID) {
        return this.plugin.getEotwHandler().isEndOfTheWorld() ? 0L : super.getRemaining(playerUUID);
    }

}

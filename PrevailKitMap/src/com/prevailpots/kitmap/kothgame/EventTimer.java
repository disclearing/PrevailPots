package com.prevailpots.kitmap.kothgame;

import com.customhcf.util.ParticleEffect;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.prevailpots.kitmap.DateTimeFormats;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.event.CaptureZoneEnterEvent;
import com.prevailpots.kitmap.faction.event.CaptureZoneLeaveEvent;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.faction.type.PlayerFaction;
import com.prevailpots.kitmap.key.Key;
import com.prevailpots.kitmap.kothgame.faction.ConquestFaction;
import com.prevailpots.kitmap.kothgame.faction.EventFaction;
import com.prevailpots.kitmap.kothgame.faction.KothFaction;
import com.prevailpots.kitmap.timer.GlobalTimer;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EventTimer extends GlobalTimer implements Listener {
    private static final long RESCHEDULE_FREEZE_MILLIS;
    private static final String RESCHEDULE_FREEZE_WORDS;

    static {
        RESCHEDULE_FREEZE_MILLIS = TimeUnit.SECONDS.toMillis(15L);
        RESCHEDULE_FREEZE_WORDS = DurationFormatUtils.formatDurationWords(EventTimer.RESCHEDULE_FREEZE_MILLIS, true, true);
    }

    private final HCF plugin;
    private long startStamp;
    private long lastContestedEventMillis;
    private EventFaction eventFaction;

    public EventTimer(final HCF plugin) {
        super("Event", 0L);
        this.plugin = plugin;
        new BukkitRunnable() {
            public void run() {
                if(EventTimer.this.eventFaction != null) {
                    EventTimer.this.eventFaction.getEventType().getEventTracker().tick(EventTimer.this, EventTimer.this.eventFaction);
                    return;
                }
                final LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
                final int day = now.getDayOfYear();
                final int hour = now.getHour();
                final int minute = now.getMinute();


                for(final Map.Entry<LocalDateTime, String> entry : plugin.eventScheduler.getScheduleMap().entrySet()) {
                    final LocalDateTime scheduledTime = entry.getKey();
                    if(day == scheduledTime.getDayOfYear() && hour == scheduledTime.getHour()) {
                        if(minute != scheduledTime.getMinute()) {
                            continue;
                        }
                        final Faction faction = plugin.getFactionManager().getFaction(entry.getValue());
                        if(faction instanceof EventFaction && EventTimer.this.tryContesting((EventFaction) faction, Bukkit.getConsoleSender())) {
                            break;
                        }
                        continue;
                    }
                }
            }
        }.runTaskTimer((Plugin) plugin, 20L, 20L);
    }

    public EventFaction getEventFaction() {
        return this.eventFaction;
    }

    public String getScoreboardPrefix() {
          return eventFaction.getName().contains("Palace") || eventFaction.getName().contains("Conquest") ? ChatColor.DARK_RED.toString() : ChatColor.RED.toString();
    }

    public String getName() {
        return (this.eventFaction == null) ? "Event" : this.eventFaction.getName();
    }

    @Override
    public boolean clearCooldown() {
        boolean result = super.clearCooldown();
        if(this.eventFaction != null) {
            for(final CaptureZone captureZone : this.eventFaction.getCaptureZones()) {
                captureZone.setCappingPlayer(null);
            }
            this.eventFaction.setDeathban(true);
            this.eventFaction.getEventType().getEventTracker().stopTiming();
            this.eventFaction = null;
            this.startStamp = -1L;
            result = true;
        }
        return result;
    }

    @EventHandler
    public void onDecay(LeavesDecayEvent e){
        if(plugin.getFactionManager().getFactionAt(e.getBlock()) != null){
            e.setCancelled(true);
        }
    }

    @Override
    public long getRemaining() {
        if(this.eventFaction == null) {
            return 0L;
        }
        if(this.eventFaction instanceof KothFaction) {
            return ((KothFaction) this.eventFaction).getCaptureZone().getRemainingCaptureMillis();
        }
        return super.getRemaining();
    }

    public void handleWinner(final Player winner) {
        if(this.eventFaction == null) {
            return;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner.getUniqueId());
        if(playerFaction != null) {
            if (eventFaction instanceof ConquestFaction || eventFaction.getName().contains("Palace")) {
                for (UUID on : playerFaction.getMembers().keySet()) {
                    if (Bukkit.getPlayer(on) != null) {
                        Player player = Bukkit.getPlayer(on);
                        ParticleEffect.FIREWORK_SPARK.sphere(player, player.getLocation(), 2);
                        ParticleEffect.FIRE.sphere(player, player.getLocation(), 3);
                    }
                }
            }
        }
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.GOLD + "[" + this.eventFaction.getEventType().getDisplayName() + "] " + ChatColor.RED + ((playerFaction == null) ?  winner.getName() : playerFaction.getName())  + ChatColor.YELLOW + " has captured " + ChatColor.RED + this.eventFaction.getName() + ChatColor.YELLOW + " after " + ChatColor.BOLD + DurationFormatUtils.formatDurationWords(this.getUptime(), true, true) + ChatColor.YELLOW + " of up-time" + ChatColor.YELLOW + '.');
        Bukkit.broadcastMessage("");
        final World world = winner.getWorld();
        final Location location = winner.getLocation();
        final Key key;
        if(eventFaction.getName().contains("Palace") || eventFaction.getName().contains("Conquest")){
            key = this.plugin.getKeyManager().getConquestKey();
        }else {
            key = this.plugin.getKeyManager().getKey(ChatColor.stripColor(eventFaction.getEventType().getDisplayName()));
        }        Preconditions.checkNotNull(key, "Key on: EventTime error.");
        final ItemStack stack;
        if(eventFaction.getName().contains("Palace") || eventFaction.getName().contains("Conquest")) {
            stack = key.getItemStack().clone();
            stack.setAmount(3);
        }else {
            stack = key.getItemStack().clone();
        }
        final Map<Integer, ItemStack> excess =  winner.getInventory().addItem(new ItemStack[]{stack, EventSignListener.getEventSign(this.eventFaction.getName(), winner.getName())});
        for(final ItemStack entry : excess.values()) {
            world.dropItemNaturally(location, entry);
        }
        this.clearCooldown();
    }

    public boolean tryContesting(final EventFaction eventFaction, final CommandSender sender) {
        if(this.eventFaction != null) {
            sender.sendMessage(ChatColor.RED + "There is already an active event, use /game cancel to end it.");
            return false;
        }
        if(eventFaction instanceof KothFaction) {
            final KothFaction kothFaction = (KothFaction) eventFaction;
            if(kothFaction.getCaptureZone() == null) {
                sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as its' capture zone is not set.");
                return false;
            }
        } else if(eventFaction instanceof ConquestFaction) {
            final ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
            final Collection<ConquestFaction.ConquestZone> zones = conquestFaction.getConquestZones();
            for(final ConquestFaction.ConquestZone zone : ConquestFaction.ConquestZone.values()) {
                if(!zones.contains(zone)) {
                    sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as capture zone '" + zone.getDisplayName() + ChatColor.RED + "' is not set.");
                    return false;
                }
            }
        }
        final long millis = System.currentTimeMillis();
        if(this.lastContestedEventMillis + EventTimer.RESCHEDULE_FREEZE_MILLIS - millis > 0L) {
            sender.sendMessage(ChatColor.RED + "Cannot reschedule events within " + EventTimer.RESCHEDULE_FREEZE_WORDS + '.');
            return false;
        }
        this.lastContestedEventMillis = millis;
        this.startStamp = millis;
        this.eventFaction = eventFaction;
        eventFaction.getEventType().getEventTracker().onContest(eventFaction, this);
        if(eventFaction instanceof ConquestFaction) {
            this.setRemaining(1000L, true);
            this.setPaused(true);
        }
        final Collection<CaptureZone> captureZones = eventFaction.getCaptureZones();
        for(final CaptureZone captureZone : captureZones) {
            if(captureZone.isActive()) {
                final Player player = (Player) Iterables.getFirst((Iterable) captureZone.getCuboid().getPlayers(), (Object) null);
                if(player == null || !eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)) {
                    continue;
                }
                captureZone.setCappingPlayer(player);
            }
        }
        eventFaction.setDeathban(true);
        return true;
    }

    public long getUptime() {
        return System.currentTimeMillis() - this.startStamp;
    }

    public long getStartStamp() {
        return this.startStamp;
    }

    private void handleDisconnect(final Player player) {
        Preconditions.checkNotNull((Object) player);
        if(this.eventFaction == null) {
            return;
        }
        final Collection<CaptureZone> captureZones = this.eventFaction.getCaptureZones();
        for(final CaptureZone captureZone : captureZones) {
            if(Objects.equal(captureZone.getCappingPlayer(), player)) {
                this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction);
                captureZone.setCappingPlayer(null);
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        this.handleDisconnect(event.getEntity());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        this.handleDisconnect(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        this.handleDisconnect(event.getPlayer());
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        handleDisconnect(event.getPlayer());
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerPortal(PlayerPortalEvent event) {
        handleDisconnect(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneEnter(final CaptureZoneEnterEvent event) {
        if(this.eventFaction == null) {
            return;
        }
        final CaptureZone captureZone = event.getCaptureZone();
        if(!this.eventFaction.getCaptureZones().contains(captureZone)) {
            return;
        }
        final Player player = event.getPlayer();
        if(captureZone.getCappingPlayer() == null && this.eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)) {
            captureZone.setCappingPlayer(player);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneLeave(final CaptureZoneLeaveEvent event) {
        if(Objects.equal((Object) event.getFaction(), (Object) this.eventFaction)) {
            final Player player = event.getPlayer();
            final CaptureZone captureZone = event.getCaptureZone();
            if(Objects.equal((Object) player, (Object) captureZone.getCappingPlayer()) && this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction)) {
                captureZone.setCappingPlayer(null);
                for(final Player target : captureZone.getCuboid().getPlayers()) {
                    if(target != null && !target.equals(player) && this.eventFaction.getEventType().getEventTracker().onControlTake(target, captureZone)) {
                        captureZone.setCappingPlayer(target);
                        break;
                    }
                }
            }
        }
    }
}

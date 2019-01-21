package com.prevailpots.hcf.kothgame.tracker;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.event.FactionRemoveEvent;
import com.prevailpots.hcf.faction.type.Faction;
import com.prevailpots.hcf.faction.type.PlayerFaction;
import com.prevailpots.hcf.kothgame.CaptureZone;
import com.prevailpots.hcf.kothgame.EventTimer;
import com.prevailpots.hcf.kothgame.EventType;
import com.prevailpots.hcf.kothgame.faction.ConquestFaction;
import com.prevailpots.hcf.kothgame.faction.EventFaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Deprecated
public class ConquestTracker implements EventTracker, Listener {
    public static final long DEFAULT_CAP_MILLIS;
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE;
    private static final Comparator<Map.Entry<PlayerFaction, Integer>> POINTS_COMPARATOR;

    static {
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(5L);
        DEFAULT_CAP_MILLIS = TimeUnit.SECONDS.toMillis(30L);
        POINTS_COMPARATOR = ((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
    }

    private final Map<PlayerFaction, Integer> factionPointsMap;
    private final HCF plugin;

    public ConquestTracker(final HCF ins) {
        this.factionPointsMap = Collections.synchronizedMap(new LinkedHashMap<PlayerFaction, Integer>());
        this.plugin = ins;
        Bukkit.getPluginManager().registerEvents((Listener) this, (Plugin) this.plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRemove(final FactionRemoveEvent event) {
        final Faction faction = event.getFaction();
        if(faction instanceof PlayerFaction) {
            synchronized(this.factionPointsMap) {
                this.factionPointsMap.remove(faction);
            }
        }
    }

    public Map<PlayerFaction, Integer> getFactionPointsMap() {
        return  ImmutableMap.copyOf(this.factionPointsMap);
    }

    public int getPoints(final PlayerFaction faction) {
        synchronized(this.factionPointsMap) {
            return (int) MoreObjects.firstNonNull( this.factionPointsMap.get(faction), (Object) 0);
        }
    }

    public int setPoints(final PlayerFaction faction, final int amount) {
        if(amount < 0) {
            return amount;
        }
        synchronized(this.factionPointsMap) {
            this.factionPointsMap.put(faction, amount);
            final List<Map.Entry<PlayerFaction, Integer>> entries = (List<Map.Entry<PlayerFaction, Integer>>) Ordering.from((Comparator) ConquestTracker.POINTS_COMPARATOR).sortedCopy((Iterable) this.factionPointsMap.entrySet());
            this.factionPointsMap.clear();
            for(final Map.Entry<PlayerFaction, Integer> entry : entries) {
                this.factionPointsMap.put(entry.getKey(), entry.getValue());
            }
        }
        return amount;
    }

    public int takePoints(final PlayerFaction faction, final int amount) {
        return this.setPoints(faction, getPoints(faction) - amount);
    }

    public int addPoints(final PlayerFaction faction, final int amount) {
        return this.setPoints(faction, this.getPoints(faction) + amount);
    }

    @Override
    public EventType getEventType() {
        return EventType.CONQUEST;
    }

    @Override
    public void tick(final EventTimer eventTimer, final EventFaction eventFaction) {
        final ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
        final List<CaptureZone> captureZones = conquestFaction.getCaptureZones();
        for(final CaptureZone captureZone : captureZones) {
            final Player cappingPlayer = captureZone.getCappingPlayer();
            if(cappingPlayer == null) {
                continue;
            }
            final long remainingMillis = captureZone.getRemainingCaptureMillis();
            if(remainingMillis <= 0L) {
                final UUID uuid = cappingPlayer.getUniqueId();
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
                if(playerFaction != null) {
                    final int newPoints = this.addPoints(playerFaction, 1);
                    if(newPoints >= plugin.getHcfHandler().getConquestWinPoints()) {
                        synchronized(this.factionPointsMap) {
                            this.factionPointsMap.clear();
                        }
                        this.plugin.getTimerManager().eventTimer.handleWinner(cappingPlayer);
                        return;
                    }
                    captureZone.setRemainingCaptureMillis(captureZone.getDefaultCaptureMillis());
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getName() + "] " + ChatColor.LIGHT_PURPLE + playerFaction.getName() +ChatColor.YELLOW + " gained " + 1 + " point for capturing " + captureZone.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.AQUA + '(' + newPoints + '/' + 300 + ')');
                }
                return;
            }
            final int remainingSeconds = (int) Math.round(remainingMillis / 1000.0);
            if(remainingSeconds % 5 != 0) {
                continue;
            }
            final UUID uuid = cappingPlayer.getUniqueId();
            final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
            playerFaction.broadcast(ChatColor.YELLOW + "[" + eventFaction.getName() + "] " + ChatColor.YELLOW + cappingPlayer.getName() + "'s attempting to control " + ChatColor.YELLOW + captureZone.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.YELLOW + '(' + remainingSeconds + "s)");
            cappingPlayer.sendMessage(ChatColor.YELLOW + "[" + eventFaction.getName() + "] " + ChatColor.YELLOW + "Attempting to control " + ChatColor.YELLOW + captureZone.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.YELLOW + '(' + remainingSeconds + "s)");
        }
    }

    @Override
    public void onContest(final EventFaction eventFaction, final EventTimer eventTimer) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getName() + "] " + ChatColor.YELLOW + eventFaction.getName() + " can now be contested.");
    }

    @Override
    public boolean onControlTake(final Player player, final CaptureZone captureZone) {
        if(this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to capture for Conquest.");
            return false;
        }
        return true;
    }

    @Override
    public boolean onControlLoss(final Player player, final CaptureZone captureZone, final EventFaction eventFaction) {
        final long remainingMillis = captureZone.getRemainingCaptureMillis();
        if(remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > ConquestTracker.MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getName() + "] " + ChatColor.YELLOW + player.getName() + " was knocked off " + captureZone.getDisplayName() + ChatColor.YELLOW + '.');
        }
        return true;
    }

    @Override
    public void stopTiming() {
        synchronized(this.factionPointsMap) {
            this.factionPointsMap.clear();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Faction currentEventFac = this.plugin.getTimerManager().eventTimer.getEventFaction();
        if(currentEventFac instanceof ConquestFaction) {
            final Player player = event.getEntity();
            final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
            if(playerFaction != null) {
                final int oldPoints = getPoints(playerFaction);
                if(oldPoints == 0) {
                    return;
                }
                if(getPoints(playerFaction) <= 20){
                    setPoints(playerFaction, 0);
                }else{
                    takePoints(playerFaction, 20);
                }
                Bukkit.broadcastMessage(getPoints(playerFaction) + "");
                event.setDeathMessage(ChatColor.YELLOW + "[" + currentEventFac.getName() + "] " + ChatColor.LIGHT_PURPLE + playerFaction.getName() + ChatColor.YELLOW+ " lost " + ChatColor.BOLD + Math.min(20, oldPoints)  + ChatColor.YELLOW +  " points because " + player.getName() + " died." + ChatColor.GOLD + " (" + getPoints(playerFaction) + '/' + plugin.getHcfHandler().getConquestWinPoints() + ')' + ChatColor.YELLOW + '.');
            }
        }
    }
}

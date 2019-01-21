package com.prevailpots.bunkers.scoreboard;

import org.bukkit.scheduler.*;
import java.util.concurrent.atomic.*;
import org.bukkit.entity.*;
import org.bukkit.scoreboard.*;

import com.prevailpots.bunkers.*;

import org.bukkit.plugin.*;
import java.util.*;
import org.bukkit.*;

public class PlayerBoard
{
    private boolean sidebarVisible;
    private SidebarProvider defaultProvider;
    private SidebarProvider temporaryProvider;
    private BukkitRunnable runnable;
    private final AtomicBoolean removed;
    private final Team red;
    private final Team green;
    private final Team blue;
    private final Team yellow;
    private final BufferedObjective bufferedObjective;
    private final Scoreboard scoreboard;
    private final Player player;
    private final Core plugin;
    
    public PlayerBoard(final Core plugin, final Player player) {
        this.sidebarVisible = false;
        this.removed = new AtomicBoolean(false);
        this.plugin = plugin;
        this.player = player;
        this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        this.bufferedObjective = new BufferedObjective(this.scoreboard);
        (this.red = this.scoreboard.registerNewTeam("red")).setPrefix(com.prevailpots.bunkers.game.Team.RED.getColor().toString());
        this.red.setCanSeeFriendlyInvisibles(true);
        (this.green = this.scoreboard.registerNewTeam("green")).setPrefix(com.prevailpots.bunkers.game.Team.GREEN.getColor().toString());
        this.green.setCanSeeFriendlyInvisibles(true);
        (this.blue = this.scoreboard.registerNewTeam("blue")).setPrefix(com.prevailpots.bunkers.game.Team.BLUE.getColor().toString());
        this.blue.setCanSeeFriendlyInvisibles(true);
        (this.yellow = this.scoreboard.registerNewTeam("yellow")).setPrefix(com.prevailpots.bunkers.game.Team.YELLOW.getColor().toString());
        this.yellow.setCanSeeFriendlyInvisibles(true);
        player.setScoreboard(this.scoreboard);
    }
    
    public void remove() {
        if (!this.removed.getAndSet(true) && this.scoreboard != null) {
            for (final Team team : this.scoreboard.getTeams()) {
                team.unregister();
            }
            for (final Objective objective : this.scoreboard.getObjectives()) {
                objective.unregister();
            }
        }
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public boolean isSidebarVisible() {
        return this.sidebarVisible;
    }
    
    public void setSidebarVisible(final boolean visible) {
        this.sidebarVisible = visible;
        this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }
    
    public void setDefaultSidebar(final SidebarProvider provider, final long updateInterval) {
        if (provider != this.defaultProvider) {
            this.defaultProvider = provider;
            if (this.runnable != null) {
                this.runnable.cancel();
            }
            if (provider == null) {
                this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
                return;
            }
            (this.runnable = new BukkitRunnable() {
                public void run() {
                    if (PlayerBoard.this.removed.get()) {
                        this.cancel();
                        return;
                    }
                    if (provider == PlayerBoard.this.defaultProvider) {
                        PlayerBoard.this.updateObjective();
                    }
                }
            }).runTaskTimerAsynchronously((Plugin)this.plugin, updateInterval, updateInterval);
        }
    }
    
    public void setTemporarySidebar(final SidebarProvider provider, final long expiration) {
        if (this.removed.get()) {
            throw new IllegalStateException("Cannot update whilst board is removed");
        }
        this.temporaryProvider = provider;
        this.updateObjective();
        new BukkitRunnable() {
            public void run() {
                if (PlayerBoard.this.removed.get()) {
                    this.cancel();
                    return;
                }
                if (PlayerBoard.this.temporaryProvider == provider) {
                    PlayerBoard.access$4(PlayerBoard.this, null);
                    PlayerBoard.this.updateObjective();
                }
            }
        }.runTaskLaterAsynchronously((Plugin)this.plugin, expiration);
    }
    
    private void updateObjective() {
        if (this.removed.get()) {
            throw new IllegalStateException("Cannot update whilst board is removed");
        }
        final SidebarProvider provider = (this.temporaryProvider != null) ? this.temporaryProvider : this.defaultProvider;
        if (provider == null) {
            this.bufferedObjective.setVisible(false);
        }
        else {
            try {
                this.bufferedObjective.setTitle(provider.getTitle());
                this.bufferedObjective.setAllLines(provider.getLines(this.player));
                this.bufferedObjective.flip();
            }
            catch (Exception ex) {}
        }
    }
    
    public void addUpdate(final Player target) {
        this.addUpdates(Collections.singleton(target));
    }
    
    public void addUpdates(final Iterable<? extends Player> updates) {
        if (this.removed.get()) {
            throw new IllegalStateException("Cannot update whilst board is removed");
        }
        new BukkitRunnable() {
            public void run() {
                if (PlayerBoard.this.removed.get()) {
                    this.cancel();
                    return;
                }
                for (final Player update : updates) {
                    if (!PlayerBoard.this.plugin.getGameHandler().getPlayers().containsKey(update.getName())) {
                        continue;
                    }
                    if (PlayerBoard.this.plugin.getGameHandler().getPlayers().get(update.getName()).equals(com.prevailpots.bunkers.game.Team.RED)) {
                        PlayerBoard.this.red.addPlayer((OfflinePlayer)update);
                    }
                    else if (PlayerBoard.this.plugin.getGameHandler().getPlayers().get(update.getName()).equals(com.prevailpots.bunkers.game.Team.GREEN)) {
                        PlayerBoard.this.green.addPlayer((OfflinePlayer)update);
                    }
                    else if (PlayerBoard.this.plugin.getGameHandler().getPlayers().get(update.getName()).equals(com.prevailpots.bunkers.game.Team.BLUE)) {
                        PlayerBoard.this.blue.addPlayer((OfflinePlayer)update);
                    }
                    else {
                        if (!PlayerBoard.this.plugin.getGameHandler().getPlayers().get(update.getName()).equals(com.prevailpots.bunkers.game.Team.YELLOW)) {
                            continue;
                        }
                        PlayerBoard.this.yellow.addPlayer((OfflinePlayer)update);
                    }
                }
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
    }
    
    static /* synthetic */ void access$4(final PlayerBoard playerBoard, final SidebarProvider temporaryProvider) {
        playerBoard.temporaryProvider = temporaryProvider;
    }
}

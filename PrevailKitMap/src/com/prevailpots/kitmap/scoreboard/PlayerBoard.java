package com.prevailpots.kitmap.scoreboard;

import com.customhcf.base.BasePlugin;
import com.prevailpots.kitmap.ConfigurationService;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.classes.type.archer.ArcherClass;
import com.prevailpots.kitmap.faction.type.PlayerFaction;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import java.util.Collection;
import java.util.Collections;

public class PlayerBoard {
    public final BufferedObjective bufferedObjective;
    private Team members;
    private Team archers;
    private Team neutrals;
    private Team vanish;
    private Team allies;
    private Team focused;
    private final Scoreboard scoreboard;
    private final Player player;
    private final HCF plugin;
    private boolean sidebarVisible;
    private boolean removed;
    private SidebarProvider defaultProvider;
    private SidebarProvider temporaryProvider;
    private BukkitRunnable runnable;

    public PlayerBoard(final HCF plugin, final Player player) {
        this.sidebarVisible = false;
        this.removed = false;
        this.plugin = plugin;
        this.player = player;
        this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        this.bufferedObjective = new BufferedObjective(this.scoreboard);
        (this.vanish = this.scoreboard.registerNewTeam("vanish")).setPrefix(ChatColor.AQUA.toString());
        this.vanish.setCanSeeFriendlyInvisibles(true);
        (this.members = this.scoreboard.registerNewTeam("members")).setPrefix(ChatColor.DARK_GREEN.toString());
        this.members.setCanSeeFriendlyInvisibles(true);
        (this.neutrals = this.scoreboard.registerNewTeam("neutrals")).setPrefix(ChatColor.RED.toString());
        (this.archers = this.scoreboard.registerNewTeam("archers")).setPrefix(ChatColor.DARK_RED.toString());
        (this.focused = this.scoreboard.registerNewTeam("focused")).setPrefix(ChatColor.LIGHT_PURPLE.toString());
        (this.allies = this.scoreboard.registerNewTeam("allies")).setPrefix(ConfigurationService.ALLY_COLOUR.toString());
        player.setScoreboard(this.scoreboard);
    }

    public void remove() {
        this.removed = true;
        if(this.scoreboard != null) {
            synchronized(this.scoreboard) {
                for(final Team team : this.scoreboard.getTeams()) {
                    team.unregister();
                }
                for(final Objective objective : this.scoreboard.getObjectives()) {
                    objective.unregister();
                }
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
        if(provider != null && provider.equals(this.defaultProvider)) {
            return;
        }
        this.defaultProvider = provider;
        if(this.runnable != null) {
            this.runnable.cancel();
        }
        if(provider == null) {
            this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
            return;
        }
        (this.runnable = new BukkitRunnable() {
            public void run() {
                if(PlayerBoard.this.removed) {
                    this.cancel();
                    return;
                }
                if(provider.equals(PlayerBoard.this.defaultProvider)) {
                    PlayerBoard.this.updateObjective();
                }
            }
        }).runTaskTimerAsynchronously((Plugin) this.plugin, updateInterval, updateInterval);
    }

    public void setTemporarySidebar(final SidebarProvider provider, final long expiration) {
        this.temporaryProvider = provider;
        this.updateObjective();
        new BukkitRunnable() {
            public void run() {
                if(PlayerBoard.this.removed) {
                    this.cancel();
                    return;
                }
                if(PlayerBoard.this.temporaryProvider == provider) {
                    PlayerBoard.this.temporaryProvider = null;
                    PlayerBoard.this.updateObjective();
                }
            }
        }.runTaskLaterAsynchronously((Plugin) this.plugin, expiration);
    }

    private void updateObjective() {
        final SidebarProvider provider = (this.temporaryProvider != null) ? this.temporaryProvider : this.defaultProvider;
        if(provider == null) {
            this.bufferedObjective.setVisible(false);
        } else {
            this.bufferedObjective.setTitle(provider.getTitle());
            this.bufferedObjective.setAllLines(provider.getLines(this.player));
            this.bufferedObjective.flip();
        }
    }

    public void addUpdate(final Player target) {
        this.addUpdates(Collections.singleton(target));
    }

    public void addUpdates(final Collection<? extends Player> updates) {
        if(this.removed) {
            return;
        }
        new BukkitRunnable() {

            public void run() {
                PlayerFaction playerFaction = null;
                boolean hasRun = false;

                for(final Player update : updates) {
                    if (PlayerBoard.this.player.equals(update)) {
                        if(BasePlugin.getPlugin().getUserManager().getUser(update.getUniqueId()).isVanished()){
                            PlayerBoard.this.vanish.addPlayer(update);
                        }
                        if(ArcherClass.TAGGED.containsKey(update.getUniqueId())){
                            PlayerBoard.this.archers.addPlayer(update);
                        }
                        playerFaction = PlayerBoard.this.plugin.getFactionManager().getPlayerFaction(PlayerBoard.this.player.getUniqueId());
                        if(playerFaction != null && playerFaction.getFocus() != null && playerFaction.getFocus().equals(update.getUniqueId())){
                            PlayerBoard.this.focused.addPlayer(update);
                        }
                        PlayerBoard.this.members.addPlayer((OfflinePlayer) update);

                    } else {
                        if (!hasRun) {
                            playerFaction = PlayerBoard.this.plugin.getFactionManager().getPlayerFaction(PlayerBoard.this.player.getUniqueId());
                            hasRun = true;
                        }
                        final PlayerFaction targetFaction;
                        if(playerFaction != null){
                            if(playerFaction.getFocus() != null){
                                if(playerFaction.getFocus().equals(update.getUniqueId())) {
                                    PlayerBoard.this.focused.addPlayer(update);
                                    continue;
                                }
                            }
                        }
                        if(BasePlugin.getPlugin().getUserManager().getUser(update.getUniqueId()).isVanished()){
                            PlayerBoard.this.vanish.addPlayer(update);
                        }else
                        if(ArcherClass.TAGGED.containsKey(update.getUniqueId())){
                            PlayerBoard.this.archers.addPlayer(update);
                        }else
                        if (playerFaction == null || (targetFaction = PlayerBoard.this.plugin.getFactionManager().getPlayerFaction(update.getUniqueId())) == null) {
                            PlayerBoard.this.neutrals.addPlayer((OfflinePlayer) update);
                        } else if (playerFaction.equals(targetFaction)) {
                            PlayerBoard.this.members.addPlayer((OfflinePlayer) update);
                        } else if (playerFaction.getAllied().contains(targetFaction.getUniqueID())) {
                            PlayerBoard.this.allies.addPlayer((OfflinePlayer) update);
                        } else {
                            PlayerBoard.this.neutrals.addPlayer((OfflinePlayer) update);
                        }


                    }
                }
                    }
        }.runTaskAsynchronously((Plugin) this.plugin);
    }
}

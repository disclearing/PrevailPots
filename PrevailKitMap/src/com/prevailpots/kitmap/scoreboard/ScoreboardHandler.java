package com.prevailpots.kitmap.scoreboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.customhcf.base.event.PlayerVanishEvent;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.event.FactionFocusChangeEvent;
import com.prevailpots.kitmap.faction.event.FactionRelationCreateEvent;
import com.prevailpots.kitmap.faction.event.FactionRelationRemoveEvent;
import com.prevailpots.kitmap.faction.event.PlayerJoinedFactionEvent;
import com.prevailpots.kitmap.faction.event.PlayerLeftFactionEvent;
import com.prevailpots.kitmap.scoreboard.provider.TimerSidebarProvider;

public class ScoreboardHandler implements Listener {
    private final Map<UUID, PlayerBoard> playerBoards;
    private final TimerSidebarProvider timerSidebarProvider;
    private final HCF plugin;

    public ScoreboardHandler(final HCF plugin) {
        this.playerBoards = new HashMap<UUID, PlayerBoard>();
        this.plugin = plugin;
        this.timerSidebarProvider = new TimerSidebarProvider(plugin);
        Bukkit.getPluginManager().registerEvents( this,  plugin);
        for(Player players : Bukkit.getOnlinePlayers()) {
            final PlayerBoard playerBoard;
            this.setPlayerBoard(players.getUniqueId(), playerBoard = new PlayerBoard(plugin, players));
                    playerBoard.addUpdates(Sets.newHashSet(Bukkit.getOnlinePlayers()));
        }
    }

    @EventHandler
    public void onVanish(PlayerVanishEvent e){
        for(Player on: Bukkit.getOnlinePlayers()){
            this.getPlayerBoard(e.getPlayer().getUniqueId()).addUpdate(on);
            this.getPlayerBoard(on.getUniqueId()).addUpdate(e.getPlayer());
        }
    }

    @EventHandler
    public void onFocus(FactionFocusChangeEvent e){
        final HashSet<Player> updates = new HashSet<>(e.getSenderFaction().getOnlinePlayers());
        if(e.getPlayer() != null) {
            updates.add(e.getPlayer());
        }
        if(e.getOldFocus() != null && Bukkit.getPlayer(e.getOldFocus()) != null) {
            updates.add(Bukkit.getPlayer(e.getOldFocus()));

        }
        for(final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdates(updates);
        }

    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        for(final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdate(player);
        }
        final PlayerBoard board2 = new PlayerBoard(this.plugin, player);
            board2.addUpdates(Sets.newHashSet(Bukkit.getOnlinePlayers()));
        setPlayerBoard(uuid, board2);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.playerBoards.remove(event.getPlayer().getUniqueId()).remove();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinedFaction(final PlayerJoinedFactionEvent event) {
        final Optional<Player> optional = event.getPlayer();
        if(optional.isPresent()) {
            final Player player =  optional.get();
            final Collection<Player> players = plugin.getFactionManager().getPlayerFaction(player.getUniqueId()).getOnlinePlayers();
            this.getPlayerBoard(event.getUniqueID()).addUpdates(players);
            for(final Player target : players) {
                this.getPlayerBoard(target.getUniqueId()).addUpdate(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(final PlayerLeftFactionEvent event) {
        final Optional<Player> optional = event.getPlayer();
        if(optional.isPresent()) {
            final Player player =  optional.get();
            final Collection<Player> players = event.getFaction().getOnlinePlayers();
            this.getPlayerBoard(event.getUniqueID()).addUpdates(players);
            for(final Player target : players) {
                this.getPlayerBoard(target.getUniqueId()).addUpdate(player);
            }
        }
    }



    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionAllyCreate(final FactionRelationCreateEvent event) {
        final Set<Player> updates = new HashSet<Player>(event.getSenderFaction().getOnlinePlayers());
        updates.addAll(event.getTargetFaction().getOnlinePlayers());
        for(final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdates(updates);
        }
    }



    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionAllyRemove(final FactionRelationRemoveEvent event) {
        final Set<Player> updates = new HashSet<>(event.getSenderFaction().getOnlinePlayers());
        updates.addAll(event.getTargetFaction().getOnlinePlayers());
        for(final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdates(updates);
        }
    }

    public PlayerBoard getPlayerBoard(final UUID uuid) {
        return this.playerBoards.get(uuid);
    }

    public void setPlayerBoard(final UUID uuid, final PlayerBoard board) {
        this.playerBoards.put(uuid, board);
        board.setSidebarVisible(true);
        board.setDefaultSidebar(this.timerSidebarProvider, 2L);
    }

    public void clearBoards() {
        final Iterator<PlayerBoard> iterator = this.playerBoards.values().iterator();
        while(iterator.hasNext()) {
            iterator.next().remove();
            iterator.remove();
        }
    }
}

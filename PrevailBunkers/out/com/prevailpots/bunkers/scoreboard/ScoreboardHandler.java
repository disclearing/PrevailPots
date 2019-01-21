package com.prevailpots.bunkers.scoreboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.prevailpots.bunkers.Core;
import com.prevailpots.bunkers.scoreboard.provider.TimerSidebarProvider;



public class ScoreboardHandler implements Listener
{
    private static final long UPDATE_TICK_INTERVAL = 2L;
    private final Map<UUID, PlayerBoard> playerBoards;
    private final TimerSidebarProvider timerSidebarProvider;
    private final Core plugin;
    
    public ScoreboardHandler(final Core plugin) {
        this.playerBoards = new HashMap<UUID, PlayerBoard>();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        this.timerSidebarProvider = new TimerSidebarProvider(plugin);
        final Set<Player> players = new HashSet<Player>();
        for (final Player player : players) {
            this.applyBoard(player).addUpdates(players);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        for (final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdate(player);
        }
        final Set<Player> players = new HashSet<Player>();
        this.applyBoard(player).addUpdates(players);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (this.playerBoards.containsKey(event.getPlayer().getUniqueId())) {
            this.playerBoards.remove(event.getPlayer().getUniqueId()).remove();
        }
    }
    
    public PlayerBoard getPlayerBoard(final UUID uuid) {
        return this.playerBoards.get(uuid);
    }
    
    public PlayerBoard applyBoard(final Player player) {
        final PlayerBoard board = new PlayerBoard(this.plugin, player);
        final PlayerBoard previous = this.playerBoards.put(player.getUniqueId(), board);
        if (previous != null && previous != board) {
            previous.remove();
        }
        board.setSidebarVisible(true);
        board.setDefaultSidebar(this.timerSidebarProvider, 2L);
        return board;
    }
    
    public void clearBoards() {
        final Iterator<PlayerBoard> iterator = this.playerBoards.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().remove();
            iterator.remove();
        }
    }
}

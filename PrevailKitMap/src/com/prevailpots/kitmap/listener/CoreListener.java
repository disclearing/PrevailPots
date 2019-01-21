package com.prevailpots.kitmap.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.customhcf.base.BasePlugin;
import com.customhcf.base.event.TickEvent;
import com.customhcf.util.BukkitUtils;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.faction.type.SpawnFaction;

public class CoreListener implements Listener {
    private final HCF plugin;
    public static final String DEFAULT_WORLD_NAME = "world";
    private Set<UUID> afk;

    public CoreListener(final HCF plugin) {
        this.plugin = plugin;
        this.afk = new HashSet<>();
    }
    @EventHandler
    public void onAttack(EntityTargetEvent e){
        if(e.getTarget() instanceof Player && e.getEntityType() != EntityType.ENDERMAN ){
            e.setCancelled(true);
        }
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerSpawn(final PlayerSpawnLocationEvent event) {
        final Player player = event.getPlayer();
        if(!player.hasPlayedBefore()) {
            this.plugin.getEconomyManager().addBalance(player.getUniqueId(), 250);
            /* WORLD SPAWN */
            event.setSpawnLocation(Bukkit.getWorld(DEFAULT_WORLD_NAME).getSpawnLocation().add(0, 1, 0));
            player.getInventory().addItem(new ItemStack(Material.FISHING_ROD, 1));
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION){
            if(plugin.getFactionManager().getFactionAt(e.getEntity().getLocation()) == null){
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onEntDamageByEnt(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            final Faction damagedFactionAt = HCF.getPlugin().getFactionManager().getFactionAt(e.getEntity().getLocation());
            final Faction damagerFactionAt = HCF.getPlugin().getFactionManager().getFactionAt(e.getDamager().getLocation());

            if(damagedFactionAt instanceof SpawnFaction || damagerFactionAt instanceof SpawnFaction) e.setCancelled(true);
        }
    }


    @EventHandler
    public void onDecay(LeavesDecayEvent e)  {
        e.setCancelled(true);
    }

    @EventHandler
    public void onTick(TickEvent e){
        for(Player on : Bukkit.getOnlinePlayers()){
            if(BukkitUtils.getIdleTime(on) >= TimeUnit.MINUTES.toMillis(5) && !on.isOp()) {
                if (!afk.contains(on.getUniqueId())) {
                    on.sendMessage(ChatColor.RED + "You have been idle for more than 5 minutes! \nYour playtime is no longer counting.");
                    BasePlugin.getPlugin().getPlayTimeManager().sessionTimestamps.remove(on.getUniqueId());
                    afk.add(on.getUniqueId());
                    BasePlugin.getPlugin().getPlayTimeManager().totalPlaytimeMap.put(on.getUniqueId(), BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(on.getUniqueId()));
                }
            }
            if(afk.contains(on.getUniqueId()) && BukkitUtils.getIdleTime(on) <= TimeUnit.SECONDS.toMillis(5) && !on.isOp()){
                afk.remove(on.getUniqueId());
                BasePlugin.getPlugin().getPlayTimeManager().sessionTimestamps.put(on.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
        if(e.getWorld().isThundering())
            e.getWorld().setStorm(false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin2(PlayerJoinEvent event) {
        Player target = event.getPlayer();
        int ver = ((CraftPlayer)target).getHandle().playerConnection.networkManager.getVersion();
        if(ver != 5) {
            target.kickPlayer(ChatColor.RED + "You must connect on 1.7.10!");
        }
    }

    @EventHandler
    public void handleSpawnJoin(PlayerJoinEvent e) {
        if(!e.getPlayer().hasPlayedBefore()) {
            e.getPlayer().teleport(new Location(Bukkit.getWorld(DEFAULT_WORLD_NAME), 0.5, 73, 0.5));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.setQuitMessage((String) null);
        final Player player = event.getPlayer();
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        this.plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        this.plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
    }
}

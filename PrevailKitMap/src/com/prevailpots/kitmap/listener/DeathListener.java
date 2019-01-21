package com.prevailpots.kitmap.listener;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.customhcf.util.JavaUtils;
import com.prevailpots.kitmap.ConfigurationService;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.balance.EconomyManager;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.faction.type.PlayerFaction;
import com.prevailpots.kitmap.user.FactionUser;

import net.minecraft.server.v1_7_R4.EntityLightning;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_7_R4.WorldServer;

public class DeathListener implements Listener {
    private static final long BASE_REGEN_DELAY;

    public static HashMap<UUID, ItemStack[]> PlayerInventoryContents;
    public static HashMap<UUID, ItemStack[]> PlayerArmorContents;

    static {
        PlayerInventoryContents = new HashMap<>();
        PlayerArmorContents = new HashMap<>();
        BASE_REGEN_DELAY = TimeUnit.MINUTES.toMillis(40L);
    }

    private final HCF plugin;

    public DeathListener(final HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDeathKillIncrement(final PlayerDeathEvent event) {
        final Player killer = event.getEntity().getKiller();
        if(killer != null) {
            final FactionUser user = this.plugin.getUserManager().getUser(killer.getUniqueId());
            user.setKills(user.getKills() + 1);
        }
        final FactionUser user = this.plugin.getUserManager().getUser(event.getEntity().getUniqueId());
        user.setDeaths(user.getKills() + 1);
        final Player player = event.getEntity();
        PlayerInventoryContents.put(player.getUniqueId(), player.getInventory().getContents());
        PlayerArmorContents.put(player.getUniqueId(), player.getInventory().getArmorContents());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if(playerFaction != null) {
            final Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());

            if(playerFaction.getDeathsUntilRaidable() >= -5) {
                playerFaction.setRemainingRegenerationTime(DeathListener.BASE_REGEN_DELAY + playerFaction.getOnlinePlayers().size() * TimeUnit.MINUTES.toMillis(2L));
                playerFaction.broadcast(ChatColor.YELLOW + "Member Death: " + ConfigurationService.TEAMMATE_COLOUR +  player.getName() + ChatColor.YELLOW + ".\n DTR: " + ChatColor.RED+ playerFaction.getDtrColour() + JavaUtils.format(playerFaction.getDeathsUntilRaidable()));
            }else{
                playerFaction.setRemainingRegenerationTime(DeathListener.BASE_REGEN_DELAY + playerFaction.getOnlinePlayers().size() * TimeUnit.MINUTES.toMillis(2L));
                playerFaction.broadcast(ChatColor.YELLOW + "Member Death: " + ConfigurationService.TEAMMATE_COLOUR +  player.getName() + ChatColor.YELLOW + ".\n DTR: " + ChatColor.RED+ playerFaction.getDtrColour() + JavaUtils.format(playerFaction.getDeathsUntilRaidable()));
            }
        }
            Integer balance = 0;
        if (player.getKiller() instanceof Player) {
            if (plugin.getEconomyManager().getBalance(player.getUniqueId()) > 0) {
                balance = plugin.getEconomyManager().getBalance(player.getUniqueId()) % 10;
                plugin.getEconomyManager().addBalance(player.getKiller().getUniqueId(), balance);
                plugin.getEconomyManager().subtractBalance(player.getUniqueId(), balance);
            }
            player.getKiller().sendMessage(ChatColor.YELLOW + "You earned " + ChatColor.GREEN + ChatColor.BOLD + EconomyManager.ECONOMY_SYMBOL + balance + ChatColor.YELLOW + " for killing " + ChatColor.GOLD + player.getName() + ChatColor.YELLOW + ".");
        }else {
            //do nothing fixes null pointer in the console
        }

        if(Bukkit.spigot().getTPS()[0] > 15.0) {
            final Location location = player.getLocation();
            final WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
            final EntityLightning entityLightning = new EntityLightning(worldServer, location.getX(), location.getY(), location.getZ(), false);
            final PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(entityLightning);
            for(final Player target : Bukkit.getOnlinePlayers()) {
                    ((CraftPlayer) target).getHandle().playerConnection.sendPacket( packet);
                    target.playSound(target.getLocation(), Sound.AMBIENCE_THUNDER, 1.0f, 1.0f);
                }
        }
    }
}

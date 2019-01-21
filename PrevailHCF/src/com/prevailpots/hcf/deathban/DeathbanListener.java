package com.prevailpots.hcf.deathban;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.cache.CacheBuilder;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.user.FactionUser;

public class DeathbanListener implements Listener {
    private static final long LIFE_USE_DELAY_MILLIS;
    private static final String LIFE_USE_DELAY_WORDS;
    private static final String DEATH_BAN_BYPASS_PERMISSION = "deathban.bypass";
    static {
        LIFE_USE_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(30L);
        LIFE_USE_DELAY_WORDS = DurationFormatUtils.formatDurationWords(DeathbanListener.LIFE_USE_DELAY_MILLIS, true, true);
    }

    private final ConcurrentMap<Object, Object> lastAttemptedJoinMap;
    private final HCF plugin;

    public DeathbanListener(final HCF plugin) {
        this.plugin = plugin;
        this.lastAttemptedJoinMap = CacheBuilder.newBuilder().expireAfterWrite(DeathbanListener.LIFE_USE_DELAY_MILLIS, TimeUnit.MILLISECONDS).build().asMap();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        final FactionUser user = this.plugin.getUserManager().getUser(player.getUniqueId());
        final Deathban deathban = user.getDeathban();
        if(deathban == null || !deathban.isActive()) {
            return;
        }
        if(player.hasPermission(DEATH_BAN_BYPASS_PERMISSION)) {
            user.setDeathban(null);
            new LoginMessageRunnable(player, ChatColor.RED + "You would be death-banned for " + deathban.getReason() + ChatColor.RED + ", but you have access to bypass.").runTask((Plugin) this.plugin);
            return;
        }
        if(this.plugin.getEotwHandler().isEndOfTheWorld()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Deathbanned for the entirety of the map due to EOTW.\nCome back for SOTW.");
        } else {
            final UUID uuid = player.getUniqueId();
            int lives = this.plugin.getUserManager().getUser(uuid).getLives();
            int soulLives = this.plugin.getUserManager().getUser(uuid).getSouLives();
            final String formattedDuration = HCF.getRemaining(deathban.getRemaining(), true, false);
            final String reason = deathban.getReason();
            final String prefix = ChatColor.RED + "You are currently death-banned" + ((reason != null) ? (" for " + reason + ".\n") : ".") + ChatColor.YELLOW + formattedDuration + " remaining.\n" + ChatColor.RED + "You currently have " + ((lives <= 0) ? "no" : lives) + " lives.";
            if(lives > 0 || soulLives > 0) {
                final long millis = System.currentTimeMillis();
                final Long lastAttemptedJoinMillis = (Long) this.lastAttemptedJoinMap.get(uuid);
                if(lastAttemptedJoinMillis != null && lastAttemptedJoinMillis - System.currentTimeMillis() < DeathbanListener.LIFE_USE_DELAY_MILLIS) {
                    this.lastAttemptedJoinMap.remove(uuid);
                    user.setDeathban(null);
                    Integer whichLives = Math.max(lives, soulLives);
                    if(whichLives == lives) {
                        lives = plugin.getUserManager().getUser(uuid).takeLives(1);
                        event.setResult(PlayerLoginEvent.Result.ALLOWED);
                        new LoginMessageRunnable(player, ChatColor.YELLOW + "You have used a life bypass your death. You now have " + ChatColor.GOLD + lives + ChatColor.YELLOW + " lives.").runTask((Plugin) this.plugin);
                    }else{
                        lives = plugin.getUserManager().getUser(uuid).takeSoulLives(1);
                        event.setResult(PlayerLoginEvent.Result.ALLOWED);
                        new LoginMessageRunnable(player, ChatColor.YELLOW + "You have used a soul life to bypass your death. You now have " + ChatColor.GOLD + lives + ChatColor.YELLOW + " soul lives.").runTask((Plugin) this.plugin);
                    }
                } else {
                    this.lastAttemptedJoinMap.put(uuid, millis + DeathbanListener.LIFE_USE_DELAY_MILLIS);
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, prefix + ChatColor.GOLD + "\n\n" + "You may use a life by reconnecting within " + ChatColor.YELLOW + DeathbanListener.LIFE_USE_DELAY_WORDS + ChatColor.YELLOW + '.');
                }
                return;
            }
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Still deathbanned for " + formattedDuration + ": " + ChatColor.GOLD + deathban.getReason() + ChatColor.RED + '.' + "\nYou can purchase lives at " + plugin.getConfig().get("DonateLink"));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerDeath(final PlayerDeathEvent event) {
            final Player player = event.getEntity();
            final Deathban deathban = this.plugin.getDeathbanManager().applyDeathBan(player, event.getDeathMessage());
            final String durationString = HCF.getRemaining(deathban.getRemaining(), true, false);
            new BukkitRunnable() {
                public void run() {
                    if (DeathbanListener.this.plugin.getEotwHandler().isEndOfTheWorld()) {
                        player.kickPlayer(ChatColor.RED + "You died during EOTW.");
                    } else {
                        player.kickPlayer(ChatColor.RED + "Deathbanned for " + durationString + ": " + ChatColor.YELLOW + deathban.getReason());
                    }
                }
            }.runTaskLater((Plugin) this.plugin, 1L);
            if (player.hasPermission(DEATH_BAN_BYPASS_PERMISSION)) {
                player.teleport(Bukkit.getWorld("world").getSpawnLocation());
                player.sendMessage(ChatColor.RED + "You've been teleported to spawn due to death.");
            }
    }

    private static class LoginMessageRunnable extends BukkitRunnable {
        private final Player player;
        private final String message;

        public LoginMessageRunnable(final Player player, final String message) {
            this.player = player;
            this.message = message;
        }

        public void run() {
            this.player.sendMessage(this.message);
        }
    }
}

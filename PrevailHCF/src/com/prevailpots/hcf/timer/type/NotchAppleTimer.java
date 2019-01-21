package com.prevailpots.hcf.timer.type;

import com.google.common.base.MoreObjects;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.timer.PlayerTimer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class NotchAppleTimer extends PlayerTimer implements Listener {

    public NotchAppleTimer(final JavaPlugin plugin) {
        super("Gapple", MoreObjects.firstNonNull(plugin.getConfig().getLong("Timer.Gapple"), TimeUnit.HOURS.toMillis(6L)));
    }

    public String getScoreboardPrefix() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerConsume(final PlayerItemConsumeEvent event) {
        final ItemStack stack = event.getItem();
        if(stack != null && stack.getType() == Material.GOLDEN_APPLE && stack.getDurability() == 1) {
            final Player player = event.getPlayer();
            if(this.setCooldown(player, player.getUniqueId(), this.defaultCooldown, false)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',  "&c\u2588\u2588\u2588\u2588\u2588&c\u2588\u2588\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',  "&c\u2588\u2588\u2588&e\u2588\u2588&c\u2588\u2588\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588&e\u2588&c\u2588\u2588\u2588\u2588 &6&l "+this.name+": "));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588&6\u2588\u2588\u2588\u2588&c\u2588\u2588 &7  Consumed"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&f\u2588&6\u2588&6\u2588\u2588&c\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588&f\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588 &6 Cooldown Remaining:"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588 &7  "+ HCF.getRemaining(this.defaultCooldown, true, true)));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588&6\u2588\u2588\u2588\u2588&c\u2588\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',  "&c\u2588\u2588\u2588\u2588\u2588&c\u2588\u2588\u2588"));


            } else {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You still have a " + this.getDisplayName() + ChatColor.RED + " cooldown for another " + ChatColor.BOLD + HCF.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + '.');
            }
        }
    }
}

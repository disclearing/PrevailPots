package com.prevailpots.kitmap.command;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.timer.type.LogoutTimer;

import java.util.Collections;
import java.util.List;

public class LogoutCommand implements CommandExecutor, TabExecutor {
    private final HCF plugin;

    public LogoutCommand(final HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player) sender;
        final LogoutTimer logoutTimer = this.plugin.getTimerManager().logoutTimer;
        if(!logoutTimer.setCooldown(player, player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are already logging out. Current timer: " + HCF.getRemaining(logoutTimer.getRemaining(player), true));
            return true;
        }
            sender.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "You are being logged out."+ChatColor.YELLOW +" Do not move for another "+ChatColor.RED+"30 "+ChatColor.YELLOW +"seconds.");
            return true;
        }
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}

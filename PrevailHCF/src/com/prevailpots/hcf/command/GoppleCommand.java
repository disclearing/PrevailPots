package com.prevailpots.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.timer.PlayerTimer;

import java.util.Collections;
import java.util.List;

public class GoppleCommand implements CommandExecutor, TabExecutor {
    private final HCF plugin;

    public GoppleCommand(final HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player) sender;
        final PlayerTimer timer = this.plugin.getTimerManager().notchAppleTimer;
        final long remaining = timer.getRemaining(player);
        if(remaining <= 0L) {
            sender.sendMessage(ChatColor.RED + "No current gopple cooldown.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Your " + timer.getDisplayName() + ChatColor.YELLOW + " timer is active for another " + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.YELLOW + '.');
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}

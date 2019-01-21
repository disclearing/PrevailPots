package com.prevailpots.hcf.command;

import com.customhcf.util.BukkitUtils;
import com.google.common.collect.ImmutableList;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.timer.type.PvpProtectionTimer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PvpTimerCommand implements CommandExecutor, TabCompleter {
    private static final ImmutableList<String> COMPLETIONS;

    static {
        COMPLETIONS = ImmutableList.of("enable", "time");
    }

    private final HCF plugin;

    public PvpTimerCommand(final HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player) sender;
        final PvpProtectionTimer pvpTimer = this.plugin.getTimerManager().pvpProtectionTimer;
        if(args.length < 1) {
            this.printUsage(sender, label, pvpTimer);
            return true;
        }
        if(args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("off")) {
            if(pvpTimer.getRemaining(player) > 0L) {
                sender.sendMessage(ChatColor.YELLOW + "Your " + pvpTimer.getDisplayName() + ChatColor.YELLOW + " is now disabled.");
                pvpTimer.clearCooldown(player);
                return true;
            }
            if(pvpTimer.isPaused(player)) {
                player.sendMessage(ChatColor.YELLOW + "You will no longer be legible for your " +  pvpTimer.getDisplayName() + ChatColor.YELLOW + " when you leave spawn.");
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "Your " + pvpTimer.getDisplayName() + ChatColor.YELLOW + " is currently not active.");
            return true;
        } else {
            if(!args[0].equalsIgnoreCase("remaining") && !args[0].equalsIgnoreCase("time") && !args[0].equalsIgnoreCase("left")) {
                this.printUsage(sender, label, pvpTimer);
                return true;
            }
            final long remaining = pvpTimer.getRemaining(player);
            if(remaining <= 0L) {
                sender.sendMessage(ChatColor.YELLOW + "Your " + pvpTimer.getDisplayName() + ChatColor.YELLOW + " is currently not active.");
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "Your " + pvpTimer.getDisplayName() + ChatColor.YELLOW + " is active for another " + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.YELLOW + (pvpTimer.isPaused(player) ? " and is currently paused" : "") + '.');
            return true;
        }
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 1) ? BukkitUtils.getCompletions(args, PvpTimerCommand.COMPLETIONS) : Collections.emptyList();
    }

    private void printUsage(final CommandSender sender, final String label, final PvpProtectionTimer pvpTimer) {
        sender.sendMessage(ChatColor.RED + "/" + label + " enable - Removes your " + pvpTimer.getDisplayName() + ChatColor.RED + ".");
        sender.sendMessage(ChatColor.RED + "/" + label + " time - Check remaining " + pvpTimer.getDisplayName() + ChatColor.RED + " time.");

    }
}

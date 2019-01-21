package com.prevailpots.kitmap.timer.argument;

import com.customhcf.util.BukkitUtils;
import com.customhcf.util.command.CommandArgument;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.timer.PlayerTimer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public class TimerCheckArgument extends CommandArgument {
    private final HCF plugin;

    public TimerCheckArgument(final HCF plugin) {
        super("check", "Check remaining timer time");
        this.plugin = plugin;
        this.permission  = "command.timer." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Set<PlayerTimer> timerNames = new HashSet<>();
        if(Bukkit.getPlayer(args[1]) != null){
            for(com.prevailpots.kitmap.timer.Timer timers : plugin.getTimerManager().getTimers()){
                if(timers instanceof PlayerTimer) {
                    if (((PlayerTimer) timers).hasCooldown(Bukkit.getPlayer(args[1]))) {
                        timerNames.add((PlayerTimer) timers);
                    }
                }
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }
        if(timerNames.isEmpty()){
            sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            sender.sendMessage(ChatColor.GOLD + Bukkit.getPlayer(args[1]).getName() + "'s "+ ChatColor.YELLOW + "active timers");
            sender.sendMessage(ChatColor.YELLOW + " - " + ChatColor.GOLD + "None!");
            sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            return true;
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GOLD + Bukkit.getPlayer(args[1]).getName() + "'s "+ ChatColor.YELLOW + "active timers");
        for(PlayerTimer timers : timerNames){
            sender.sendMessage(ChatColor.YELLOW + " - " + ChatColor.GOLD + timers.getDisplayName() + ChatColor.GRAY + " \u00BB " + ChatColor.YELLOW + HCF.getRemaining(timers.getRemaining(Bukkit.getPlayer(args[1]).getUniqueId()), true));
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}

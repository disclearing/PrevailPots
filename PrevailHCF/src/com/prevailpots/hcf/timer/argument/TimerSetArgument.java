package com.prevailpots.hcf.timer.argument;

import com.customhcf.util.JavaUtils;
import com.customhcf.util.command.CommandArgument;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.timer.PlayerTimer;
import com.prevailpots.hcf.timer.Timer;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class TimerSetArgument extends CommandArgument {
    private static final Pattern WHITESPACE_TRIMMER;

    static {
        WHITESPACE_TRIMMER = Pattern.compile("\\s");
    }

    private final HCF plugin;

    public TimerSetArgument(final HCF plugin) {
        super("set", "Set remaining timer time");
        this.plugin = plugin;
        this.permission  = "command.timer." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <timerName> <all|playerName> <remaining>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final long duration = JavaUtils.parse(args[3]);
        if(duration == -1L) {
            sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
            return true;
        }
        PlayerTimer playerTimer = null;
        for(final Timer timer : this.plugin.getTimerManager().getTimers()) {
            if(timer instanceof PlayerTimer && TimerSetArgument.WHITESPACE_TRIMMER.matcher(timer.getName()).replaceAll("").equalsIgnoreCase(args[1])) {
                playerTimer = (PlayerTimer) timer;
                break;
            }
        }
        if(playerTimer == null) {
            sender.sendMessage(ChatColor.RED + "Timer '" + args[1] + "' not found.");
            return true;
        }
        if(args[2].equalsIgnoreCase("all")) {
            for(final Player player : Bukkit.getOnlinePlayers()) {
                playerTimer.setCooldown(player, player.getUniqueId(), duration, true);
            }
            sender.sendMessage(ChatColor.BLUE + "Set timer " + playerTimer.getName() + " for all to " + DurationFormatUtils.formatDurationWords(duration, true, true) + '.');
        } else {
            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
            Player targetPlayer = null;
            if(target == null || (sender instanceof Player && (targetPlayer = target.getPlayer()) != null && !((Player) sender).canSee(targetPlayer))) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.YELLOW + args[1] + ChatColor.GOLD + "' not found.");
                return true;
            }
            if(duration == 0){
                sender.sendMessage(ChatColor.YELLOW + "Cleared timer " + playerTimer.getDisplayName() + ChatColor.YELLOW + " for " + ChatColor.BLUE +DurationFormatUtils.formatDurationWords(duration, true, true));
            }else{
                sender.sendMessage(ChatColor.YELLOW + "Set timer " + playerTimer.getDisplayName() + ChatColor.YELLOW + " duration to " + ChatColor.BLUE +DurationFormatUtils.formatDurationWords(duration, true, true) + ChatColor.YELLOW + " for " + ChatColor.BLUE+ target.getName() + '.');
            }
            playerTimer.setCooldown(targetPlayer, target.getUniqueId(), duration, true);
        }
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length == 2) {
            return FluentIterable.from(plugin.getTimerManager().getTimers()).filter(new Predicate<Timer>() {
                public boolean apply(final Timer timer) {
                    return timer instanceof PlayerTimer;
                }
            }).transform(new Function<Timer, String>() {
                @Nullable
                public String apply(final Timer timer) {
                    return TimerSetArgument.WHITESPACE_TRIMMER.matcher(timer.getName()).replaceAll("");
                }
            }).toList();
        }
        if(args.length == 3) {
            final List<String> list = new ArrayList<String>();
            list.add("ALL");
            final Player player = (Player) sender;
            for(final Player target : Bukkit.getOnlinePlayers()) {
                if(player == null || player.canSee(target)) {
                    list.add(target.getName());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}

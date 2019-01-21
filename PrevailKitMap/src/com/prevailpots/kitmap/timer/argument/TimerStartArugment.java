package com.prevailpots.kitmap.timer.argument;

import com.customhcf.util.JavaUtils;
import com.customhcf.util.command.CommandArgument;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.timer.GlobalTimer;
import com.prevailpots.kitmap.timer.Timer;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by TREHOME on 04/11/2016.
 */
public class TimerStartArugment extends CommandArgument {
    private static final Pattern WHITESPACE_TRIMMER;

    static {
        WHITESPACE_TRIMMER = Pattern.compile("\\s");
    }

    private final HCF plugin;

    public TimerStartArugment(final HCF plugin) {
        super("start", "start timer time");
        this.plugin = plugin;
        this.permission = "command.timer." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <timerName> [remaining]";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        GlobalTimer globalTimer = null;
        for (final Timer timer : this.plugin.getTimerManager().getTimers()) {
            if (timer instanceof GlobalTimer && WHITESPACE_TRIMMER.matcher(timer.getName()).replaceAll("").equalsIgnoreCase(args[1])) {
                globalTimer = (GlobalTimer) timer;
                break;
            }
        }
        if (globalTimer == null) {
            sender.sendMessage(ChatColor.RED + "Timer '" + args[1] + "' not found.");
            return true;
        }

        if (args.length == 3) {
            final long duration = JavaUtils.parse(args[2]);
            if (duration == -1L) {
                sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
                return true;
            }
            globalTimer.setRemaining(duration, true);
        } else {
            globalTimer.setRemaining();
        }
        if(globalTimer.getRemaining()  < 0){
            sender.sendMessage(ChatColor.RED + "The number was less than 0, if this error continues. Try inputting a value. [SOTW = 2h | Reboot = 30m]");
            return false;
        }

        sender.sendMessage(ChatColor.YELLOW + "Set timer " +  globalTimer.getDisplayName() + ChatColor.YELLOW + " duration to " + ChatColor.BLUE +DurationFormatUtils.formatDurationWords(globalTimer.getRemaining(), true, true) + '.');
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            return FluentIterable.from(plugin.getTimerManager().getTimers()).filter(new Predicate<Timer>() {
                public boolean apply(final Timer timer) {
                    return timer instanceof GlobalTimer;
                }
            }).transform(new Function<Timer, String>() {
                @Nullable
                public String apply(final Timer timer) {
                    return WHITESPACE_TRIMMER.matcher(timer.getName()).replaceAll("");
                }
            }).toList();
        }
        return Collections.emptyList();
    }
}
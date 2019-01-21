package com.prevailpots.hcf.kothgame.argument;

import com.customhcf.permissions.DateTimeFormats;
import com.customhcf.util.JavaUtils;
import com.customhcf.util.command.CommandArgument;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.type.Faction;
import com.prevailpots.hcf.kothgame.faction.EventFaction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameScheduleArgument extends CommandArgument {
    private final HCF plugin;

    public GameScheduleArgument(final HCF plugin) {
        super("schedule", "Schedules a game");
        this.plugin = plugin;
        this.permission = "command.game." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <name> <time>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if(!(faction instanceof EventFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not an game faction named '" + args[1] + "'.");
            return true;
        }
        EventFaction eventFaction = (EventFaction) faction;
        Long newTicks = JavaUtils.parse(args[2]);
        if(newTicks == -1L) {
            sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m1s");
            return true;
        }
      //  plugin.newEventScheduler.addSchedule(eventFaction, newTicks);
        sender.sendMessage(ChatColor.GOLD + faction.getName() + ChatColor.YELLOW + " is now scheduled for " + ChatColor.GOLD + DateTimeFormats.MTH_DAY_YEAR_TIME_AMPM.format(System.currentTimeMillis() + newTicks));
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length != 2) {
            return Collections.emptyList();
        }
        return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
    }
}

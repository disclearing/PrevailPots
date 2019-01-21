package com.prevailpots.hcf.kothgame.koth.argument;

import com.customhcf.util.command.CommandArgument;
import com.prevailpots.hcf.DateTimeFormats;
import com.prevailpots.hcf.HCF;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

public class KothNextArgument extends CommandArgument {
    private final HCF plugin;

    public KothNextArgument(final HCF plugin) {
        super("next", "View the next scheduled KOTH");
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final long millis = System.currentTimeMillis();
        sender.sendMessage(ChatColor.YELLOW + "The current server time is " + ChatColor.GREEN + DateTimeFormats.DAY_MTH_HR_MIN_AMPM.format(millis) + ChatColor.GOLD + '.');
        final Map<LocalDateTime, String> scheduleMap = this.plugin.eventScheduler.getScheduleMap();
        if(scheduleMap.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill]" + ChatColor.RED + " KOTH Next: "+ ChatColor.YELLOW + "Undefined" );
            return true;
        }
        final LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
        for(final Map.Entry<LocalDateTime, String> entry : scheduleMap.entrySet()) {
            final LocalDateTime scheduleDateTime = entry.getKey();
            if(now.isAfter(scheduleDateTime)) {
                continue;
            }
            final int currentDay = now.getDayOfYear();
            final String eventName = entry.getValue();
            final int dayDifference = scheduleDateTime.getDayOfYear() - currentDay;
            final String monthName = scheduleDateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            final String weekName = scheduleDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            final ChatColor colour = (dayDifference == 0) ? ChatColor.GREEN : ChatColor.RED;
            sender.sendMessage("  " + colour + WordUtils.capitalizeFully(eventName) + ChatColor.GRAY + " is the next event: " + ChatColor.YELLOW + weekName + ' ' + scheduleDateTime.getDayOfMonth() + ' ' + monthName + ChatColor.GREEN + " (" + KothScheduleArgument.HHMMA.format(scheduleDateTime) + ')');
            return true;
        }
        sender.sendMessage(ChatColor.RED + "There is not an event scheduled after now.");
        return true;
    }
}

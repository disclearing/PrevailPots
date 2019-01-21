package com.prevailpots.hcf.kothgame.koth.argument;

import com.customhcf.util.BukkitUtils;
import com.customhcf.util.command.CommandArgument;
import com.prevailpots.hcf.DateTimeFormats;
import com.prevailpots.hcf.HCF;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class KothScheduleArgument extends CommandArgument {
    private static final String TIME_UNTIL_PATTERN = "d'd' H'h' mm'm'";
    public static final DateTimeFormatter HHMMA;
    private static List<String> shownEvents;

    static {
        shownEvents = new ArrayList<>();
        HHMMA = DateTimeFormatter.ofPattern("h:mma");
    }

    private final HCF plugin;

    public KothScheduleArgument(final HCF plugin) {
        super("schedule", "View the schedule for KOTH arenas");
        this.plugin = plugin;
        this.aliases = new String[]{"info", "i", "time"};
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
        final int currentDay = now.getDayOfYear();
        final Map<LocalDateTime, String> scheduleMap = this.plugin.eventScheduler.getScheduleMap();
        final List<String> shownEvents = new ArrayList<String>();
        for(final Map.Entry<LocalDateTime, String> entry : scheduleMap.entrySet()) {
            final LocalDateTime scheduleDateTime = entry.getKey();
            if(scheduleDateTime.isAfter(now)) {
                final int dayDifference = scheduleDateTime.getDayOfYear() - currentDay;
                final String eventName = entry.getValue();
                final String monthName = scheduleDateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                final String weekName = scheduleDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                final ChatColor colour = (dayDifference == 0) ? ChatColor.GREEN : ChatColor.RED;
                shownEvents.add("  " + colour + WordUtils.capitalizeFully(eventName) + ": " + ChatColor.YELLOW + weekName + ' ' + scheduleDateTime.getDayOfMonth() + ' ' + monthName + ChatColor.RED + " (" + KothScheduleArgument.HHMMA.format(scheduleDateTime) + ')' + ChatColor.GRAY + " - " + ChatColor.GOLD + DurationFormatUtils.formatDuration(now.until(scheduleDateTime, ChronoUnit.MILLIS), TIME_UNTIL_PATTERN));
            }
        }
        if(shownEvents.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill]" + ChatColor.RED + " KOTH Schedule: "+ ChatColor.YELLOW + "Undefined" );
            return true;
        }
        final String monthName2 = WordUtils.capitalizeFully(now.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        final String weekName2 = WordUtils.capitalizeFully(now.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.YELLOW + "Current Time " + ChatColor.GREEN + weekName2 + ' ' + now.getDayOfMonth() + ' ' + monthName2 + ' ' + KothScheduleArgument.HHMMA.format(now) + ChatColor.YELLOW + '.');
        sender.sendMessage((String[]) shownEvents.toArray(new String[shownEvents.size()]));
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }
}

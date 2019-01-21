package com.prevailpots.hcf.command;

import org.apache.commons.lang3.time.FastDateFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.*;

import com.prevailpots.hcf.ConfigurationService;

import java.util.Collections;
import java.util.List;

public class ServerTimeCommand implements CommandExecutor {
    private static final FastDateFormat FORMAT;

    static {
        FORMAT = FastDateFormat.getInstance("E MMM dd h:mm:ssa z yyyy", ConfigurationService.SERVER_TIME_ZONE);
    }
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        sender.sendMessage(ChatColor.YELLOW + "It is " + ChatColor.GOLD + ServerTimeCommand.FORMAT.format(System.currentTimeMillis()) + " EST" +ChatColor.YELLOW  + '.');
        return true;
    }
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}

package com.prevailpots.kitmap.faction.argument;

import com.customhcf.util.command.CommandArgument;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionVersionArgument extends CommandArgument {
    public FactionVersionArgument() {
        super("version", "Display the version of this HCFactions plugin.");
    }

    @Override
    public String getUsage(final String label) {
        return "";
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() +
                "HCFactions" + ChatColor.RESET + ChatColor.YELLOW.toString() + " made by " + ChatColor.RED + "Clipped");
        return false;
    }
}

package com.prevailpots.kitmap.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.type.ClaimableFaction;
import com.prevailpots.kitmap.faction.type.Faction;

import java.util.Collections;
import java.util.List;

public class LocationCommand implements CommandExecutor, TabExecutor {
    private final HCF plugin;

    public LocationCommand(final HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " [playerName]");
            return true;
        }
        Player target = (Player) sender;
        final Location location = target.getLocation();
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        if(factionAt instanceof ClaimableFaction){
            sender.sendMessage(ChatColor.YELLOW + "You are in " + factionAt.getDisplayName(sender) + ChatColor.YELLOW + "'s territory.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "You are in "+ factionAt.getDisplayName(sender));
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 1 && sender.hasPermission(command.getPermission() + ".others")) ? null : Collections.emptyList();
    }
}

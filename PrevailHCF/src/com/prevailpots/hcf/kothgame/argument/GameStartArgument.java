package com.prevailpots.hcf.kothgame.argument;

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

public class GameStartArgument extends CommandArgument {
    private final HCF plugin;

    public GameStartArgument(final HCF plugin) {
        super("start", "Starts an game");
        this.plugin = plugin;
        this.aliases = new String[]{"begin"};
        this.permission = "command.game." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <eventName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if(!(faction instanceof EventFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not an event faction named '" + args[1] + "'.");
            return true;
        }
        if(this.plugin.getTimerManager().eventTimer.tryContesting((EventFaction) faction, sender)) {
            sender.sendMessage(ChatColor.YELLOW + "Successfully contested " + ChatColor.GOLD +faction.getName() + ChatColor.YELLOW +'.');
        }
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length != 2) {
            return Collections.emptyList();
        }
        return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
    }
}

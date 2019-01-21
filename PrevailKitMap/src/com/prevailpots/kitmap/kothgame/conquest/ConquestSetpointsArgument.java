package com.prevailpots.kitmap.kothgame.conquest;

import com.customhcf.util.command.CommandArgument;
import com.google.common.primitives.Ints;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.faction.type.PlayerFaction;
import com.prevailpots.kitmap.kothgame.EventType;
import com.prevailpots.kitmap.kothgame.tracker.ConquestTracker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ConquestSetpointsArgument extends CommandArgument {
    private final HCF plugin;

    public ConquestSetpointsArgument(final HCF plugin) {
        super("setpoints", "Sets the points of a faction in the Conquest event", "command.conquest.setpoints");
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <factionName> <amount>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if(!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "Faction " + args[1] + " is either not found or is not a player faction.");
            return true;
        }
        final Integer amount = Ints.tryParse(args[2]);
        if(amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
            return true;
        }
        if(amount > plugin.getHcfHandler().getConquestWinPoints()) {
            sender.sendMessage(ChatColor.RED + "Maximum points for Conquest is " + plugin.getHcfHandler().getConquestWinPoints() + '.');
            return true;
        }
        final PlayerFaction playerFaction = (PlayerFaction) faction;
        ((ConquestTracker) EventType.CONQUEST.getEventTracker()).setPoints(playerFaction, amount);
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set the points of faction " + playerFaction.getName() + " to " + amount + '.');
        return true;
    }
}

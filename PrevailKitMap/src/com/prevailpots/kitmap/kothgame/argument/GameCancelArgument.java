package com.prevailpots.kitmap.kothgame.argument;

import com.customhcf.util.command.CommandArgument;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.kothgame.EventTimer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class GameCancelArgument extends CommandArgument {
    private final HCF plugin;

    public GameCancelArgument(final HCF plugin) {
        super("cancel", "Cancels a running event", new String[]{"stop", "end"});
        this.plugin = plugin;
        this.permission = "command.game." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
        final Faction eventFaction = eventTimer.getEventFaction();
        if(!eventTimer.clearCooldown()) {
            sender.sendMessage(ChatColor.RED + "There is not a running event.");
            return true;
        }
        Bukkit.broadcastMessage(ChatColor.BLUE + sender.getName() + ChatColor.YELLOW + " has cancelled " +  ((eventFaction == null) ? "the active event" : ChatColor.GOLD +(eventFaction.getName() + ChatColor.YELLOW)) + ".");
        return true;
    }
}

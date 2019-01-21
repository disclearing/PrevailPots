package com.prevailpots.hcf.faction.argument;

import com.customhcf.util.JavaUtils;
import com.customhcf.util.command.CommandArgument;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.type.PlayerFaction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FactionCreateArgument extends CommandArgument {
    private final HCF plugin;

    public FactionCreateArgument(final HCF plugin) {
        super("create", "Start a new faction.", new String[]{"make", "define"});
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <factionName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command may only be executed by players.");
            return true;
        }
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final String name = args[1];
        if(plugin.getConfig().getStringList("Disabled-Faction-Names").contains(name.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "'" + name + "' is a blocked faction name.");
            return true;
        }
        if(name.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Faction names must have at least " + 3 + " characters.");
            return true;
        }
        if(name.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Faction names cannot be longer than " + 16 + " characters.");
            return true;
        }
        if(!JavaUtils.isAlphanumeric(name)) {
            sender.sendMessage(ChatColor.RED + "Faction names may only be alphanumeric.");
            return true;
        }
        if(this.plugin.getFactionManager().getFaction(name) != null) {
            sender.sendMessage(ChatColor.RED + "Faction '" + name + "' already exists.");
            return true;
        }
        if(this.plugin.getFactionManager().getPlayerFaction(((Player) sender).getUniqueId()) != null) {
            sender.sendMessage(ChatColor.RED + "You are already in a faction.");
            return true;
        }
        PlayerFaction shit = new PlayerFaction(name);
        this.plugin.getFactionManager().createFaction(shit, sender);
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getTabApi().onPlayerQuitEvent((Player)sender);
                plugin.getTabApi().onPlayerJoinEvent((Player)sender);
            }
        }.runTaskLaterAsynchronously(plugin, 4L);
        return true;
    }
}

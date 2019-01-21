package com.prevailpots.kitmap.faction.argument;

import com.customhcf.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.event.FactionRelationRemoveEvent;
import com.prevailpots.kitmap.faction.struct.Relation;
import com.prevailpots.kitmap.faction.struct.Role;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.faction.type.PlayerFaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class FactionUnallyArgument extends CommandArgument {
    private static ImmutableList<String> COMPLETIONS;
    private final HCF plugin;

    public FactionUnallyArgument(final HCF plugin) {
        super("unally", "Remove an ally pact with other factions.");
        this.plugin = plugin;
        this.aliases = new String[]{"unalliance", "neutral"};
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <all|factionName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player) sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if(playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if(playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction officer to edit relations.");
            return true;
        }
        final Relation relation = Relation.ALLY;
        final Collection<PlayerFaction> targetFactions = new HashSet<PlayerFaction>();
        if(args[1].equalsIgnoreCase("all")) {
            final Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
            if(allies.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Your faction has no allies.");
                return true;
            }
            targetFactions.addAll(allies);
        } else {
            final Faction searchedFaction = this.plugin.getFactionManager().getContainingFaction(args[1]);
            if(!(searchedFaction instanceof PlayerFaction)) {
                sender.sendMessage(ChatColor.RED + "Player faction named or containing member with IGN or UUID " + args[1] + " not found.");
                return true;
            }
            targetFactions.add((PlayerFaction) searchedFaction);
        }
        for(final PlayerFaction targetFaction : targetFactions) {
            if(playerFaction.getRelations().remove(targetFaction.getUniqueID()) == null || targetFaction.getRelations().remove(playerFaction.getUniqueID()) == null) {
                sender.sendMessage(ChatColor.RED + "Your faction is not " + relation.getDisplayName() + ChatColor.RED + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + '.');
                return true;
            }
            final FactionRelationRemoveEvent event = new FactionRelationRemoveEvent(playerFaction, targetFaction, Relation.ALLY);
            Bukkit.getPluginManager().callEvent((Event) event);
            if(event.isCancelled()) {
                sender.sendMessage(ChatColor.RED + "Could not drop " + relation.getDisplayName() + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + ".");
                return true;
            }
            playerFaction.broadcast(ChatColor.YELLOW + "Your faction has broken its " + relation.getDisplayName() + ChatColor.YELLOW + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + '.');
            targetFaction.broadcast(ChatColor.YELLOW + playerFaction.getDisplayName(targetFaction) + ChatColor.YELLOW + " has dropped their " + relation.getDisplayName() + ChatColor.YELLOW + " with your faction.");
        }
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final Player player = (Player) sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if(playerFaction == null) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(Iterables.concat((Iterable) FactionUnallyArgument.COMPLETIONS, (Iterable) playerFaction.getAlliedFactions().stream().map(Faction::getName).collect(Collectors.toList())));
    }


}

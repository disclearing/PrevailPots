package com.prevailpots.hcf.faction.argument;

import com.customhcf.util.command.CommandArgument;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.FactionMember;
import com.prevailpots.hcf.faction.struct.Relation;
import com.prevailpots.hcf.faction.struct.Role;
import com.prevailpots.hcf.faction.type.PlayerFaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FactionDemoteArgument extends CommandArgument {
    private final HCF plugin;

    public FactionDemoteArgument(final HCF plugin) {
        super("demote", "Demotes a faction officer to an member.", new String[]{"uncaptain", "delcaptain", "delofficer"});
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
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
        if(playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER && playerFaction.getMember(player.getUniqueId()).getRole() != Role.COLEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a leader to edit the roster.");
            return true;
        }
        final FactionMember targetMember = playerFaction.getMember(args[1]);
        if(targetMember == null) {
            sender.sendMessage(ChatColor.RED + "That player is not in your faction.");
            return true;
        }
        if(targetMember.getRole() == Role.LEADER){
            sender.sendMessage(ChatColor.RED + "You cannot demote leader!");
            return true;
        }
        if(targetMember.getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You cannot demote members.");
            return true;
        }
        if(targetMember.getRole() == Role.COLEADER && playerFaction.getMember(player.getUniqueId()).getRole() == Role.COLEADER){
            sender.sendMessage(ChatColor.RED + "You cannot demote other co-leaders.");
            return true;
        }
        targetMember.setRole(Role.MEMBER);
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + targetMember.getName() + ChatColor.YELLOW + " is now a member.");
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final Player player = (Player) sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if(playerFaction == null || (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER && playerFaction.getMember(player.getUniqueId()).getRole() != Role.COLEADER) ){
            return Collections.emptyList();
        }
        final List<String> results = new ArrayList<String>();
        final Collection<UUID> keySet = playerFaction.getMembers().keySet();
        for(final UUID entry : keySet) {
            final OfflinePlayer target = Bukkit.getOfflinePlayer(entry);
            final String targetName = target.getName();
            if(targetName != null && (playerFaction.getMember(target.getUniqueId()).getRole() == Role.CAPTAIN || playerFaction.getMember(target.getUniqueId()).getRole() == Role.COLEADER)) {
                results.add(targetName);
            }
        }
        return results;
    }
}

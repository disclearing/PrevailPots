package com.prevailpots.kitmap.faction.argument;

import com.customhcf.util.command.CommandArgument;
import com.prevailpots.kitmap.ConfigurationService;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.FactionMember;
import com.prevailpots.kitmap.faction.struct.Role;
import com.prevailpots.kitmap.faction.type.PlayerFaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FactionKickArgument extends CommandArgument {
    private final HCF plugin;

    public FactionKickArgument(final HCF plugin) {
        super("kick", "Kick a player from the faction.");
        this.plugin = plugin;
        this.aliases = new String[]{"kickmember", "kickplayer"};
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can kick from a faction.");
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
        if(playerFaction.isRaidable() && !this.plugin.getEotwHandler().isEndOfTheWorld()) {
            sender.sendMessage(ChatColor.RED + "You cannot kick players whilst your faction is raidable.");
            return true;
        }
        final FactionMember targetMember = playerFaction.getMember(args[1]);
        if(targetMember == null) {
            sender.sendMessage(ChatColor.RED + "Your faction does not have a member named '" + args[1] + "'.");
            return true;
        }
        final Role selfRole = playerFaction.getMember(player.getUniqueId()).getRole();
        if(selfRole == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction officer to kick members.");
            return true;
        }
        final Role targetRole = targetMember.getRole();
        if(targetRole == Role.LEADER || targetRole == Role.COLEADER) {
            sender.sendMessage(ChatColor.RED + "You cannot kick a faction leader.");
            return true;
        }
        if(targetRole == Role.CAPTAIN && selfRole == Role.CAPTAIN) {
            sender.sendMessage(ChatColor.RED + "You must be a faction leader to kick captains.");
            return true;
        }
        if(playerFaction.setMember(targetMember.getUniqueId(), null, true)) {
            final Player onlineTarget = targetMember.toOnlinePlayer();
            if(onlineTarget != null) {
                onlineTarget.sendMessage(ChatColor.RED.toString() +  "You were kicked from "+playerFaction.getName()+ '.');
            }
            playerFaction.broadcast(ConfigurationService.ENEMY_COLOUR + targetMember.getName() + ChatColor.YELLOW + " was kicked by " + ConfigurationService.TEAMMATE_COLOUR + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + '.');
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
        final Role memberRole = playerFaction.getMember(player.getUniqueId()).getRole();
        if(memberRole == Role.MEMBER) {
            return Collections.emptyList();
        }
        final List<String> results = new ArrayList<String>();
        for(final UUID entry : playerFaction.getMembers().keySet()) {
            final Role targetRole = playerFaction.getMember(entry).getRole();
            if(targetRole != Role.LEADER) {
                if(targetRole == Role.CAPTAIN && memberRole != Role.LEADER) {
                    continue;
                }
                final OfflinePlayer target = Bukkit.getOfflinePlayer(entry);
                final String targetName = target.getName();
                if(targetName == null || results.contains(targetName)) {
                    continue;
                }
                results.add(targetName);
            }
        }
        return results;
    }
}

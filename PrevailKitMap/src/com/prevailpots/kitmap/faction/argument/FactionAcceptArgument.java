package com.prevailpots.kitmap.faction.argument;

import com.customhcf.util.command.CommandArgument;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.FactionMember;
import com.prevailpots.kitmap.faction.struct.ChatChannel;
import com.prevailpots.kitmap.faction.struct.Relation;
import com.prevailpots.kitmap.faction.struct.Role;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.faction.type.PlayerFaction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FactionAcceptArgument extends CommandArgument {
    private final HCF plugin;

    public FactionAcceptArgument(final HCF plugin) {
        super("accept", "Accept an faction invitation.", new String[]{"join", "a"});
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <factionName>";
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
        if(this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) != null) {
            sender.sendMessage(ChatColor.RED + "You are already in a faction.");
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if(faction == null) {
            sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        if(!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "You can only join player factions.");
            return true;
        }
        final PlayerFaction targetFaction = (PlayerFaction) faction;
        if(targetFaction.getMembers().size() >= 5) { // FACTION LIMIT
            sender.sendMessage(faction.getDisplayName(sender) + ChatColor.RED + " is full. Faction limits are at " + plugin.getHcfHandler().getFactionLimit() + '.');
            return true;
        }
        if(!targetFaction.isOpen() && !targetFaction.getInvitedPlayerNames().contains(player.getName())) {
            sender.sendMessage(ChatColor.RED + faction.getDisplayName(sender) + ChatColor.RED + " has not invited you.");
            return true;
        }
//        if(targetFaction.getKicked().contains(player.getUniqueId().toString()) && !targetFaction.isForce(player.getName())){
//            sender.sendMessage(ChatColor.RED + "This faction has kicked you before and has not used a force-invite!");
//            return true;
//        }
        if(targetFaction.isLocked()){
            sender.sendMessage(ChatColor.RED + "You cannot join locked factions.");
            return  true;
        }
        if(targetFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER))) {
            targetFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.YELLOW + " has joined your faction.");
            new BukkitRunnable() {
                @Override
                public void run() {
                    HCF.getPlugin().getTabApi().onPlayerQuitEvent(player);
                    HCF.getPlugin().getTabApi().onPlayerJoinEvent(player);
                }
            }.runTaskLaterAsynchronously(HCF.getPlugin(), 4L);
        }
        return true;
    }


}

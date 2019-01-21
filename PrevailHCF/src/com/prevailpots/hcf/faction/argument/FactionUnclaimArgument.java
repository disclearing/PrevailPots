package com.prevailpots.hcf.faction.argument;

import com.customhcf.util.chat.ClickAction;
import com.customhcf.util.chat.Text;
import com.customhcf.util.command.CommandArgument;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.FactionMember;
import com.prevailpots.hcf.faction.claim.Claim;
import com.prevailpots.hcf.faction.struct.Role;
import com.prevailpots.hcf.faction.type.PlayerFaction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class FactionUnclaimArgument extends CommandArgument {

    private final HCF plugin;

    public FactionUnclaimArgument(final HCF plugin) {
        super("unclaim", "Unclaims land from your faction.");
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " ";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can un-claim land from a faction.");
            return true;
        }
        final Player player = (Player) sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if(playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        final FactionMember factionMember = playerFaction.getMember(player);
        if(factionMember.getRole() != Role.LEADER && factionMember.getRole() != Role.COLEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction leader to unclaim land.");
            return true;
        }
        final Collection<Claim> factionClaims = playerFaction.getClaims();
        if(factionClaims.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Your faction does not own any claims.");
            return true;
        }

        if(args.length == 2) {
            if(args[1].equalsIgnoreCase("yes")) {
                    for(Claim claims : factionClaims) {
                        playerFaction.removeClaim(claims, player);
                    }
                    factionClaims.clear();
                    return true;
            }
            if(args[1].equalsIgnoreCase("no")) {
                    player.sendMessage(ChatColor.YELLOW + "You have been removed the unclaim-set.");
                    return true;
            }
        }
            new Text(ChatColor.YELLOW + "Do you want to unclaim " + ChatColor.BOLD + "all" + ChatColor.YELLOW + " of your land?").send(player);
            new Text(ChatColor.YELLOW+"If so, "+ChatColor.DARK_GREEN+"/f unclaim yes"+ChatColor.YELLOW+" otherwise do"+ChatColor.DARK_RED+" /f unclaim no"+ChatColor.GRAY+" (Click here to unclaim)").setColor(ChatColor.GRAY).setHoverText(ChatColor.GRAY+"Click here to unclaim all").setClick(ClickAction.RUN_COMMAND, "/f unclaim yes").send(player);
            return true;
    }

}
package com.prevailpots.kitmap.faction.argument.staff;

import com.customhcf.base.BasePlugin;
import com.customhcf.util.ItemBuilder;
import com.customhcf.util.Menu;
import com.customhcf.util.command.CommandArgument;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.FactionMember;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.faction.type.PlayerFaction;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactionPunishArgument extends CommandArgument {
    private final HCF plugin;

    public FactionPunishArgument(final HCF plugin) {
        super("punish", "GUI punishment system");
        this.plugin = plugin;
        this.permission = "command.faction." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <factioName> [reason]";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        String message = StringUtils.join(args, ' ', 2, args.length);
        final Player player = (Player) sender;
        final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if(faction == null) {
            sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        if(!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "You can only punish player factions.");
            return true;
        }
        final PlayerFaction fac = (PlayerFaction) faction;
        Menu menu = new Menu(faction.getName(), 3);
        menu.fill(new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 15).displayName("").build());

        menu.setItem(10, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 0).displayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Freeze Faction").build());

        if(player.hasPermission("command.faction.punish.ban")) {
            menu.setItem(12, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 14).displayName(ChatColor.RED + ChatColor.BOLD.toString() + "Ban Faction").build());
        }else{
            menu.setItem(12, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 14).displayName(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "No Permission").build());
        }

        menu.setItem(14, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 11).displayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Mute Faction").build());

        menu.setItem(16, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 2).displayName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Kick Faction").build());

        menu.runWhenEmpty(false);

        menu.setGlobalAction((player1, inventory, itemStack, slot, inventoryAction) -> {
            switch (slot) {
                case 10: {
                    for (FactionMember factionMember : fac.getMembers().values()) {
                        if (Bukkit.getPlayer(factionMember.getUniqueId()) != null) {
                            Bukkit.dispatchCommand(player1, "freeze " + factionMember.getName());
                        }
                    }
                    Bukkit.broadcastMessage(ChatColor.GREEN + player1.getName() + " has frozen the faction " + fac.getName());
                    break;
                }
                case 12: {
                    if(itemStack.getItemMeta().getDisplayName().contains(ChatColor.DARK_RED.toString())) return;
                    for (FactionMember factionMember : fac.getMembers().values()) {
                        Bukkit.dispatchCommand(player1, "ban -s " + factionMember.getName() + " " + message);
                    }
                    Bukkit.broadcastMessage(ChatColor.GREEN + player1.getName() + " has banned the faction " + fac.getName());
                    break;
                }
                case 14: {
                    for (FactionMember factionMember : fac.getMembers().values()) {
                        Bukkit.dispatchCommand(player1, "mute -s " + factionMember.getName() + " " + message);
                    }
                    Bukkit.broadcastMessage(ChatColor.GREEN + player1.getName() + " has muted the faction " + fac.getName());
                    break;
                }
                case 16: {
                    for (FactionMember factionMember : fac.getMembers().values()) {
                        Bukkit.dispatchCommand(player1, "kick -s " + factionMember.getName() + " " + message);
                    }
                    Bukkit.broadcastMessage(ChatColor.GREEN + player1.getName() + " has kicked the faction " + fac.getName());
                    break;
                }
                default: {
                    return;
                }
            }

        });
        menu.showMenu(player);
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if(args[1].isEmpty()) {
            return null;
        }
        final Player player = (Player) sender;
        final List<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for(final Player target : Bukkit.getOnlinePlayers()) {
            if(player.canSee(target) && !results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }

}

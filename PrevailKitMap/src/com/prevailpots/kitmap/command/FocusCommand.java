package com.prevailpots.kitmap.command;

import com.customhcf.util.BukkitUtils;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.event.FactionFocusChangeEvent;
import com.prevailpots.kitmap.faction.struct.Role;
import com.prevailpots.kitmap.faction.type.PlayerFaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FocusCommand implements CommandExecutor {


    public String getUsage(final String label) {
        return ChatColor.RED +"Usage: /" + label + " <playerName>";
    }



    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
        Player player = (Player) cs;
        if(args.length != 1){
            cs.sendMessage(getUsage(label));
            return true;
        }
        final PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId());
        if(playerFaction == null){
            player.sendMessage(ChatColor.RED + "You must be in a faction!");
            return true;
        }
        if(playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER){
            player.sendMessage(org.bukkit.ChatColor.RED + "You cannot focus a member of your faction!");
            return true;
        }
        if(Bukkit.getPlayer(args[0]) == null && !args[0].equalsIgnoreCase("none")){
            player.sendMessage(org.bukkit.ChatColor.RED + getUsage(label));
            player.sendMessage(ChatColor.RED + "Expected 'playerName' but got " + args[0]);
            return true;
        }
        if(args[0].equalsIgnoreCase("none")){
            playerFaction.broadcast(ChatColor.LIGHT_PURPLE + cs.getName() + ChatColor.YELLOW + " has removed the current focus!");
            Bukkit.getPluginManager().callEvent(new FactionFocusChangeEvent(playerFaction, null, playerFaction.getFocus()));
            return true;
        }
        Bukkit.getPluginManager().callEvent(new FactionFocusChangeEvent(playerFaction, Bukkit.getPlayer(args[0]), playerFaction.getFocus()));
        if(playerFaction.getFocus() == null){
            playerFaction.broadcast(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE + cs.getName() + net.md_5.bungee.api.ChatColor.YELLOW + " has focused " + net.md_5.bungee.api.ChatColor.DARK_PURPLE + args[0]);
        }else {
            playerFaction.broadcast(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE + cs.getName() + net.md_5.bungee.api.ChatColor.YELLOW + " has removed the focus on " + net.md_5.bungee.api.ChatColor.DARK_PURPLE + BukkitUtils.offlinePlayerWithNameOrUUID(playerFaction.getFocus().toString()).getName() + net.md_5.bungee.api.ChatColor.YELLOW + " and has focused " + net.md_5.bungee.api.ChatColor.DARK_PURPLE + args[0]);
        }
            playerFaction.setFocus(Bukkit.getPlayer(args[0]).getUniqueId());
        return false;
    }
}

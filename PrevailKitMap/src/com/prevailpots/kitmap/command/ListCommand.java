package com.prevailpots.kitmap.command;

import com.customhcf.base.command.BaseCommand;
import com.prevailpots.kitmap.HCF;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spirit on 05/08/2017.
 */
public class ListCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
        final List<String> list = new ArrayList<String>();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("command.list.own") && BaseCommand.canSee(s, player)) {
                list.add(player.getName());
            }
        }

        int playerCount = HCF.getPlugin().getPlayers();
        s.sendMessage("§7§m----------------------------------------------------------");
        s.sendMessage(ChatColor.RED + "There are currently " + ChatColor.WHITE + playerCount + ChatColor.GRAY + "/" + ChatColor.WHITE + Bukkit.getMaxPlayers() + ChatColor.RED + " players online.");
        s.sendMessage(ChatColor.RED + "Staff Online: ");
        s.sendMessage(ChatColor.GREEN + list.toString().replace("[", "").replace("]", "").replace(",", ChatColor.GRAY + "," + ChatColor.GREEN));
        s.sendMessage("§7§m----------------------------------------------------------");
        return true;
    }
}

package com.prevailpots.kitmap.balance;

import com.customhcf.base.Constants;
import com.google.common.primitives.Ints;
import com.prevailpots.kitmap.HCF;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PayCommand implements CommandExecutor, TabCompleter {
    private final HCF plugin;

    public PayCommand(final HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName> <amount>");
            return true;
        }
        final Integer amount = Ints.tryParse(args[1]);
        if(amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        if(amount <= 0) {
            sender.sendMessage(ChatColor.RED + "You must send money in positive quantities.");
            return true;
        }
        final Player senderPlayer = (Player) sender;
        final int senderBalance = (senderPlayer != null) ? this.plugin.getEconomyManager().getBalance(senderPlayer.getUniqueId()) : 1024;
        if(senderBalance < amount) {
            sender.sendMessage(ChatColor.RED + "You do not have that much money, you have: " + ChatColor.GREEN + senderBalance);
            return true;
        }
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if(sender.equals(target)) {
            sender.sendMessage(ChatColor.RED + "You cannot send money to yourself.");
            return true;
        }
        final Player targetPlayer = target.getPlayer();
        if(!target.hasPlayedBefore() && targetPlayer == null) {
            sender.sendMessage(String.format(Constants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }
        if(targetPlayer == null) {
            return false;
        }
        if(senderPlayer != null) {
            this.plugin.getEconomyManager().subtractBalance(senderPlayer.getUniqueId(), amount);
        }
        this.plugin.getEconomyManager().addBalance(targetPlayer.getUniqueId(), amount);
        targetPlayer.sendMessage(ChatColor.GOLD + sender.getName() + " has sent you " + ChatColor.GOLD + EconomyManager.ECONOMY_SYMBOL + amount + ChatColor.YELLOW + '.');
        sender.sendMessage(ChatColor.YELLOW + "You have sent " + ChatColor.GOLD + EconomyManager.ECONOMY_SYMBOL + amount + ChatColor.YELLOW + " to " + target.getName() + '.');
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 1) ? null : Collections.emptyList();
    }
}

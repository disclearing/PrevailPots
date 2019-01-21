package com.prevailpots.kitmap.lives.argument;

import com.customhcf.base.Constants;
import com.customhcf.util.command.CommandArgument;
import com.google.common.primitives.Ints;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.lives.LivesType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LivesGiveArgument extends CommandArgument {
    private final HCF plugin;
    private final static String PERMISSION;

    static{
        PERMISSION = "command.stafflives.argument.give.bypass";
    }

    public LivesGiveArgument(final HCF plugin) {
        super("give", "Give lives to a player");
        this.plugin = plugin;
        this.aliases = new String[]{"transfer", "send", "pay", "add"};
        this.permission = "command.stafflives." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName> <amount>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Integer amount = Ints.tryParse(args[2]);
        if(amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
            return true;
        }
        if(amount <= 0) {
            sender.sendMessage(ChatColor.RED + "The amount of lives must be positive.");
            return true;
        }
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if(!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(String.format(Constants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[1]));
            return true;
        }
        final Player onlineTarget = target.getPlayer();
        if(sender.hasPermission(PERMISSION) && args.length == 4 && (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("yes"))){
            plugin.getUserManager().getUser(target.getUniqueId()).addSoulLives(amount);
            sender.sendMessage(ChatColor.YELLOW + target.getName() + " now has " + ChatColor.GOLD + amount + ChatColor.YELLOW + " soul bound lives.");
            return true;
        }
        plugin.getUserManager().getUser(target.getUniqueId()).addLives(amount);
        sender.sendMessage(ChatColor.YELLOW + "You have sent " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + ' ' + amount + ' ' + ((amount > 1) ? "life" : "lives") + '.');
        if(onlineTarget != null) {
            onlineTarget.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " has sent you " + ChatColor.GOLD + amount + ' ' + ((amount > 1) ? "life" : "lives") + '.');
        }
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}

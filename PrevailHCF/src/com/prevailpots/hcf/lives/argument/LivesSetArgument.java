package com.prevailpots.hcf.lives.argument;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.customhcf.base.Constants;
import com.customhcf.util.BukkitUtils;
import com.customhcf.util.command.CommandArgument;
import com.google.common.primitives.Ints;
import com.prevailpots.hcf.HCF;

public class LivesSetArgument extends CommandArgument {
    private final HCF plugin;

    public LivesSetArgument(final HCF plugin) {
        super("set", "Set how much lives a player has");
        this.plugin = plugin;
        this.permission = "command.stafflives." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName> <amount> [soulBound]";
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
        final OfflinePlayer target = BukkitUtils.offlinePlayerWithNameOrUUID(args[1]);
        if(!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(String.format(Constants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[1]));
            return true;
        }
        if(args.length == 4 && (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("yes"))){
            plugin.getUserManager().getUser(target.getUniqueId()).setSouLives(amount);
            sender.sendMessage(ChatColor.YELLOW + target.getName() + " now has " + ChatColor.GOLD + amount + ChatColor.YELLOW + " soul bound lives.");
            return true;
        }
        plugin.getUserManager().getUser(target.getUniqueId()).setLives(amount);
        sender.sendMessage(ChatColor.YELLOW + target.getName() + " now has " + ChatColor.GOLD + amount + ChatColor.YELLOW + " lives.");
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}

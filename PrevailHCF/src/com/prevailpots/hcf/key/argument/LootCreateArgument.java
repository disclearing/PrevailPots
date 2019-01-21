package com.prevailpots.hcf.key.argument;

import com.customhcf.util.command.CommandArgument;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.key.Key;
import com.prevailpots.hcf.key.type.CustomKey;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LootCreateArgument extends CommandArgument {
    private final HCF plugin;

    public LootCreateArgument(final HCF plugin) {
        super("create", "Creates a key");
        this.plugin = plugin;
        this.permission = "command.key." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <name>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Key key = this.plugin.getKeyManager().getKey(args[1]);
        if(key != null) {
            sender.sendMessage(ChatColor.RED + "There is already a key named '" + ChatColor.GRAY + args[1]  +ChatColor.RED + "'.");
            return true;
        }
        CustomKey realKey = new CustomKey(args[1]);
        plugin.getKeyManager().getKeys().add(realKey);
        sender.sendMessage(ChatColor.YELLOW + "Creating key " + realKey.getColour() + realKey.getName() + ChatColor.YELLOW + " with " + ChatColor.GOLD + 3 + ChatColor.YELLOW + " items given per roll.");
        return true;
    }

}

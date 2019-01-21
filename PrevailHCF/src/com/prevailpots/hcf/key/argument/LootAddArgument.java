package com.prevailpots.hcf.key.argument;

import com.customhcf.util.command.CommandArgument;
import com.google.common.primitives.Ints;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.key.Key;
import com.prevailpots.hcf.key.RewardableItemStack;
import com.prevailpots.hcf.key.type.CustomKey;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LootAddArgument extends CommandArgument {
    private final HCF plugin;

    public LootAddArgument(final HCF plugin) {
        super("add", "adds a item to the keys");
        this.plugin = plugin;
        this.permission = "command.key." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <keyName> <percentage>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        if(!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        final Key key = this.plugin.getKeyManager().getKey(args[1]);
        if(key == null) {
            sender.sendMessage(ChatColor.RED + "There is not a key named '" + ChatColor.GRAY + args[1]  +ChatColor.RED + "'.");
            return true;
        }
        if(!(key instanceof CustomKey)){
            sender.sendMessage(ChatColor.RED + "It must be a custom key to add or remove items.");
            return true;
        }
        CustomKey customKey = (CustomKey) key;
        Integer percentage = Ints.tryParse(args[2]);
        if(percentage == null) {
            sender.sendMessage(ChatColor.RED + "'" + ChatColor.GRAY + args[2] + ChatColor.RED + "' is not a number.");
            return true;
        }
        if(percentage <= 0) {
            sender.sendMessage(ChatColor.RED + "You can only give keys in positive quantities.");
            return true;
        }
        customKey.addItem(new RewardableItemStack(player.getItemInHand(), percentage, (player.getItemInHand().getAmount() == 0 ? 1 : player.getItemInHand().getAmount())));
        sender.sendMessage(ChatColor.YELLOW + "Adding " + ChatColor.GOLD + player.getItemInHand().getType() + ChatColor.YELLOW +  " to " + customKey.getColour() + customKey.getName() + ChatColor.YELLOW + " at " + ChatColor.GOLD +  percentage+ '%'+ChatColor.YELLOW +".");
        return true;
    }

}

package com.prevailpots.hcf.command;

import com.customhcf.util.BukkitUtils;
import com.google.common.base.Optional;
import com.google.common.primitives.Ints;
import com.prevailpots.hcf.crowbar.Crowbar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CrowbarCommand implements CommandExecutor, TabExecutor {
    private final List<String> completions;

    public CrowbarCommand() {
        this.completions = Arrays.asList("spawn", "setspawneruses", "setframeuses", "setegguses");
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player) && args.length != 2) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <spawn|setSpawnerUses|setFrameUses> (playerName)");
            return true;
        }
       Player player = null;
        if(sender instanceof Player) {
           player = (Player) sender;
        }
        if (args[0].equalsIgnoreCase("spawn")) {
            if(args.length == 1) {
                final ItemStack stack = new Crowbar().getItemIfPresent();
                player.getInventory().addItem(new ItemStack[]{stack});
                sender.sendMessage(ChatColor.YELLOW + "You have given yourself a " + stack.getItemMeta().getDisplayName() + ChatColor.YELLOW + '.');
                return true;
            }else{
                if(args.length != 2){
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <spawn|setSpawnerUses|setFrameUses|setEggUses> (playerName)");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if(target == null){
                    return true;
                }
                final ItemStack stack = new Crowbar().getItemIfPresent();
                target.getInventory().addItem(new ItemStack[]{stack});
                return true;
            }
        }
        final Optional<Crowbar> crowbarOptional = Crowbar.fromStack(player.getItemInHand());
        if (!crowbarOptional.isPresent()) {
            sender.sendMessage(ChatColor.RED + "You are not holding a Crowbar.");
            return true;
        }
        if (args[0].equalsIgnoreCase("setspawneruses")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
                return true;
            }
            final Integer amount = Ints.tryParse(args[1]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
                return true;
            }
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "You cannot set Spawner uses to an amount less than " + 0 + '.');
                return true;
            }
            if (amount > 1) {
                sender.sendMessage(ChatColor.RED + "Crowbars have maximum Spawner uses of " + 1 + '.');
                return true;
            }
            final Crowbar crowbar = (Crowbar) crowbarOptional.get();
            crowbar.setSpawnerUses(amount);
            player.setItemInHand(crowbar.getItemIfPresent());
            sender.sendMessage(ChatColor.YELLOW + "Set Spawner uses of held Crowbar to " + amount + '.');
            return true;
        }
        if (args[0].equalsIgnoreCase("setframeuses")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
                return true;
            }
            final Integer amount = Ints.tryParse(args[1]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not an integer.");
                return true;
            }
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "You cannot set End Portal Frame  uses to an amount less than " + 0 + '.');
                return true;
            }
            if (amount > 5) {
                sender.sendMessage(ChatColor.RED + "Crowbars have maximum End Portal Frame uses of " + 5 + '.');
                return true;
            }
            final Crowbar crowbar = (Crowbar) crowbarOptional.get();
            crowbar.setEndFrameUses(amount);
            player.setItemInHand(crowbar.getItemIfPresent());
            sender.sendMessage(ChatColor.YELLOW + "Set End Portal Frame uses of held Crowbar to " + amount + '.');
            return true;
        }
        if (args[0].equalsIgnoreCase("setegguses")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
                return true;
            }
            final Integer amount = Ints.tryParse(args[1]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
                return true;
            }
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "You cannot set End Dragon Egg uses to an amount less than " + 0 + '.');
                return true;
            }
            if (amount > 1) {
                sender.sendMessage(ChatColor.RED + "Crowbars have maximum End Dragon Egg uses of " + 1 + '.');
                return true;
            }
            final Crowbar crowbar = (Crowbar) crowbarOptional.get();
            player.setItemInHand(crowbar.getItemIfPresent());
            sender.sendMessage(ChatColor.YELLOW + "Set End Dragon Egg uses of held Crowbar to " + amount + '.');
            return true;
        }
        return true;
    }


    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 1) ? BukkitUtils.getCompletions(args, this.completions) : Collections.emptyList();
    }
}

package com.prevailpots.kitmap.key.argument;

import com.customhcf.util.command.CommandArgument;
import com.google.common.primitives.Ints;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.key.Key;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LootGiveArgument extends CommandArgument {
    private final HCF plugin;

    public LootGiveArgument(final HCF plugin) {
        super("give", "Gives a key key to a player");
        this.plugin = plugin;
        this.aliases = new String[]{"send"};
        this.permission = "command.key." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName|all> <type> [amount]";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }

        if(args[1].equalsIgnoreCase("all")){
            for(Player on : Bukkit.getOnlinePlayers()){
                final Key key = this.plugin.getKeyManager().getKey(args[2]);
                if(key == null) {
                    sender.sendMessage(ChatColor.RED + "There is no key type named '" + args[2] + "'.");
                    return true;
                }
                Integer quantity;
                if(args.length >= 4) {
                    quantity = Ints.tryParse(args[3]);
                    if(quantity == null) {
                        sender.sendMessage(ChatColor.RED + "'" + args[3] + "' is not a number.");
                        return true;
                    }
                } else {
                    quantity = 1;
                }
                if(quantity <= 0) {
                    sender.sendMessage(ChatColor.RED + "You can only give keys in positive quantities.");
                    return true;
                }
                ItemStack stack = key.getItemStack().clone();
                final int maxAmount = 16;
                if(quantity > maxAmount) {
                    sender.sendMessage(ChatColor.RED + "You cannot give keys in quantities more than " + maxAmount + '.');
                    return true;
                }
                stack.setAmount(quantity);
                final PlayerInventory inventory = on.getInventory();
                final Location location = on.getLocation();
                final World world = on.getWorld();
                final Map<Integer, ItemStack> excess = inventory.addItem(stack);
                for(final ItemStack entry : excess.values()) {
                    world.dropItemNaturally(location, entry);
                }
            }
            Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Given "+ChatColor.GOLD + "all"+ChatColor.YELLOW+" players keys",true);
            return true;
        }
        final Player target = Bukkit.getPlayer(args[1]);
        if(target == null || (sender instanceof Player && !((Player) sender).canSee(target))) {
            sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.GRAY + args[1] + ChatColor.RED + "' not found.");
            return true;
        }
        final Key key = this.plugin.getKeyManager().getKey(args[2]);
        if(key == null) {
            sender.sendMessage(ChatColor.RED + "There is no key type named '" + args[2] + "'.");
            return true;
        }
        Integer quantity;
        if(args.length >= 4) {
            quantity = Ints.tryParse(args[3]);
            if(quantity == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[3] + "' is not a number.");
                return true;
            }
        } else {
            quantity = 1;
        }
        if(quantity <= 0) {
            sender.sendMessage(ChatColor.RED + "You can only give keys in positive quantities.");
            return true;
        }
        ItemStack stack = key.getItemStack().clone();
        final int maxAmount = 16;
        if(quantity > maxAmount) {
            sender.sendMessage(ChatColor.RED + "You cannot give keys in quantities more than " + maxAmount + '.');
            return true;
        }
        stack.setAmount(quantity);
        final PlayerInventory inventory = target.getInventory();
        final Location location = target.getLocation();
        final World world = target.getWorld();
        final Map<Integer, ItemStack> excess = inventory.addItem(stack);
        for(final ItemStack entry : excess.values()) {
            world.dropItemNaturally(location, entry);
        }
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Given " + ChatColor.GOLD + quantity + ChatColor.YELLOW + "x " + key.getDisplayName() + ChatColor.YELLOW + " key to " + ChatColor.BLUE + target.getName() + ChatColor.YELLOW + '.');
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length == 2) {
            return null;
        }
        if(args.length == 3) {
            return this.plugin.getKeyManager().getKeys().stream().map(Key::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}

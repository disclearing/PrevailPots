package com.prevailpots.kitmap.command;

import com.customhcf.util.BukkitUtils;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.primitives.Ints;
import com.prevailpots.kitmap.HCF;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetBorderCommand implements CommandExecutor, TabExecutor {
    private static final int MIN_SET_SIZE = 50;
    private static final int MAX_SET_SIZE = 25000;

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <worldType> <amount>");
            return true;
        }
        final Optional<World.Environment> optional = (Optional<World.Environment>) Enums.getIfPresent((Class) World.Environment.class, args[0]);
        if(!optional.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Environment '" + args[0] + "' not found.");
            return true;
        }
        final Integer amount = Ints.tryParse(args[1]);
        if(amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        if(amount < MIN_SET_SIZE) {
            sender.sendMessage(ChatColor.RED + "Minimum border size is " + 50 + 100 + '.');
            return true;
        }
        if(amount > MAX_SET_SIZE) {
            sender.sendMessage(ChatColor.RED + "Maximum border size is " + 25000 + '.');
            return true;
        }
        final World.Environment environment = optional.get();
        HCF.getPlugin().getHcfHandler().setWorldBorder(environment, amount);

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set border size of environment " + ChatColor.GOLD  + environment.name() + ChatColor.YELLOW + " to " +ChatColor.GOLD+ amount + ChatColor.YELLOW + '.');
        return true;
    }


    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length != 1) {
            return Collections.emptyList();
        }
        final World.Environment[] values = World.Environment.values();
        final List<String> results = new ArrayList<String>(values.length);
        for(final World.Environment environment : values) {
            results.add(environment.name());
        }
        return BukkitUtils.getCompletions(args, results);
    }
}

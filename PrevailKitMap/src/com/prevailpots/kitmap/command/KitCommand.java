package com.prevailpots.kitmap.command;

import com.customhcf.base.BasePlugin;
import com.customhcf.base.kit.Kit;
import com.prevailpots.kitmap.HCF;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;


/**
 * Created by Spirit on 21/07/2017.
 */
public class KitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to run this command.");
            return true;
        }

        if(args.length < 1) {
            commandSender.sendMessage(ChatColor.RED + "Invalid usage. /kit <kitName>");
            return true;
        }

        Player p = (Player)commandSender;
        String kitName = args[0];
        Kit kit = BasePlugin.getPlugin().getKitManager().getKit(kitName);
        if(kit != null && p.hasPermission(kit.getPermissionNode())) {
            //kit.applyTo(p, false, true);
            kit.applyTo(p, false, true);
            return true;
        }

        p.sendMessage(ChatColor.RED + "Kit " + kitName + " not found.");

        return true;
    }
}

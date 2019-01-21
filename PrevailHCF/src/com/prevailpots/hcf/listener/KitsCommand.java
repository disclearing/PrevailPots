package com.prevailpots.hcf.listener;

import com.customhcf.base.BasePlugin;
import com.customhcf.base.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spirit on 21/07/2017.
 */
public class KitsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to run this command.");
            return true;
        }

        Player p = (Player)commandSender;
        StringBuilder sb = new StringBuilder();

        for(Kit k : BasePlugin.getPlugin().getKitManager().getKits()) {
            if(p.hasPermission(k.getPermissionNode())) {
                sb.append(ChatColor.GREEN + k.getDisplayName() + ", ");
            }
        }

        if(sb.length() == 0) {
            p.sendMessage(ChatColor.RED + "You do not have any kits available. You can buy one at " + ChatColor.AQUA + " http://buy.prevailpots.com");
        } else {
            p.sendMessage(ChatColor.YELLOW + "Available kits: " + sb.toString());
        }

        return true;
    }
}

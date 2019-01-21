package com.prevailpots.kitmap.command;

import com.customhcf.base.BasePlugin;
import com.prevailpots.kitmap.HCF;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by HelpMe on 12/9/2015.
 */
public class RandomCommand implements CommandExecutor {
    private final HCF plugin;
    public RandomCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player) sender;
        List<Player> players = new ArrayList<Player>();
        for (Player players1 : Bukkit.getOnlinePlayers()) {
            if (players1.getLocation().getBlockY() < 30) {
                if (BasePlugin.getPlugin().getUserManager().getUser(players1.getUniqueId()).isStaffUtil()) {
                    continue;
                }
                players.add(players1);
            }
            players.add(players1);
        }

        Collections.shuffle(players);
        Random random = new Random();
        Integer randoms = random.nextInt(players.size());
        Player p = players.get(randoms);
        if (players.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No players mining!");
            return true;
        }
        if (players == sender){
            return false;
        }
        if(player.canSee(p) && player.hasPermission(command.getPermission()+".teleport")){
            player.teleport(p);
            player.sendMessage(ChatColor.DARK_AQUA+"You've teleported to "+ ChatColor.BLUE +p.getName());
        }else if(player.canSee(p)) {
            player.sendMessage(ChatColor.DARK_AQUA+"You've found "+p.getName());
        }else{
            player.sendMessage(ChatColor.RED + "Player not found");
        }
        return true;
    }

}

package com.prevailpots.kitmap.listener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.prevailpots.kitmap.HCF;

/**
 * Created by Spirit on 05/08/2017.
 */
public class ReloadTabCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player)commandSender;

        new BukkitRunnable() {
            @Override
            public void run() {
                HCF.getPlugin().getTabApi().onPlayerQuitEvent(player);
                HCF.getPlugin().getTabApi().onPlayerJoinEvent(player);
                player.sendMessage("§EYou have §a§lenabled §eyour custom tab!");
            }
        }.runTaskLaterAsynchronously(HCF.getPlugin(), 4L);

        return false;
    }
}

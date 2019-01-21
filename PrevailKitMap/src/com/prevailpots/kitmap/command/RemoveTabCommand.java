package com.prevailpots.kitmap.command;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.alexandeh.kraken.Kraken;
import com.alexandeh.kraken.tab.PlayerTab;
import com.alexandeh.kraken.tab.event.PlayerTabRemoveEvent;
import com.prevailpots.kitmap.HCF;

public class RemoveTabCommand implements CommandExecutor {

	
	
	
	@Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player)commandSender;

        new BukkitRunnable() {
            @Override
            public void run() {
            	//HCF.getPlugin().getTabApi().onPlaryerQuitEvent(player);
            	command.tabCompleteTimings.reset();
            	player.sendMessage("§eYou have §6§lremoved §eyour custom tab!");
            }
        }.runTaskLaterAsynchronously(HCF.getPlugin(), 4L);

        return true;
    }
}
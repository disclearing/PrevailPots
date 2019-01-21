package com.prevailpots.bunkers.commands;


import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.prevailpots.bunkers.Core;

import me.vertises.aztec.tablist.ClientVersion;


public class RemoveTab implements CommandExecutor {

	
	
	
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            return true;
        }
		return true;
	}
	public boolean getEntries(Player player) {
    		Table<Integer, Integer, String> table = HashBasedTable.create();
            	int index = 0;
            	int magic = ClientVersion.getVersion(player).ordinal() != 0 ? 4 : 3;
            	for (Player next : Bukkit.getOnlinePlayers()) {
            	  int x = index % magic;
            	  int y = index / magic;
            	  table.put(x, y, next.getName());
            	player.sendMessage("§eYou have §6§lremoved §eyour custom tab!");
    	}
            	return true;
    }
}

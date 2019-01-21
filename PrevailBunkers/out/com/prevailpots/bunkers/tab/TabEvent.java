package com.prevailpots.bunkers.tab;

import org.bukkit.entity.Player;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import me.vertises.aztec.tablist.TablistEntrySupplier;
import net.md_5.bungee.api.ChatColor;

public class TabEvent implements TablistEntrySupplier {

	
	@Override
	public Table<Integer, Integer, String> getEntries(Player player) {
		Table<Integer, Integer, String> table = HashBasedTable.create();
		table.put(1, 0, ChatColor.AQUA + "Prevail" + ChatColor.GRAY + " [Bunkers]");
		table.put(0, 3, ChatColor.AQUA + "Bunkers:" + ChatColor.GRAY + " [Online]");
		table.put(0, 4, ChatColor.AQUA + "HCFactions:" + ChatColor.GRAY + " [Offline]");
		table.put(0, 5, ChatColor.AQUA + "Practice:" + ChatColor.GRAY + " [Offline]");
		
		//mid
		table.put(1, 3, ChatColor.AQUA + "Wins:" + ChatColor.GRAY + " [3]");
		table.put(1, 4, ChatColor.AQUA + "Team:" + ChatColor.GRAY + " [Blue]");
		table.put(1, 5, ChatColor.AQUA + "Proxy:" + ChatColor.GRAY + " [EU-1]");
		
		table.put(2, 3, ChatColor.AQUA + "Kills:" + ChatColor.GRAY + " [13]");
		table.put(2, 4, ChatColor.AQUA + "Deaths:" + ChatColor.GRAY + " [41]");
		table.put(2, 5, ChatColor.AQUA + "Online:" + ChatColor.GRAY + " [1]");
		
		table.put(0, 19, ChatColor.AQUA + "1.8 Fixed" + ChatColor.GRAY + "");
		table.put(1, 19, ChatColor.AQUA + "1.8 Fixed" + ChatColor.GRAY + "");
		table.put(2, 19, ChatColor.AQUA + "1.8 Fixed" + ChatColor.GRAY + "");
		table.put(3, 19, ChatColor.AQUA + "1.8 Fixed" + ChatColor.GRAY + "");
		//
		
		table.put(3, 0, ChatColor.GRAY + "You are using 1.8" + ChatColor.YELLOW + "");
		table.put(3, 1, ChatColor.GRAY + "Please use 1.7 for" + ChatColor.YELLOW + "");
		table.put(3, 2, ChatColor.GRAY + "For the best quality!" + ChatColor.YELLOW + "");
		return table;
	}

	@Override
	public String getHeader(Player player) {
		return "§bYou are connected to 1.8 Bunkers!";
	}

	@Override
	public String getFooter(Player player) {
		return "§CWe suggest using 1.7 for the highest quality!";
	}

}

package me.vertises.aztec.tablist.example;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import me.vertises.aztec.tablist.TablistEntrySupplier;

import net.md_5.bungee.api.ChatColor;


public class ExampleSupplier implements TablistEntrySupplier {

	
	@Override
	public Table<Integer, Integer, String> getEntries(Player player) {
		Table<Integer, Integer, String> table = HashBasedTable.create();
		table.put(1, 0, ChatColor.AQUA + "Prevail" + ChatColor.GRAY + " [Bunkers]");
		table.put(1, 2, ChatColor.AQUA + "Bunkers:" + ChatColor.GRAY + " [Offline]");
		table.put(1, 3, ChatColor.AQUA + "HCFactions:" + ChatColor.GRAY + " [Offline]");
		table.put(1, 4, ChatColor.AQUA + "Practice:" + ChatColor.GRAY + " [Offline]");
		return table;
	}

	@Override
	public String getHeader(Player player) {
		return "Godly header";
	}

	@Override
	public String getFooter(Player player) {
		return "Godly footer";
	}

}

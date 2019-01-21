package com.prevailpots.kitmap.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.ibex.nestedvm.Runtime;
import org.bukkit.enchantments.Enchantment;

import com.prevailpots.kitmap.ConfigurationService;

public class MapKitCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        cs.sendMessage(ChatColor.YELLOW + "The current map kit is: " + ChatColor.GOLD+ "Protection: " + ChatColor.GRAY + ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL) + ChatColor.YELLOW + ", " + org.bukkit.ChatColor.GOLD + "Sharpness: " + ChatColor.GRAY + ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL));
        return false;
    }
}

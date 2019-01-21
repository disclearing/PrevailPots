package com.prevailpots.kitmap.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;

import com.prevailpots.kitmap.HCF;

import java.util.Collections;
import java.util.List;

/**
 * Created by TREHOME on 01/28/2016.
 */
public class ToggleEndCommand implements CommandExecutor, TabExecutor {
    private final HCF plugin;

    public ToggleEndCommand(final HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final boolean newMode = !plugin.getHcfHandler().isEndEnabled();
        plugin.getHcfHandler().setEndEnabled(newMode);
        Bukkit.broadcastMessage(ChatColor.YELLOW + "The End is now " + (newMode ? ChatColor.RED + "closed" : ChatColor.GREEN + "open") + ChatColor.YELLOW + '.');
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
package com.prevailpots.hcf.command;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.event.inventory.*;

import com.prevailpots.hcf.user.GUIManager;

import org.bukkit.event.*;

public class TabCommand implements CommandExecutor, Listener
{
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        final Player p = (Player)sender;
        p.openInventory(GUIManager.GUI);
        p.sendMessage(ChatColor.YELLOW + "Opening the " + ChatColor.GOLD + "Tab Managment " + ChatColor.GRAY + " Click Gui!");
        return false;
    }
    
    @EventHandler
    public void onPlayerClick(final InventoryClickEvent e) {
        final Player p = (Player)e.getWhoClicked();
        if (e.getCurrentItem().getItemMeta() == null) {
            return;
        }
        if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Disable")) {
            e.setCancelled(true);
            p.performCommand("removetab");
            e.getWhoClicked().closeInventory();
        }
        if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Enable")) {
            e.setCancelled(true);
            p.performCommand("reloadtab");
            e.getWhoClicked().closeInventory();
        }
    }
}

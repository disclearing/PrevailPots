package com.prevailpots.kitmap.user;

import org.bukkit.inventory.*;
import org.bukkit.*;
import org.bukkit.inventory.meta.*;

/*
 * This was created by S3ries copied from my prac gui :p
*/

public class GUIManager
{
    private static ItemStack enable;
    private static ItemStack disable;
    public static Inventory GUI;
    
    static {
        GUIManager.GUI = Bukkit.createInventory((InventoryHolder)null, 27, "§eTab Manager");
        GUIManager.enable = togglechat1(ChatColor.translateAlternateColorCodes('&', "&aEnable Custom Tab"));
        GUIManager.disable = time1(ChatColor.translateAlternateColorCodes('&', "&cDisable Custom Tab"));
        GUIManager.GUI.setItem(11, GUIManager.enable);
        GUIManager.GUI.setItem(15, GUIManager.disable);
    }
    
    private static ItemStack togglechat1(final String name) {
        final ItemStack togglechat = new ItemStack(Material.EMERALD_BLOCK);
        final ItemMeta togglechatm = togglechat.getItemMeta();
        togglechatm.setDisplayName(name);
        togglechat.setItemMeta(togglechatm);
        return togglechat;
    }
    

    
    private static ItemStack time1(final String name) {
        final ItemStack time = new ItemStack(Material.REDSTONE_BLOCK);
        final ItemMeta timem = time.getItemMeta();
        timem.setDisplayName(name);
        time.setItemMeta(timem);
        return time;
    }
}

package com.prevailpots.bunkers.game.managers;

import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import com.prevailpots.bunkers.utils.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.material.Wool;

import java.util.*;

public class ItemManager
{
    private ArrayList<ItemStack> starterItems;
    
    public ItemManager() {
        (this.starterItems = new ArrayList<ItemStack>()).add(ItemUtil.getItem("§fStarter Sword", Material.STONE_SWORD, 1, "§7Just an ordinary §8Stone Sword§7."));
        this.starterItems.add(ItemUtil.getItem("§fStarter Pick", Material.IRON_PICKAXE, 1, "§7Just an ordinary §fIron Pickaxe§7."));
    }
    
    public void giveStarterItems(final Player p) {
        for (final ItemStack i : this.starterItems) {
            p.getInventory().addItem(new ItemStack[] { i });
        }
    }

    public void giveTeamItems(Player p) {
        ItemStack blue, green, red, leave, yellow;
        blue = new Wool(DyeColor.BLUE).toItemStack(1);
        green = new Wool(DyeColor.GREEN).toItemStack(1);
        red = new Wool(DyeColor.RED).toItemStack(1);
        yellow = new Wool(DyeColor.YELLOW).toItemStack(1);
        leave = new Wool(DyeColor.WHITE).toItemStack(1);
        
        //blue
        ItemMeta imMeta = blue.getItemMeta();
        imMeta.setDisplayName("§bRight Click to join Blue Team!");
        blue.setItemMeta(imMeta);
        //red
        ItemMeta imMeta1 = red.getItemMeta();
        imMeta1.setDisplayName("§CRight Click to join Red Team!");
        red.setItemMeta(imMeta1);
        //green
        ItemMeta imMeta2 = green.getItemMeta();
        imMeta2.setDisplayName("§ARight Click to join GREEN Team!");
        green.setItemMeta(imMeta2);
        //yellow
        ItemMeta imMeta3 = yellow.getItemMeta();
        imMeta3.setDisplayName("§ERight Click to join Yellow Team!");
        yellow.setItemMeta(imMeta3);
        //leave
        ItemMeta imMeta4 = leave.getItemMeta();
        imMeta4.setDisplayName("§EClick here to leave the bunkers game!");
        leave.setItemMeta(imMeta4);
        
        p.getInventory().setItem(0, blue); // 4, 5, 6, 7
        p.getInventory().setItem(1, green); // 4, 5, 6, 7
        p.getInventory().setItem(2, red); // 4, 5, 6, 7
        p.getInventory().setItem(3, yellow); // 4, 5, 6, 7
        p.getInventory().setItem(8, leave); // 4, 5, 6, 7
        p.updateInventory();
    }
}
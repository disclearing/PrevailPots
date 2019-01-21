package com.prevailpots.hcf.key;

import com.customhcf.util.Config;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class EnderChestKey extends Key {
    private final ItemStack[] items;
    private int rolls;

    public EnderChestKey(final String name, final int rolls) {
        super(name);
        this.items = new ItemStack[100];
        this.rolls = rolls;
    }

    public boolean getBroadcastItems() {
        return false;
    }

    public int getRolls() {
        return this.rolls;
    }

    public void setRolls(final int rolls) {
        this.rolls = rolls;
    }

    public ItemStack[] getLoot() {
        return Arrays.copyOf(this.items, this.items.length);
    }

    public void setupRarity(final ItemStack stack, final int percent) {
        int currentItems = 0;
        for(final ItemStack item : this.items) {
            if(item != null && item.getType() != Material.AIR) {
                ++currentItems;
            }
        }
        for(int min = Math.min(100, currentItems + percent), i = currentItems; i < min; ++i) {
            this.items[i] = stack;
        }
    }

    @Override
    public ChatColor getColour() {
        return ChatColor.GOLD;
    }

    @Override
    public ItemStack getItemStack() {
        final ItemStack stack = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(this.getColour() + this.getName() + " Key");
        meta.setLore(Lists.newArrayList(new String[]{ChatColor.YELLOW + "Click an Ender Chest in a safe claim to use this key."}));
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public void save(Config config){

    }
}

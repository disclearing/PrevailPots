package com.prevailpots.bunkers.task;

import org.bukkit.inventory.*;
import org.bukkit.*;
import org.bukkit.scheduler.*;

import com.prevailpots.bunkers.*;

import java.util.*;

import org.bukkit.plugin.*;

public class ItemDropTask implements DynamicTask
{
    private List<ItemStack> drops;
    private Location drop;
    
    public ItemDropTask(final List<ItemStack> dropd, final Location drop) {
        this.drops = new ArrayList<ItemStack>();
        this.drops = dropd;
        this.drop = drop;
    }
    
    @Override
    public void execute() {
        new BukkitRunnable() {
            public void run() {
                for (final ItemStack i : ItemDropTask.this.drops) {
                    ItemDropTask.this.drop.getWorld().dropItemNaturally(ItemDropTask.this.drop, i);
                }
            }
        }.runTaskLater((Plugin)Core.getInstance(), 2L);
    }
}

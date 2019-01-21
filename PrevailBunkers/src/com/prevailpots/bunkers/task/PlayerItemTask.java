package com.prevailpots.bunkers.task;

import org.bukkit.entity.*;

import com.prevailpots.bunkers.*;

public class PlayerItemTask implements DynamicTask
{
    private Player player;
    
    public PlayerItemTask(final Player p) {
        this.player = p;
    }
    
    @Override
    public void execute() {
        Core.getInstance().getItemManager().giveStarterItems(this.player);
    }
}

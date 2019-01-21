package com.prevailpots.kitmap.fixes;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.prevailpots.kitmap.HCF;

/**
 * Created by TREHOME on 10/30/2015.
 */
public class HungerFixListener implements Listener{


    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if(HCF.getPlugin().getFactionManager().getFactionAt(e.getPlayer().getLocation()).isSafezone()) {
            if(e.getPlayer().getFoodLevel() < 20) {
                e.getPlayer().setFoodLevel(20);
                e.getPlayer().setSaturation(20);
            }
        }
    }


    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(HCF.getPlugin().getFactionManager().getFactionAt(p.getLocation()).isSafezone()){
                p.setSaturation(20);
                p.setHealth(20);
            }
            p.setSaturation(10);
        }
    }
}

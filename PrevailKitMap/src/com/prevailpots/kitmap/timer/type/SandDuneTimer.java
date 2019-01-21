package com.prevailpots.kitmap.timer.type;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.prevailpots.kitmap.timer.GlobalTimer;
import com.prevailpots.kitmap.timer.event.TimerExpireEvent;

import java.util.concurrent.TimeUnit;

/**
 * Created by TREHOME on 04/10/2016.
 */
public class SandDuneTimer extends GlobalTimer implements Listener {
    public SandDuneTimer() {
        super("SandDune", TimeUnit.SECONDS.toMillis(5));
    }


    @EventHandler
    public void onExpire(TimerExpireEvent e) {
        if (e.getTimer().getDisplayName().equals(this.getDisplayName())){
            return;
        }
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.GOLD + "Sand Dune";
    }
}

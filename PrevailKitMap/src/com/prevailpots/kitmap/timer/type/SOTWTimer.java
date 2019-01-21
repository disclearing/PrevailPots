package com.prevailpots.kitmap.timer.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.timer.GlobalTimer;

import java.util.concurrent.TimeUnit;

/**
 * Created by HelpMe on 12/11/2015.
 */
public class SOTWTimer extends GlobalTimer implements Listener {

    public SOTWTimer() {
        super("SOTW", TimeUnit.HOURS.toMillis(2));
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.DARK_GREEN.toString() + ChatColor.BOLD;

    }
    @Override
    public void onExpire(){
        super.onExpire();
        if(clearCooldown()) {
            Bukkit.broadcastMessage(this.getDisplayName() + ChatColor.RED + " ended!");
        }
    }


    public void run(){
    if(this.getRemaining() > TimeUnit.MINUTES.toMillis(14)) {
        if (this.getRemaining() % TimeUnit.MINUTES.toMillis(15) == 0) {
            Bukkit.broadcastMessage(this.getDisplayName() + ChatColor.RED +  " will end in " + HCF.getRemaining(getRemaining(), true));
        }
    }else if (this.getRemaining() > TimeUnit.MINUTES.toMillis(2)){
      if(getRemaining() % TimeUnit.MINUTES.toMillis(2) == 0){
          Bukkit.broadcastMessage(this.getDisplayName() + ChatColor.RED +  " will end in " + HCF.getRemaining(getRemaining(), true));
      }
    }else{
        if(getRemaining() % TimeUnit.SECONDS.toMillis(30) == 0){
            Bukkit.broadcastMessage(this.getDisplayName() + ChatColor.RED +  " will end in " + HCF.getRemaining(getRemaining(), true));
        }
    }
}

    //// TODO: 12/20/2016 Take and modify SOTW system from iHCF. Allows the timer to be adjusted.

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && this.getRemaining() > 0 && !this.isPaused()) {
            e.setCancelled(true);
        }
    }

}

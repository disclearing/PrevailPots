package com.prevailpots.kitmap.listener;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import com.prevailpots.kitmap.HCF;

/**
 * Created by TREHOME on 04/06/2016.
 */
public class MOTDListener implements Listener {

    @EventHandler
    public void onMotdChange(ServerListPingEvent e){
        if(HCF.getPlugin().getTimerManager().eventTimer.getRemaining() > 0){
            String[] motd = Bukkit.getMotd().split("\n");
            e.setMotd(motd[0] + '\n' + ChatColor.GREEN + "KOTH Active: " + HCF.getPlugin().getTimerManager().eventTimer.getName() + " " + HCF.getRemaining(HCF.getPlugin().getTimerManager().eventTimer.getRemaining(), true));
        }else{
            return;
        }
    }
}

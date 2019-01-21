package com.prevailpots.hcf.listener;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.prevailpots.hcf.HCF;

/**
 * Created by TREHOME on 01/28/2016.
 */
public class DonorOnlyListener implements Listener {
    private static final String DONOR_ONLY_PERMISSION;

    static{
        DONOR_ONLY_PERMISSION = "donoronly.bypass";
    }

    @EventHandler
    public void onJoinServerWhileNotDonor(PlayerLoginEvent e){
        if(HCF.getPlugin().getHcfHandler().isDonorOnly()) {
            if (!e.getPlayer().hasPermission(DONOR_ONLY_PERMISSION)) {
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "The server is currently in Donor-Only mode. \n\n " + ChatColor.YELLOW + HCF.getPlugin().getConfig().getString("DonateLink"));
                return;
            }
        }
    }
}

package com.prevailpots.hcf.fixes;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by TREHOME on 01/28/2016.
 */
public class PexCrashFix implements Listener {


    @EventHandler
    public void onCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        if ((event.getMessage().toLowerCase().startsWith("/pex") ||event.getMessage().toLowerCase().startsWith("/kill") || event.getMessage().toLowerCase().startsWith("/slay") ||event.getMessage().toLowerCase().startsWith("/bukkit:version")||event.getMessage().toLowerCase().startsWith("/bukkit:about")|| event.getMessage().toLowerCase().startsWith("/bukkit:kill") || event.getMessage().toLowerCase().startsWith("/bukkit:slay") || event.getMessage().toLowerCase().startsWith("/suicide")) && !event.getPlayer().hasPermission("*.**")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "No permission.");
        }
    }

}

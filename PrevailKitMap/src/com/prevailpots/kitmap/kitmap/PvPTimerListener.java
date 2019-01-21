package com.prevailpots.kitmap.kitmap;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.timer.type.PvpProtectionTimer;

public class PvPTimerListener implements Listener
{
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final long protectionTimer = HCF.getPlugin().getTimerManager().pvpProtectionTimer.getRemaining(player.getUniqueId());
        final PvpProtectionTimer protectionTimer2 = HCF.getPlugin().getTimerManager().pvpProtectionTimer;
        if (protectionTimer <= 0L) {
            return;
        }
        protectionTimer2.clearCooldown(player);
    }
    
    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        final long protectionTimer = HCF.getPlugin().getTimerManager().pvpProtectionTimer.getRemaining(player.getUniqueId());
        final PvpProtectionTimer protectionTimer2 = HCF.getPlugin().getTimerManager().pvpProtectionTimer;
        if (protectionTimer <= 0L) {
            return;
        }
        protectionTimer2.clearCooldown(player);
    }
    
    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final long protectionTimer = HCF.getPlugin().getTimerManager().pvpProtectionTimer.getRemaining(player.getUniqueId());
        final PvpProtectionTimer protectionTimer2 = HCF.getPlugin().getTimerManager().pvpProtectionTimer;
        if (protectionTimer <= 0L) {
            return;
        }
        protectionTimer2.clearCooldown(player);
    }
}

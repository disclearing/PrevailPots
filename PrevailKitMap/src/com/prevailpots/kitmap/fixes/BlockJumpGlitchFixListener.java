package com.prevailpots.kitmap.fixes;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

public class BlockJumpGlitchFixListener implements Listener {
    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onBlockBreak(final BlockPlaceEvent event) {
        if(event.isCancelled()) {
            final Player player = event.getPlayer();
            if(player.getGameMode() == GameMode.CREATIVE || player.getAllowFlight()) {
                return;
            }
            final Block block = event.getBlockPlaced();
            if(block.getType().isSolid() && !(block.getState() instanceof Sign)) {
                final int playerY = player.getLocation().getBlockY();
                final int blockY = block.getLocation().getBlockY();
                if(playerY > blockY) {
                    final Vector vector = player.getVelocity();
                    player.setVelocity(vector.setY(vector.getY() - 0.41999998688697815));
                }
            }
        }
    }
}

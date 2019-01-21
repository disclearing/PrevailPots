package com.prevailpots.hcf.fixes;

import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

/**
 * Created by TREHOME on 12/31/2015.
 */
public class PhaseListener implements Listener {


    private final long GRAVITY_BLOCK = TimeUnit.HOURS.toMillis(6);
    private final long UTILITY_BLOCK = TimeUnit.HOURS.toMillis(3);
//    @EventHandler
//    public void onMove(PlayerInteractEvent e){
//        if(e.getPlayer().getLocation().getBlock() != null){
//            if(e.getPlayer().getLocation().getBlock().getType() == Material.TRAP_DOOR){
//                    if(!HCF.getPlugin().getFactionManager().getFactionAt(e.getPlayer().getLocation()).equals(HCF.getPlugin().getFactionManager().getPlayerFaction(e.getPlayer().getUniqueId()))) {
//                        e.getPlayer().sendMessage(ChatColor.RED + "Glitch detected. Now reporting, and fixing.");
//                        e.getPlayer().teleport(e.getPlayer().getLocation().add(0, 1, 0));
//                    }
//        }
//        }
//    }
//
//    @EventHandler
//    public void onPlayerTimePlace(BlockPlaceEvent event){
//        if(!event.getPlayer().hasPermission("hcf.gravity.bypass") && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
//            Player player = (Player) event.getPlayer();
//            if(event.getBlockPlaced().getType() == Material.SAND || event.getBlockPlaced().getType() == Material.GRAVEL) {
//                if(BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(player.getUniqueId()) <= gravityBlock) {
//                    player.sendMessage(ChatColor.RED + "You must wait another " + DurationFormatUtils.formatDurationWords(gravityBlock - BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(player.getUniqueId()), true, true) + " before placing a gravity block.");
//                    event.setCancelled(true);
//                    return;
//                }
//            } else if(event.getBlockPlaced().getType() == Material.ANVIL) {
//                if(BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(player.getUniqueId()) <= utilityBlock) {
//                    player.sendMessage(ChatColor.RED + "You must wait another " + DurationFormatUtils.formatDurationWords(utilityBlock - BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(player.getUniqueId()), true, true) + " before placing an anvil.");
//                    event.setCancelled(true);
//                    return;
//                }
//            }
//        }
//
//    }
}

package com.prevailpots.kitmap.kothgame.glowstone;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.customhcf.base.event.TickEvent;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.prevailpots.kitmap.DateTimeFormats;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.kothgame.faction.GlowstoneFaction;

import org.bukkit.scheduler.BukkitRunnable;

public class GlowstoneListener implements Listener{
    protected static final ImmutableMap<World.Environment, String> ENVIRONMENT_MAPPINGS = Maps.immutableEnumMap(ImmutableMap.of(World.Environment.NETHER, "Nether", World.Environment.NORMAL, "Overworld", World.Environment.THE_END, "The End"));

    /**
     * Checking to see if glowstone MT should start.
     * If it should start, run start code.
     * @param e
     */
    @EventHandler
    public void onTick(TickEvent e){
        GlowstoneFaction faction = (GlowstoneFaction) HCF.getPlugin().getFactionManager().getFaction("Glowstone");
        if(faction.isActive()) return;
        if(faction.getTimeTillNextReset() - System.currentTimeMillis() <= 0){
            faction.start();
        }
    }

    /**
     * Handles when a player breaks glowstone in the correct area.
     * @param e
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBreakGlowstone(BlockBreakEvent e){
        GlowstoneFaction faction = (GlowstoneFaction) HCF.getPlugin().getFactionManager().getFaction("Glowstone");
     // Make sure event has started
        if(!faction.isActive()) return;
     // Make sure the block broken is in the correct area
        if(HCF.getPlugin().getFactionManager().getFactionAt(e.getBlock()) == null || !HCF.getPlugin().getFactionManager().getFactionAt(e.getBlock()).equals(faction))  return;
    // Make sure its actually glowstone So we can do cool designs
        if(!e.getBlock().getType().equals(Material.GLOWSTONE)) return;
     // Make sure the glowstone is in the correct area so we can light the palace
        if(!faction.getGlowstoneArea().contains(e.getBlock())) return;

        // Used check to see if it should alert
        int there =0;
        int gone = 0;
        // wtf is this
        for(Block block : faction.getGlowstoneArea()){
            if(block.getType().equals(Material.GLOWSTONE)){
                there++;
            }else{
                gone++;
            }
        }
        if(e.getBlock().getType().equals(Material.GLOWSTONE)){
            there--;
            gone++;
        }

        // 75%
        if(there > gone) {
            int total = there + gone;
            int fifty = total / 2;
            int twenty = fifty / 2;
            if (gone == twenty && there == (fifty + twenty)) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "75%" + ChatColor.YELLOW + " of Glowstone Mountain has been mined.");
                //there is 75% glowstone remaining.
            }
        }

        // 50%
        if(there == gone){
            Bukkit.broadcastMessage(ChatColor.GOLD + "50%" + ChatColor.YELLOW + " of Glowstone Mountain has been mined.");
            //there is 50% glowstone remaining
        }

        // 25%
        if(gone > there) {
            int total = there + gone;
            int fifty = total / 2;
            int twenty = fifty / 2;
            if (there == twenty && gone == (fifty+twenty)) {
                //Bukkit.broadcastMessage(ChatColor.GOLD  + "Glowstone Mountain" + ChatColor.YELLOW + " there is " + ChatColor.GOLD + "25%" + ChatColor.YELLOW + " of remaining glowstone.");
                Bukkit.broadcastMessage(ChatColor.GOLD + "25%" + ChatColor.YELLOW + " of Glowstone Mountain has been mined.");
                // there is 25% glowstone remaining
            }
        }
        //Gone
        if(there == 0){
            Bukkit.broadcastMessage(ChatColor.GOLD  + "Glowstone Mountain" + ChatColor.YELLOW + " has been mined. It will will regenerate at " + ChatColor.GOLD + DateTimeFormats.HR_MIN.format(faction.getTimeTillNextReset()) + " EST.");

            //has been fully looted. The area will regenerate at time.
            faction.setTimeTillNextReset(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15));
            Bukkit.getScheduler().runTaskLater(HCF.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    faction.setActive(false);
                }
            }, 5L);
        }
        e.setCancelled(false);
    }
}

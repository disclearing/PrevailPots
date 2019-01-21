package com.prevailpots.kitmap.kothgame.faction;

import com.customhcf.util.BukkitUtils;
import com.customhcf.util.cuboid.Cuboid;
import com.prevailpots.kitmap.DateTimeFormats;
import com.prevailpots.kitmap.faction.claim.Claim;
import com.prevailpots.kitmap.faction.type.ClaimableFaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GlowstoneFaction extends ClaimableFaction implements ConfigurationSerializable {

    private Long defaultMillisTillReset;
    private Long lastReset;
    private Long timeTillNextReset;
    private Cuboid glowstoneArea;
    boolean active;


    public GlowstoneFaction() {
        super("Glowstone");
        defaultMillisTillReset = TimeUnit.MINUTES.toMillis(15);
        lastReset = (long) 0;
        timeTillNextReset = System.currentTimeMillis() + defaultMillisTillReset;
        active = false;
        this.glowstoneArea = null;
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> map = super.serialize();
        defaultMillisTillReset = TimeUnit.MINUTES.toMillis(15);
        lastReset = (long) 0;
        timeTillNextReset = System.currentTimeMillis() + defaultMillisTillReset;
        map.put("glowstoneArea", glowstoneArea);
        return map;
    }

    public GlowstoneFaction(final Map<String, Object> map) {
        super(map);
        defaultMillisTillReset = TimeUnit.MINUTES.toMillis(15);
        lastReset = (long) 0;
        timeTillNextReset = System.currentTimeMillis() + defaultMillisTillReset;
        this.setDeathban(true);
        this.glowstoneArea = (Cuboid) map.get("glowstoneArea");
    }


    public void start() {
        // Resets time
        lastReset = System.currentTimeMillis();
        timeTillNextReset = System.currentTimeMillis() + defaultMillisTillReset;
        // Sets event active
        this.active = true;
        //debug
        // Make sure we got a cube to look &
        if(glowstoneArea == null) return;

        // Spawns Glowstone in cubiod
        for (Block block : glowstoneArea){
            block.setType(Material.GLOWSTONE);
        }
        Bukkit.broadcastMessage(ChatColor.GOLD  + "Glowstone Mountain" + ChatColor.YELLOW + " has been " + ChatColor.GREEN + "regenerated" + ChatColor.YELLOW + '.');
    }
    /**TODO: Spawn glowstone
     *
     */




    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getDefaultMillisTillReset() {
        return defaultMillisTillReset;
    }

    public Long getLastReset() {
        return lastReset;
    }

    public void setLastReset(Long lastReset) {
        this.lastReset = lastReset;
    }

    public Long getTimeTillNextReset() {
        return timeTillNextReset;
    }

    public void setTimeTillNextReset(Long timeTillNextReset) {
        this.timeTillNextReset = timeTillNextReset;
    }

    public Cuboid getGlowstoneArea() {
        return glowstoneArea;
    }

    public void setGlowstoneArea(Cuboid glowstoneArea) {
        this.glowstoneArea = glowstoneArea;
    }


}

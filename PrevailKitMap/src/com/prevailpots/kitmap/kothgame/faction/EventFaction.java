package com.prevailpots.kitmap.kothgame.faction;

import com.customhcf.util.cuboid.Cuboid;
import com.prevailpots.kitmap.faction.claim.Claim;
import com.prevailpots.kitmap.faction.type.ClaimableFaction;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.kothgame.CaptureZone;
import com.prevailpots.kitmap.kothgame.EventType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public abstract class  EventFaction extends ClaimableFaction {
    public EventFaction(final String name) {
        super(name);
        this.setDeathban(true);
    }

    public EventFaction(final Map<String, Object> map) {
        super(map);
        this.setDeathban(true);
    }

    @Override
    public String getDisplayName(final Faction faction) {
        if(this.getEventType() == EventType.KOTH){
            if(getName().equalsIgnoreCase("Palace")){
                return ChatColor.GOLD + "Palace";
            }
            return ChatColor.GOLD + this.getName() + ' ' + this.getEventType().getDisplayName();
        }
        return ChatColor.DARK_RED + this.getEventType().getDisplayName();
    }

    @Override
    public String getDisplayName(final CommandSender sender) {
        if(this.getEventType() == EventType.KOTH){
            if(getName().equalsIgnoreCase("Palace")){
                return ChatColor.GOLD + "Palace";
            }
            return ChatColor.GOLD + this.getName() + ' ' + this.getEventType().getDisplayName();
        }
        return ChatColor.DARK_RED + this.getEventType().getDisplayName();
    }

    @Override
    public void setClaim(final Cuboid cuboid, final CommandSender sender) {
        this.removeClaims(this.getClaims(), sender);
        final Location min = cuboid.getMinimumPoint();
        min.setY(0);
        final Location max = cuboid.getMaximumPoint();
        max.setY(256);
        this.addClaim(new Claim(this, min, max), sender);
    }

    public abstract EventType getEventType();

    public abstract List<CaptureZone> getCaptureZones();
}

package com.prevailpots.hcf.faction.type;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.prevailpots.hcf.faction.claim.Claim;

import java.util.Map;

public class SpawnFaction extends ClaimableFaction implements ConfigurationSerializable {
    public SpawnFaction() {
        super("Spawn");
        this.safezone = true;
        for(final World world : Bukkit.getWorlds()) {
            final World.Environment environment = world.getEnvironment();
            if(environment != World.Environment.THE_END) {
                this.addClaim(new Claim(this, new Location(world, (double) 121, 0.0, (double) 121), new Location(world, (double) (-121), (double) world.getMaxHeight(), (double) (-121))), null);
            }
        }
    }

    public SpawnFaction(final Map<String, Object> map) {
        super(map);
    }

    public boolean isDeathban() {
        return false;
    }
}


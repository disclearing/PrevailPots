package com.prevailpots.hcf.deathban;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.customhcf.util.PersistableLocation;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.type.Faction;

public class FlatFileDeathbanManager implements DeathbanManager {
    private final HCF plugin;
    private static final String DEATH_BAN_BYPASS_PERMISSION = "deathban.bypass";

    public FlatFileDeathbanManager(final HCF plugin) {
        this.plugin = plugin;
    }


    @Override
    public long getDeathBanMultiplier(final Player player) {
        for (int i = 5; i < TimeUnit.MILLISECONDS.toSeconds(DeathbanManager.MAX_DEATHBAN_TIME); i++) {
            if (player.hasPermission("deathban.seconds." + i)) {
                return i * 1000;
            }
        }
        return plugin.getHcfHandler().getDefaultDeathban();
    }

    private final String BUILDER = "deathban.builder";
    private final String BRONZE = "deathban.bronze";
    private final String IRON = "deathban.iron";
    private final String PLATINUM = "deathban.platinum";
    private final String POISON = "deathban.poison";
    private final String GOLD = "deathban.gold";
    private final String GUARDIAN  = "deathban.guardian";
    private final String PREVAIL = "deathban.prevail";
    private final String YOUTUBE = "deathban.youtube";
    private final String FAMOUS = "deathban.famous";

    @Override
    public Deathban applyDeathBan(final Player player, final String reason) {
        final Location location = player.getLocation();
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        long duration = getDeathBanMultiplier(player);
        if (factionAt.isSafezone()) {
            duration = 0;
        }
        if (player.hasPermission(DEATH_BAN_BYPASS_PERMISSION)) {
            return null;
        }

        if(player.hasPermission(BUILDER)) {
            duration = TimeUnit.HOURS.toMillis(1);
            return applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
        }

        if(player.hasPermission(BRONZE)) {
            duration = TimeUnit.HOURS.toMillis(2);
            return applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
        }

        if(player.hasPermission(IRON)) {
            duration = TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(30);
            return applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
        }

        if(player.hasPermission(PLATINUM)) {
            duration = TimeUnit.HOURS.toMillis(1);
            return applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
        }

        if(player.hasPermission(POISON)) {
            duration = TimeUnit.MINUTES.toMillis(30);
            return applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
        }

        if(player.hasPermission(GOLD)) {
            duration = TimeUnit.MINUTES.toMillis(20);
            return applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
        }

        if(player.hasPermission(GUARDIAN)) {
            duration = TimeUnit.MINUTES.toMillis(10);
            return applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
        }

        if(player.hasPermission(PREVAIL)) {
            duration = TimeUnit.MINUTES.toMillis(5);
            return applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
        }

        if(player.hasPermission(YOUTUBE)) {
            duration = TimeUnit.HOURS.toMillis(1);
            return applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
        }

        if(player.hasPermission(FAMOUS)) {
            duration = TimeUnit.MINUTES.toMillis(30);
            return applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
        }

        return applyDeathBan(player.getUniqueId(), new Deathban(reason, TimeUnit.HOURS.toMillis(2), new PersistableLocation(location)));
    }

    @Override
    public Deathban applyDeathBan(final UUID uuid, final Deathban deathban) {
        this.plugin.getUserManager().getUser(uuid).setDeathban(deathban);
        return deathban;
    }




}

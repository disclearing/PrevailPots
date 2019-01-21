package com.prevailpots.hcf.deathban;

import org.bukkit.entity.Player;

import com.prevailpots.hcf.lives.LivesType;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface DeathbanManager {
    long MAX_DEATHBAN_TIME = TimeUnit.HOURS.toMillis(8L);


    long getDeathBanMultiplier(Player p0);

    Deathban applyDeathBan(Player p0, String p1);

    Deathban applyDeathBan(UUID p0, Deathban p1);

}

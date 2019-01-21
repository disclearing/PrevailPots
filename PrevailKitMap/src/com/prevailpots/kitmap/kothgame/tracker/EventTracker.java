package com.prevailpots.kitmap.kothgame.tracker;

import org.bukkit.entity.Player;

import com.prevailpots.kitmap.kothgame.CaptureZone;
import com.prevailpots.kitmap.kothgame.EventTimer;
import com.prevailpots.kitmap.kothgame.EventType;
import com.prevailpots.kitmap.kothgame.faction.EventFaction;

@Deprecated
public interface EventTracker {
    EventType getEventType();

    void tick(EventTimer p0, EventFaction p1);

    void onContest(EventFaction p0, EventTimer p1);

    boolean onControlTake(Player p0, CaptureZone p1);

    boolean onControlLoss(Player p0, CaptureZone p1, EventFaction p2);

    void stopTiming();
}

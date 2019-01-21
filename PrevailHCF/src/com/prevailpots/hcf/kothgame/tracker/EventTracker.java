package com.prevailpots.hcf.kothgame.tracker;

import org.bukkit.entity.Player;

import com.prevailpots.hcf.kothgame.CaptureZone;
import com.prevailpots.hcf.kothgame.EventTimer;
import com.prevailpots.hcf.kothgame.EventType;
import com.prevailpots.hcf.kothgame.faction.EventFaction;

@Deprecated
public interface EventTracker {
    EventType getEventType();

    void tick(EventTimer p0, EventFaction p1);

    void onContest(EventFaction p0, EventTimer p1);

    boolean onControlTake(Player p0, CaptureZone p1);

    boolean onControlLoss(Player p0, CaptureZone p1, EventFaction p2);

    void stopTiming();
}

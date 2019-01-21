package com.prevailpots.kitmap.kothgame;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.kothgame.tracker.ConquestTracker;
import com.prevailpots.kitmap.kothgame.tracker.EventTracker;
import com.prevailpots.kitmap.kothgame.tracker.KothTracker;

public enum EventType {
    CONQUEST("Conquest",  new ConquestTracker(HCF.getPlugin())),
    KOTH("KOTH",  new KothTracker(HCF.getPlugin())), ;


    private static final ImmutableMap<String, EventType> byDisplayName;

    static {
        final ImmutableMap.Builder<String, EventType> builder = (ImmutableMap.Builder<String, EventType>) new ImmutableBiMap.Builder();
        for(final EventType eventType : values()) {
            builder.put(eventType.displayName.toLowerCase(), eventType);
        }
        byDisplayName = builder.build();
    }

    private final EventTracker eventTracker;
    private final String displayName;

    private EventType(final String displayName, final EventTracker eventTracker) {
        this.displayName = displayName;
        this.eventTracker = eventTracker;
    }

    @Deprecated
    public static EventType getByDisplayName(final String name) {
        return (EventType) EventType.byDisplayName.get((Object) name.toLowerCase());
    }

    public EventTracker getEventTracker() {
        return this.eventTracker;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}

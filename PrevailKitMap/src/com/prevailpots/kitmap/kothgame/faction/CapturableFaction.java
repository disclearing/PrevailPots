package com.prevailpots.kitmap.kothgame.faction;

import java.util.Map;

public abstract class CapturableFaction extends EventFaction {
    public CapturableFaction(final String name) {
        super(name);
    }

    public CapturableFaction(final Map<String, Object> map) {
        super(map);
    }
}

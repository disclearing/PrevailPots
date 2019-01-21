package com.prevailpots.kitmap.timer;


import com.customhcf.util.Config;

public abstract class Timer {
    protected final String name;
    protected final long defaultCooldown;

    public Timer(final String name, final long defaultCooldown) {
        this.name = name;
        this.defaultCooldown = defaultCooldown;
    }

    public abstract String getScoreboardPrefix();

    public String getName() {
        return this.name;
    }

    public final String getDisplayName() {
        return this.getScoreboardPrefix() + this.name;
    }

    public void load(final Config config) {
    }

    public void onDisable(final Config config) {
    }
}

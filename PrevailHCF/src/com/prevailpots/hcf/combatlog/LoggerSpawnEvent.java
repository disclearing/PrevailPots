package com.prevailpots.hcf.combatlog;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoggerSpawnEvent extends Event {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private final LoggerEntity loggerEntity;

    public LoggerSpawnEvent(final LoggerEntity loggerEntity) {
        this.loggerEntity = loggerEntity;
    }

    public static HandlerList getHandlerList() {
        return LoggerSpawnEvent.handlers;
    }

    public LoggerEntity getLoggerEntity() {
        return this.loggerEntity;
    }

    public HandlerList getHandlers() {
        return LoggerSpawnEvent.handlers;
    }
}

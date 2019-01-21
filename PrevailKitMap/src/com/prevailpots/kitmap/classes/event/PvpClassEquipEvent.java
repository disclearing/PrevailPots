package com.prevailpots.kitmap.classes.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.prevailpots.kitmap.classes.PvpClass;

public class PvpClassEquipEvent extends PlayerEvent {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private final PvpClass pvpClass;

    public PvpClassEquipEvent(final Player player, final PvpClass pvpClass) {
        super(player);
        this.pvpClass = pvpClass;
    }

    public static HandlerList getHandlerList() {
        return PvpClassEquipEvent.handlers;
    }

    public PvpClass getPvpClass() {
        return this.pvpClass;
    }

    public HandlerList getHandlers() {
        return PvpClassEquipEvent.handlers;
    }
}

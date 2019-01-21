package com.prevailpots.hcf.timer.event;

import com.google.common.base.Optional;
import com.prevailpots.hcf.timer.PlayerTimer;
import com.prevailpots.hcf.timer.Timer;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;
import java.util.UUID;

public class TimerStartEvent extends Event {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private final Optional<Player> player;
    private final Optional<UUID> userUUID;
    private final Timer timer;
    private final long duration;

    public TimerStartEvent(final Timer timer, final long duration) {
        this.player = Optional.absent();
        this.userUUID = Optional.absent();
        this.timer = timer;
        this.duration = duration;
    }

    public TimerStartEvent(@Nullable final Player player, final UUID uniqueId, final PlayerTimer timer, final long duration) {
        this.player = Optional.fromNullable(player);
        this.userUUID = Optional.fromNullable(uniqueId);
        this.timer = timer;
        this.duration = duration;
    }

    public static HandlerList getHandlerList() {
        return TimerStartEvent.handlers;
    }

    public Optional<Player> getPlayer() {
        return this.player;
    }

    public Optional<UUID> getUserUUID() {
        return this.userUUID;
    }

    public Timer getTimer() {
        return this.timer;
    }

    public long getDuration() {
        return this.duration;
    }

    public HandlerList getHandlers() {
        return TimerStartEvent.handlers;
    }
}

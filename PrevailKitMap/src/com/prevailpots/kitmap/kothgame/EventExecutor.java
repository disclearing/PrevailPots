package com.prevailpots.kitmap.kothgame;

import com.customhcf.util.command.ArgumentExecutor;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.kothgame.argument.*;

public class EventExecutor extends ArgumentExecutor {
    public EventExecutor(final HCF plugin) {
        super("game");
        addArgument(new GameCancelArgument(plugin));
        addArgument(new GameCreateArgument(plugin));
        addArgument(new GameDeleteArgument(plugin));
        addArgument(new GameRenameArgument(plugin));
        addArgument(new GameSetAreaArgument(plugin));
        addArgument(new GameSetCapzoneArgument(plugin));
        addArgument(new GameStartArgument(plugin));
        addArgument(new GameScheduleArgument(plugin));
        addArgument(new GameUptimeArgument(plugin));
    }
}

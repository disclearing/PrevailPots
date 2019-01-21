package com.prevailpots.kitmap.timer;

import com.customhcf.util.command.ArgumentExecutor;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.timer.argument.TimerCheckArgument;
import com.prevailpots.kitmap.timer.argument.TimerClearArgument;
import com.prevailpots.kitmap.timer.argument.TimerSetArgument;
import com.prevailpots.kitmap.timer.argument.TimerStartArugment;

public class TimerExecutor extends ArgumentExecutor {
    public TimerExecutor(final HCF plugin) {
        super("timer");
        this.addArgument(new TimerCheckArgument(plugin));
        this.addArgument(new TimerClearArgument(plugin));
        this.addArgument(new TimerSetArgument(plugin));
        this.addArgument(new TimerStartArugment(plugin));
    }
}

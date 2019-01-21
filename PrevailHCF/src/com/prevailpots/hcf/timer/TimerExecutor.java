package com.prevailpots.hcf.timer;

import com.customhcf.util.command.ArgumentExecutor;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.timer.argument.TimerCheckArgument;
import com.prevailpots.hcf.timer.argument.TimerClearArgument;
import com.prevailpots.hcf.timer.argument.TimerSetArgument;
import com.prevailpots.hcf.timer.argument.TimerStartArugment;

public class TimerExecutor extends ArgumentExecutor {
    public TimerExecutor(final HCF plugin) {
        super("timer");
        this.addArgument(new TimerCheckArgument(plugin));
        this.addArgument(new TimerClearArgument(plugin));
        this.addArgument(new TimerSetArgument(plugin));
        this.addArgument(new TimerStartArugment(plugin));
    }
}

package com.prevailpots.kitmap.kothgame.conquest;

import com.customhcf.util.command.ArgumentExecutor;
import com.prevailpots.kitmap.HCF;

public class ConquestExecutor extends ArgumentExecutor {
    public ConquestExecutor(final HCF plugin) {
        super("conquest");
        this.addArgument(new ConquestSetpointsArgument(plugin));
    }
}

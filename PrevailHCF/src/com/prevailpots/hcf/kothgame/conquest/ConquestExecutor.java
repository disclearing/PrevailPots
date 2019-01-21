package com.prevailpots.hcf.kothgame.conquest;

import com.customhcf.util.command.ArgumentExecutor;
import com.prevailpots.hcf.HCF;

public class ConquestExecutor extends ArgumentExecutor {
    public ConquestExecutor(final HCF plugin) {
        super("conquest");
        this.addArgument(new ConquestSetpointsArgument(plugin));
    }
}

package com.prevailpots.hcf.kothgame.argument.glowstone;

import com.customhcf.util.command.ArgumentExecutor;
import com.prevailpots.hcf.HCF;

public class GlowstoneExecutor extends ArgumentExecutor {


    public GlowstoneExecutor(HCF plugin) {
        super("glowstone");
        addArgument(new GlowstoneSetTimeArgument(plugin));
        addArgument(new GlowstoneSetAreaArgument(plugin));
        addArgument(new GlowstoneStartArgument(plugin));
    }
}

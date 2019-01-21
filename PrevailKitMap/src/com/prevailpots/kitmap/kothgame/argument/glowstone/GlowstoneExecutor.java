package com.prevailpots.kitmap.kothgame.argument.glowstone;

import com.customhcf.util.command.ArgumentExecutor;
import com.prevailpots.kitmap.HCF;

public class GlowstoneExecutor extends ArgumentExecutor {


    public GlowstoneExecutor(HCF plugin) {
        super("glowstone");
        addArgument(new GlowstoneSetTimeArgument(plugin));
        addArgument(new GlowstoneSetAreaArgument(plugin));
        addArgument(new GlowstoneStartArgument(plugin));
    }
}

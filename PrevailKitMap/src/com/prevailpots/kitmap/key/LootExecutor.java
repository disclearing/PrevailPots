package com.prevailpots.kitmap.key;

import com.customhcf.util.command.ArgumentExecutor;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.key.argument.LootAddArgument;
import com.prevailpots.kitmap.key.argument.LootCreateArgument;
import com.prevailpots.kitmap.key.argument.LootGiveArgument;
import com.prevailpots.kitmap.key.argument.LootRollsArgument;

public class LootExecutor extends ArgumentExecutor {
    public LootExecutor(final HCF plugin) {
        super("key");
        addArgument(new LootCreateArgument(plugin));
        addArgument(new LootRollsArgument(plugin));
        addArgument(new LootAddArgument(plugin));
        this.addArgument(new LootGiveArgument(plugin));
    }
}

package com.prevailpots.hcf.key;

import com.customhcf.util.command.ArgumentExecutor;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.key.argument.LootAddArgument;
import com.prevailpots.hcf.key.argument.LootCreateArgument;
import com.prevailpots.hcf.key.argument.LootGiveArgument;
import com.prevailpots.hcf.key.argument.LootRollsArgument;

public class LootExecutor extends ArgumentExecutor {
    public LootExecutor(final HCF plugin) {
        super("key");
        addArgument(new LootCreateArgument(plugin));
        addArgument(new LootRollsArgument(plugin));
        addArgument(new LootAddArgument(plugin));
        this.addArgument(new LootGiveArgument(plugin));
    }
}

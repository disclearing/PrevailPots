package com.prevailpots.hcf.lives;


import com.customhcf.util.command.ArgumentExecutor;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.lives.argument.*;

public class LivesExecutor extends ArgumentExecutor {
    public LivesExecutor(final HCF plugin) {
        super("stafflives");
        this.addArgument(new LivesClearDeathbansArgument(plugin));
        this.addArgument( new LivesGiveArgument(plugin));
        this.addArgument( new LivesReviveArgument(plugin));
        this.addArgument( new LivesSetArgument(plugin));
        this.addArgument( new LivesCheckDeathban(plugin));
        this.addArgument( new LivesSetDeathbanTimeArgument());
    }
}

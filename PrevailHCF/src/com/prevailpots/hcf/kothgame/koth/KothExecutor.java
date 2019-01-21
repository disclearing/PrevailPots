package com.prevailpots.hcf.kothgame.koth;

import com.customhcf.util.command.ArgumentExecutor;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.kothgame.koth.argument.KothNextArgument;
import com.prevailpots.hcf.kothgame.koth.argument.KothScheduleArgument;
import com.prevailpots.hcf.kothgame.koth.argument.KothSetCapDelayArgument;
import com.prevailpots.hcf.kothgame.koth.argument.KothShowArgument;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KothExecutor extends ArgumentExecutor {
    private final KothScheduleArgument kothScheduleArgument;

    public KothExecutor(final HCF plugin) {
        super("koth");
        this.addArgument( new KothNextArgument(plugin));
        this.addArgument(new KothShowArgument());
        this.addArgument((this.kothScheduleArgument = new KothScheduleArgument(plugin)));
        this.addArgument(new KothSetCapDelayArgument(plugin));
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 1) {
            this.kothScheduleArgument.onCommand(sender, command, label, args);
            return true;
        }
        return super.onCommand(sender, command, label, args);
    }
}

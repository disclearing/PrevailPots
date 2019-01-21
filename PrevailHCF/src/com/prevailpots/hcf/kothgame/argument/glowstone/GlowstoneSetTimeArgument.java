package com.prevailpots.hcf.kothgame.argument.glowstone;

import com.customhcf.permissions.DateTimeFormats;
import com.customhcf.util.JavaUtils;
import com.customhcf.util.command.CommandArgument;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.type.Faction;
import com.prevailpots.hcf.kothgame.faction.GlowstoneFaction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class GlowstoneSetTimeArgument extends CommandArgument {

    HCF plugin;
    public GlowstoneSetTimeArgument(HCF plugin) {
        super("setregen", "Sets the time till it regens again", "command.glowstone.setregen");
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String s) {
        return "/"+s + " " + this.getName() + " <time>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final Faction faction = this.plugin.getFactionManager().getFaction("Glowstone");
        if(!(faction instanceof GlowstoneFaction)){
            sender.sendMessage(ChatColor.RED +"Error!");
            return true;
        }
        GlowstoneFaction glowstoneFaction = (GlowstoneFaction) faction;
        if(args.length != 2){
            sender.sendMessage(ChatColor.RED + getUsage(label));
            return true;
        }
        Long newTicks = JavaUtils.parse(args[1]);
        if(newTicks == -1L) {
            sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m1s");
            return true;
        }
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set glowstone palace reset time to " + ChatColor.GOLD + HCF.getRemaining(newTicks, true) + ChatColor.YELLOW + " and will reset at " + ChatColor.GOLD +DateTimeFormats.HR_MIN.format(System.currentTimeMillis() + newTicks));
        glowstoneFaction.setTimeTillNextReset(System.currentTimeMillis() + newTicks);
        return true;
    }
}

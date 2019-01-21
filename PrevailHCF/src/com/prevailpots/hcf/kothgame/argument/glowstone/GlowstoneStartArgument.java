package com.prevailpots.hcf.kothgame.argument.glowstone;

import com.customhcf.util.command.CommandArgument;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.type.Faction;
import com.prevailpots.hcf.kothgame.faction.GlowstoneFaction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class GlowstoneStartArgument extends CommandArgument {

    HCF plugin;
    public GlowstoneStartArgument(HCF plugin) {
        super("start", "Sets the time till it regens again", "command.glowstone.start");
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String s) {
        return "/"+s + " " + this.getName() + "";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final Faction faction = this.plugin.getFactionManager().getFaction("Glowstone");
        if(!(faction instanceof GlowstoneFaction)){
            sender.sendMessage(ChatColor.RED +"Error!");
            return true;
        }
        GlowstoneFaction glowstoneFaction = (GlowstoneFaction) faction;
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Started glowstone mountain");
        glowstoneFaction.start();
        return true;
    }
}

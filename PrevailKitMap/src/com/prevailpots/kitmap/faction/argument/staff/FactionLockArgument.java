package com.prevailpots.kitmap.faction.argument.staff;

import com.customhcf.util.command.CommandArgument;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.faction.type.PlayerFaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * Created by TREHOME on 01/20/2016.
 */
public class FactionLockArgument extends CommandArgument {
    private final HCF plugin;

    public FactionLockArgument(final HCF plugin) {
        super("lock", "Lock all factions");
        this.plugin = plugin;
        this.permission = "command.faction." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [on|off]";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
                if (args[1].equalsIgnoreCase("on")) {
                    Bukkit.broadcastMessage(ChatColor.RED + "All Factions are being locked!");
                    for (Faction faction : plugin.getFactionManager().getFactions()) {
                        if (faction instanceof PlayerFaction) {
                            faction.setLocked(true);
                        }
                    }
                }
                if (args[1].equalsIgnoreCase("off")) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "All Factions are being un-locked!");
                    for (Faction faction : plugin.getFactionManager().getFactions()) {
                        if (faction instanceof PlayerFaction) {
                            faction.setLocked(false);
                        }
                    }
                }


        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}

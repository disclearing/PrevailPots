package com.prevailpots.kitmap.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.prevailpots.kitmap.HCF;

/**
 * Created by TREHOME on 05/13/2016.
 */
public class ToggleDonorOnly implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        HCF.getPlugin().getHcfHandler().setDonorOnly(!HCF.getPlugin().getHcfHandler().isDonorOnly());
        Command.broadcastCommandMessage(sender, ChatColor.GREEN + "Server is " + (!HCF.getPlugin().getHcfHandler().isDonorOnly() ? (ChatColor.RED.toString() + ChatColor.BOLD + "not") : (ChatColor.GREEN.toString() + ChatColor.BOLD + "now")) + ChatColor.GREEN+ " in donor only mode.");
        return true;
    }
}
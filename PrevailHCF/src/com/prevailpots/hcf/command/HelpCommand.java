package com.prevailpots.hcf.command;

import com.customhcf.util.BukkitUtils;
import com.prevailpots.hcf.HCF;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class HelpCommand implements CommandExecutor  {

    private final ChatColor VALUE_COLOR = ChatColor.GRAY;
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        sender.sendMessage(VALUE_COLOR + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        final List<String> list = HCF.getPlugin().getConfig().getStringList("Help");
        for (final String playerlist : list) {
                final Player player = (Player)sender;
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', playerlist));
            }
        sender.sendMessage(VALUE_COLOR + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}

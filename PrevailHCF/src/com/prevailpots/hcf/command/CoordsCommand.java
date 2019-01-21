package com.prevailpots.hcf.command;

import com.customhcf.util.BukkitUtils;
import com.prevailpots.hcf.HCF;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CoordsCommand implements CommandExecutor, TabExecutor {

    private final ChatColor VALUE_COLOR = ChatColor.YELLOW;


    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        sender.sendMessage(VALUE_COLOR + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        final List<String> list = HCF.getPlugin().getConfig().getStringList("Coords");
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

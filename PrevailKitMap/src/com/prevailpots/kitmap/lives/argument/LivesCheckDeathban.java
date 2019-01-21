package com.prevailpots.kitmap.lives.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.customhcf.util.command.CommandArgument;
import com.google.common.base.Strings;
import com.prevailpots.kitmap.DateTimeFormats;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.deathban.Deathban;
import com.prevailpots.kitmap.user.FactionUser;

/**
 * Created by TREHOME on 03/07/2016.
 */
public class LivesCheckDeathban  extends CommandArgument {
    private final HCF plugin;

    public LivesCheckDeathban(final HCF plugin) {
        super("checkdeathban", "Check a players deathban.");
        this.plugin = plugin;
        isPlayerOnly  = true;
        this.permission = "command.stafflives." + this.getName();
    }
    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <playerName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]); //TODO: breaking

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.GRAY + args[1] + ChatColor.RED + "' not found.");
            return true;
        }

        Deathban deathban = plugin.getUserManager().getUser(target.getUniqueId()).getDeathban();

        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not death-banned.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Deathban of: " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + ".");
        sender.sendMessage(ChatColor.YELLOW + " Time: " +  ChatColor.GOLD + DateTimeFormats.HR_MIN.format(deathban.getCreationMillis()));
        sender.sendMessage(ChatColor.YELLOW + " Duration: " + ChatColor.GOLD + DurationFormatUtils.formatDurationWords(deathban.getCreationMillis(), true, true));

        Location location = deathban.getDeathPoint();
        if (location != null) {
            sender.sendMessage(ChatColor.YELLOW + " Location: "+ ChatColor.GOLD+"" + location.getBlockX() + "| " + location.getBlockY() + ", " + location.getBlockZ() + ") - " + location.getWorld().getName());
            sender.sendMessage(ChatColor.YELLOW + "  World: " + ChatColor.GOLD + location.getWorld());
            sender.sendMessage(ChatColor.YELLOW + "  X: " + ChatColor.GOLD + location.getBlockX());
            sender.sendMessage(ChatColor.YELLOW + "  Y: " + ChatColor.GOLD + location.getBlockY());
            sender.sendMessage(ChatColor.YELLOW + "  Z: " + ChatColor.GOLD + location.getBlockZ());
        }

        sender.sendMessage(ChatColor.YELLOW + " Reason: " + ChatColor.GOLD + Strings.nullToEmpty(deathban.getReason()) );
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        for (FactionUser factionUser : plugin.getUserManager().getUsers().values()) {
            Deathban deathban = factionUser.getDeathban();
            if (deathban != null && deathban.isActive()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
                String name = offlinePlayer.getName();
                if (name != null) {
                    results.add(name);
                }
            }
        }

        return results;
    }
}

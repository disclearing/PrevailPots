package com.prevailpots.hcf.faction.argument.staff;

import com.customhcf.util.command.CommandArgument;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.type.Faction;
import com.prevailpots.hcf.faction.type.PlayerFaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Interlagos on 12/19/2016.
 */
public class FactionTPArgument extends CommandArgument {

    private final HCF plugin;
    private final Map<UUID, Location> destinationMap = new HashMap<>();
    public FactionTPArgument(HCF plugin) {
        super("tp", "Teleports the sender to the selected faction's home point.");
        this.plugin = plugin;
        this.permission = "command.faction.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <factionName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }
        Player player = (Player) sender;

        Faction faction = plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null){
            sender.sendMessage(ChatColor.RED + "Player faction named or containing member with IGN or UUID "
                    + args[1] + " not found.");
            return true;
        }

        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "Player faction named or containing member with IGN or UUID "
                    + args[1] + " not found.");
            return true;
        }

        PlayerFaction playerFaction = (PlayerFaction) faction;
        Location location = playerFaction.getHome();
        ((Player) sender).teleport(location);
        sender.sendMessage(ChatColor.GREEN + "Teleported you to the home point of the faction: " + faction.getName() + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        } else if (args[1].isEmpty()) {
            return null;
        } else {
            Player player = (Player) sender;
            List<String> results = new ArrayList<>(plugin.getFactionManager().getFactionNameMap().keySet());
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (player.canSee(target) && !results.contains(target.getName())) {
                    results.add(target.getName());
                }
            }

            return results;
        }
    }
}



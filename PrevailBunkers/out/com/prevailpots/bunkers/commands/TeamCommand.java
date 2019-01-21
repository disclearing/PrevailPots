package com.prevailpots.bunkers.commands;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import ru.tehkode.permissions.bukkit.*;

import org.bukkit.*;
import org.apache.commons.lang3.*;
import org.bukkit.util.*;

import com.prevailpots.bunkers.*;
import com.prevailpots.bunkers.config.*;
import com.prevailpots.bunkers.game.*;
import com.prevailpots.bunkers.utils.*;
import com.sk89q.worldedit.bukkit.selections.*;

public class TeamCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c§lPLAYERS ONLY!");
            return true;
        }
        final Player player = (Player)sender;
        if (args.length == 0) {
            player.sendMessage("§cCorrect usage: " + command.getUsage());
            return true;
        }
        if (args[0].equalsIgnoreCase("setspawn")) {
            if (!PermissionsEx.getUser(player).has("uhcb.setteamspawn") && !player.isOp()) {
                player.sendMessage("§c§lNO PERMISSIONS...");
                return true;
            }
            if (args.length != 2) {
                player.sendMessage("§cCorrect usage: /team setspawn <team>");
                return true;
            }
            final String team = args[1];
            if (Team.fromString(team) == null) {
                player.sendMessage("§eInvalid team.");
                return true;
            }
            ConfigurationService.setTeamSpawn(player.getLocation(), Team.fromString(team));
            BroadcastUtils.broadcastToPerm("§7[" + player.getName() + ": §oSet the " + team + " team spawn to " + (int)player.getLocation().getX() + "x, " + (int)player.getLocation().getY() + "y, " + (int)player.getLocation().getZ() + "z§7]", "uhcb.setteamspawn");
        }
        else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("stats") || args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("who")) {
            if (args.length != 2) {
                player.sendMessage("§cCorrect usage: /team " + args[0] + " <team>");
                return true;
            }
            String team = args[1];
            if (Team.fromString(team) == null) {
                team = (Core.getInstance().getGameHandler().getPlayers().containsKey(team) ? StringUtils.capitalize(Core.getInstance().getGameHandler().getPlayers().get(Bukkit.getPlayer(team).getName()).toString().toLowerCase()) : "NULLBLOCK");
            }
            final Team tem = Team.fromString(team);
            if (tem == null) {
                player.sendMessage("§eInvalid team.");
                return true;
            }
            String toSend = "";
            toSend = String.valueOf(toSend) + "§7§m------------------------------------\n";
            toSend = String.valueOf(toSend) + "§7[" + tem.getColor().toString() + "Team " + StringUtils.capitalize(tem.toString().toLowerCase()) + "§7]\n";
            toSend = String.valueOf(toSend) + " \n";
            toSend = String.valueOf(toSend) + "§aTeam members" + tem.getColor().toString() + ":\n";
            String[] members;
            for (int length = (members = Core.getInstance().getTeamManager().getMembers(tem)).length, i = 0; i < length; ++i) {
                final String s = members[i];
                toSend = String.valueOf(toSend) + "§7[" + (Core.getInstance().getGameHandler().getPlayersDeadOrRespawning().contains(Bukkit.getPlayer(s).getName()) ? "§c" : "§a") + s + "§7]" + "\n";
            }
            toSend = String.valueOf(toSend) + " \n";
            toSend = String.valueOf(toSend) + "§aTeam info" + tem.getColor().toString() + ":\n";
            toSend = String.valueOf(toSend) + "§aTotal team balance" + tem.getColor().toString() + ": " + Core.getInstance().getTeamManager().getTeamBalanceFormatted(tem) + "\n";
            toSend = String.valueOf(toSend) + "§aTotal team kills" + tem.getColor().toString() + ": " + Core.getInstance().getTeamManager().getTotalKills(tem) + "\n";
            toSend = String.valueOf(toSend) + "§aTotal team deaths" + tem.getColor().toString() + ": " + Core.getInstance().getTeamManager().getTotalDeaths(tem) + "\n";
            toSend = String.valueOf(toSend) + "§aTotal team points" + tem.getColor().toString() + ": " + Core.getInstance().getTeamManager().getTeamPoints(tem) + "\n";
            toSend = String.valueOf(toSend) + "§aDTR" + tem.getColor().toString() + ": " + Core.getInstance().getDTRManager().getDTRFormatted(tem) + "\n";
            toSend = String.valueOf(toSend) + "§7§m------------------------------------\n";
            player.sendMessage(toSend);
        }
        else if (args[0].equalsIgnoreCase("setregion")) {
            if (args.length != 2) {
                player.sendMessage("§cCorrect usage: /team setregion <team>");
                return true;
            }
            final String team = args[1];
            if (!PermissionsEx.getUser(player).has("uhcb.setteamregion") && !player.isOp()) {
                player.sendMessage("§c§lNO PERMISSIONS...");
                return true;
            }
            if (Team.fromString(team) == null) {
                player.sendMessage("§cInvalid team.");
                return true;
            }
            final Selection s2 = Core.getInstance().getWorldEdit().getSelection(player);
            if (s2 == null) {
                player.sendMessage("§cNo region selected. Select one with WorldEdit.");
                return true;
            }
            final Vector point1 = new Vector(s2.getMinimumPoint().getX(), 0.0, s2.getMinimumPoint().getZ());
            final Vector point2 = new Vector(s2.getMaximumPoint().getX(), 255.0, s2.getMaximumPoint().getZ());
            ConfigurationService.setRegions(Team.fromString(team), new Vector[] { point1, point2 });
            BroadcastUtils.broadcastToPerm("§7[" + player.getName() + ": §oSet the " + team + " team region.§7]", "uhcb.setteamregion");
        }
        else if (args[0].equalsIgnoreCase("setcapzone")) {
            if (args.length != 2) {
                player.sendMessage("§cCorrect usage: /team setcapzone <team>");
                return true;
            }
            final String team = args[1];
            if (!PermissionsEx.getUser(player).has("uhcb.setteamcapzone") && !player.isOp()) {
                player.sendMessage("§c§lNO PERMISSIONS...");
                return true;
            }
            if (Team.fromString(team) == null) {
                player.sendMessage("§cInvalid team.");
                return true;
            }
            final Selection s2 = Core.getInstance().getWorldEdit().getSelection(player);
            if (s2 == null) {
                player.sendMessage("§cNo region selected. Select one with WorldEdit.");
                return true;
            }
            final Vector point1 = new Vector(s2.getMinimumPoint().getX(), s2.getMinimumPoint().getY(), s2.getMinimumPoint().getZ());
            final Vector point2 = new Vector(s2.getMaximumPoint().getX(), 255.0, s2.getMaximumPoint().getZ());
            ConfigurationService.setCapzone(Team.fromString(team), new Vector[] { point1, point2 });
            BroadcastUtils.broadcastToPerm("§7[" + player.getName() + ": §oSet the " + team + " team capzone.§7]", "uhcb.setteamcapzone");
        }
        else if (args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("c")) {
            if (args.length != 1) {
                player.sendMessage("§cCorrect usage: /team chat");
                return true;
            }
            if (Core.getInstance().getChatManager().isTeamChat(player)) {
                Core.getInstance().getChatManager().setTeamChat(player, false);
                player.sendMessage("§aYou are no longer talking in team chat.");
            }
            else {
                Core.getInstance().getChatManager().setTeamChat(player, true);
                player.sendMessage("§aYou are now talking in team chat.");
            }
        }
        return true;
    }
}

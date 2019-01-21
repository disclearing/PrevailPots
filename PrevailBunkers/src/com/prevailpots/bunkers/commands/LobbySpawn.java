package com.prevailpots.bunkers.commands;

import org.bukkit.command.*;
import org.bukkit.entity.*;

import com.prevailpots.bunkers.config.*;
import com.prevailpots.bunkers.utils.*;

import ru.tehkode.permissions.bukkit.*;

public class LobbySpawn implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c§lPLAYERS ONLY!");
            return true;
        }
        final Player player = (Player)sender;
        if (!PermissionsEx.getUser(player).has("uhcb.setlobbyspawn") && !player.isOp()) {
            player.sendMessage("§c§lNO PERMISSIONS...");
            return true;
        }
        if (args.length != 0) {
            player.sendMessage("§cCorrect usage: " + command.getUsage());
            return true;
        }
        ConfigurationService.setLobbySpawn(player.getLocation());
        BroadcastUtils.broadcastToPerm("§7[" + player.getName() + ": §oSet the lobby spawn to " + (int)player.getLocation().getX() + "x, " + (int)player.getLocation().getY() + "y, " + (int)player.getLocation().getZ() + "z§7]", "uhcb.setlobbyspawn");
        return true;
    }
}

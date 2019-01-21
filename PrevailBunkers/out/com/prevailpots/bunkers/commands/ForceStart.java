package com.prevailpots.bunkers.commands;

import org.bukkit.command.*;
import org.bukkit.entity.*;

import com.prevailpots.bunkers.*;
import com.prevailpots.bunkers.game.*;
import com.prevailpots.bunkers.utils.*;

import ru.tehkode.permissions.bukkit.*;

public class ForceStart implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c§lPLAYERS ONLY!");
            return true;
        }
        final Player player = (Player)sender;
        if (!PermissionsEx.getUser(player).has("uhcb.forcestart") && !player.isOp()) {
            player.sendMessage("§c§lNO PERMISSIONS...");
            return true;
        }
        if (args.length != 0) {
            player.sendMessage("§cCorrect usage: " + command.getUsage());
            return true;
        }
        if (!Core.getInstance().getGameHandler().getGameState().equals(GameState.LOBBY)) {
            player.sendMessage("§cThe game is already running!");
            return true;
        }
        Core.getInstance().getGameHandler().startGame();
        BroadcastUtils.broadcastToPerm("§7[" + player.getName() + ": §oForce started the game.§7]", "uhcb.forcestart");
        return true;
    }
}

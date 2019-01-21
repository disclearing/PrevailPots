package com.prevailpots.kitmap.command;

import com.customhcf.util.BukkitUtils;
import com.prevailpots.kitmap.HCF;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpawnCommand implements CommandExecutor, TabCompleter {
    private static final long KIT_MAP_TELEPORT_DELAY;

    static {
        KIT_MAP_TELEPORT_DELAY = TimeUnit.SECONDS.toMillis(10L);
    }

    final HCF plugin;

    public SpawnCommand(final HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        final Player player = (Player) sender;
        World world = player.getWorld();
        Location spawn = world.getSpawnLocation().clone().add(0, 1, 0);
        if(!sender.hasPermission(command.getPermission() + ".teleport")){
            plugin.getTimerManager().teleportTimer.teleport(player, Bukkit.getWorld("world").getSpawnLocation(), KIT_MAP_TELEPORT_DELAY, ChatColor.YELLOW + "Teleporting to spawn in "+ 15 + " seconds.", PlayerTeleportEvent.TeleportCause.COMMAND);
            return true;
        }
        if(args.length > 0) {
            world = Bukkit.getWorld(args[0]);
            if(world == null) {
                sender.sendMessage(ChatColor.RED + "There is not a world named " + args[0] + '.');
                return true;
            }
            spawn = world.getSpawnLocation().clone().add(0, 1, 0);
        }
        player.teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length != 1 || !sender.hasPermission(command.getPermission() + ".teleport")) {
            return Collections.emptyList();
        }
        return BukkitUtils.getCompletions(args, Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
    }
}

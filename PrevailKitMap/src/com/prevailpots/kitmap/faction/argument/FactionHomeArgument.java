package com.prevailpots.kitmap.faction.argument;

import com.customhcf.util.command.CommandArgument;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.FactionExecutor;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.faction.type.PlayerFaction;
import com.prevailpots.kitmap.kothgame.faction.EventFaction;
import com.prevailpots.kitmap.timer.PlayerTimer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class FactionHomeArgument extends CommandArgument {
    private final FactionExecutor factionExecutor;
    private final HCF plugin;

    public FactionHomeArgument(final FactionExecutor factionExecutor, final HCF plugin) {
        super("home", "Teleport to the faction's home point.");
        this.factionExecutor = factionExecutor;
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player) sender;
        if(args.length >= 2 && args[1].equalsIgnoreCase("set")) {
            this.factionExecutor.getArgument("sethome").onCommand(sender, command, label, args);
            return true;
        }
        final UUID uuid = player.getUniqueId();
        PlayerTimer timer = this.plugin.getTimerManager().enderPearlTimer;
        long remaining = timer.getRemaining(player);
        if(remaining > 0L) {
            sender.sendMessage(ChatColor.RED + "You cannot warp whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
            return true;
        }
        if((remaining = (timer = this.plugin.getTimerManager().spawnTagTimer).getRemaining(player)) > 0L) {
            sender.sendMessage(ChatColor.RED + "You cannot warp whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
            return true;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
        if(playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        final Location home = playerFaction.getHome();
        if(home == null) {
            sender.sendMessage(ChatColor.RED + "Your faction does not have a home set.");
            return true;
        }
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
        if(factionAt instanceof EventFaction) {
            sender.sendMessage(ChatColor.RED + "You cannot warp whilst in event zones.");
            return true;
        }
        long millis;
        if(factionAt.isSafezone()) {
            if(player.getWorld().getEnvironment() != World.Environment.THE_END) {
                plugin.getTimerManager().pvpProtectionTimer.clearCooldown(player.getUniqueId());
                millis = 0L;
            } else {
                sender.sendMessage(ChatColor.RED + "You cannot teleport to your faction home whilst in The End.");
                return true;
            }
        } else {
            switch(player.getWorld().getEnvironment()) {
                case THE_END: {
                    millis = 30000L;
                    break;
                }
                case NETHER: {
                    millis = 30000L;
                    break;
                }
                default: {
                    millis = 10000L;
                    break;
                }
            }
        }
        if(!factionAt.equals(playerFaction) && factionAt instanceof PlayerFaction) {
            if(!((PlayerFaction) factionAt).isRaidable()) {
                player.sendMessage(ChatColor.RED + "You are in a claim, if your stuck use /f stuck");
                return true;
            } else {
                millis *= 2l;
            }
        }
        if(plugin.getTimerManager().pvpProtectionTimer.getRemaining(player.getUniqueId()) > 0) {
            player.sendMessage(ChatColor.RED + "You still have PvP Protection, you must enable it first.");
            return true;
        }
        if(plugin.getTimerManager().teleportTimer.getNearbyEnemies(player, 60) != 0) {
            this.plugin.getTimerManager().teleportTimer.teleport(player, home, millis, ChatColor.YELLOW + "Teleporting to your faction home in " + ChatColor.LIGHT_PURPLE + HCF.getRemaining(millis, true, false) + ChatColor.YELLOW + ". Do not move or take damage.", PlayerTeleportEvent.TeleportCause.COMMAND);
            return true;
        }else{
            this.plugin.getTimerManager().teleportTimer.teleport(player, home, 1, ChatColor.YELLOW + "Teleported to your faction home "+ChatColor.GOLD +"instantly "+ChatColor.YELLOW +"since there is no enemies nearby.", PlayerTeleportEvent.TeleportCause.COMMAND);
            return true;
        }
    }
}

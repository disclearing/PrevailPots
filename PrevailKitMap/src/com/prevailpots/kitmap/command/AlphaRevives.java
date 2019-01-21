package com.prevailpots.kitmap.command;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.prevailpots.kitmap.Cooldowns;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.deathban.Deathban;
import com.prevailpots.kitmap.faction.struct.Relation;
import com.prevailpots.kitmap.faction.type.PlayerFaction;
import com.prevailpots.kitmap.user.FactionUser;

public class AlphaRevives implements CommandExecutor {

	private final HCF plugin;

	public AlphaRevives(final HCF plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		if (player == null && (sender instanceof Player))
			return true;
		if (args.length == 2 && (args[0].equalsIgnoreCase("revive"))) {
			if (!player.hasPermission("alpha.revive")) {
				player.sendMessage(ChatColor.RED + "Not really.");
				return true;
			}
			if (Cooldowns.isOnCooldown("alpha_revive_command_cooldown", player)) {
				player.sendMessage(ChatColor.RED + "You are still on cooldown for another " + ChatColor.BOLD.toString() + HCF.getRemaining(Cooldowns.getCooldownForPlayerLong("alpha_revive_command_cooldown", player), true) + ChatColor.RED + '.');
				return true;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
			if (!target.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.GRAY + args[1] + ChatColor.RED + "' not found.");
				return true;
			}
			final UUID targetUUID = target.getUniqueId();
			final FactionUser factionTarget = this.plugin.getUserManager().getUser(targetUUID);
			final Deathban deathban = factionTarget.getDeathban();
			if (deathban == null || !deathban.isActive()) {
				sender.sendMessage(ChatColor.RED + target.getName() + " is not death-banned.");
				return true;
			}
			Relation relation;
			final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
			relation = ((playerFaction == null) ? Relation.ENEMY : playerFaction.getFactionRelation(this.plugin.getFactionManager().getPlayerFaction(targetUUID)));
			factionTarget.setDeathban(null);
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("AlphaBroadcast")).replace("{reviver}", player.getName()).replace("{revived}", target.getName()));
			sender.sendMessage(ChatColor.YELLOW + "You have revived " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
			Cooldowns.addCooldown("alpha_revive_command_cooldown", player, (int) TimeUnit.HOURS.toSeconds(3));
		}
		return false;
	}

}

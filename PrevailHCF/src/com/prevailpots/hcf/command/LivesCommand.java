package com.prevailpots.hcf.command;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.prevailpots.hcf.Cooldowns;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.deathban.Deathban;
import com.prevailpots.hcf.faction.struct.Relation;
import com.prevailpots.hcf.faction.type.PlayerFaction;
import com.prevailpots.hcf.user.FactionUser;

public class LivesCommand implements CommandExecutor {
	private static final ImmutableList<String> COMPLETIONS;

	static {
		COMPLETIONS = ImmutableList.of("enable", "time");
	}

	private final HCF plugin;

	public LivesCommand(final HCF plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		if (player == null && (sender instanceof Player))
			return true;
		if (args.length == 3 && (args[0].equalsIgnoreCase("givelives") || args[0].equalsIgnoreCase("give"))) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
			if (!target.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.GRAY + args[1] + ChatColor.RED + "' not found.");
				return true;
			}
			Integer amount = Ints.tryParse(args[2]);
			if (amount == null) {
				printUsage(sender, label);
				return true;
			}
			if (amount <= 0) {
				printUsage(sender, label);
				return true;
			}
			int num;
			if (sender instanceof Player) {
				num = plugin.getUserManager().getUser(((Player) sender).getUniqueId()).getLives();
			} else {
				num = 99999;
			}
			if (amount > num) {
				sender.sendMessage(ChatColor.RED + "You tried to send " + ChatColor.BOLD + amount + ChatColor.RED + " lives when you only have " + ChatColor.BOLD + plugin.getUserManager().getUser(((Player) sender).getUniqueId()).getLives());
				return true;
			}
			if (sender instanceof Player) {
				plugin.getUserManager().getUser(((Player) sender).getUniqueId()).takeLives(amount);
			}

			plugin.getUserManager().getUser(target.getUniqueId()).addLives(amount);
			final int targetLives = plugin.getUserManager().getUser(target.getUniqueId()).getLives();
			int senderLives = 0;
			if (sender instanceof Player) {
				senderLives = plugin.getUserManager().getUser(((Player) sender).getUniqueId()).getLives();
			}
			if (target.isOnline() && Bukkit.getPlayer(target.getUniqueId()) != null) {
				Bukkit.getPlayer(target.getUniqueId()).sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " has sent you " + ChatColor.RED + amount + ChatColor.YELLOW + " lives!");
				Bukkit.getPlayer(target.getUniqueId()).sendMessage(ChatColor.YELLOW + "You now have " + ChatColor.RED + targetLives + ChatColor.YELLOW + " lives.");
			}
			sender.sendMessage(ChatColor.YELLOW + "You sent " + ChatColor.GOLD + target.getName() + ChatColor.RED + ' ' + amount + ChatColor.YELLOW + " lives!");
			sender.sendMessage(ChatColor.YELLOW + "You now have " + ChatColor.RED + senderLives + ChatColor.YELLOW + " lives.");
			return true;
		}
		if (args.length == 2 && args[0].equalsIgnoreCase("revive")) {
			if (!player.hasPermission("alpha.revive")) {
				player.sendMessage(ChatColor.RED + "Not really.");
				return true;
			}
			if (Cooldowns.isOnCooldown("diamond_revive_cooldown", player)) {
				player.sendMessage(ChatColor.RED + "You are still on cooldown for another " + ChatColor.BOLD.toString() + HCF.getRemaining(Cooldowns.getCooldownForPlayerLong("diamond_revive_cooldown", player), true) + ChatColor.RED + '.');
				return true;
			}
			final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
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
			final int selfLives;
			final UUID playerUUID = player.getUniqueId();
			selfLives = plugin.getUserManager().getUser(playerUUID).getLives();
			if (selfLives <= 0) {
				sender.sendMessage(ChatColor.RED + "You do not have any lives.");
				return true;
			}
			plugin.getUserManager().getUser(playerUUID).takeLives(1);
			final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
			relation = ((playerFaction == null) ? Relation.ENEMY : playerFaction.getFactionRelation(this.plugin.getFactionManager().getPlayerFaction(targetUUID)));
			factionTarget.setDeathban(null);
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("AlphaBroadcast")).replace("{reviver}", player.getName()).replace("{revived}", target.getName()));
			sender.sendMessage(ChatColor.YELLOW + "You have revived " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
			Cooldowns.addCooldown("diamond_revive_cooldown", player, (int) TimeUnit.HOURS.toSeconds(3));
			return true;
		}
		if (args.length == 0) {
			this.printUsage(sender, label);
			return true;
		}
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
		if (!target.hasPlayedBefore() && !target.isOnline()) {
			sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.GRAY + args[0] + ChatColor.RED + "' not found.");
			return true;
		}
		final int targetLives = this.plugin.getUserManager().getUser(target.getUniqueId()).getLives();
		final int targetSoulLives = this.plugin.getUserManager().getUser(target.getUniqueId()).getSouLives();
		sender.sendMessage(ChatColor.BLUE + target.getName() + ChatColor.YELLOW + " lives.");
		sender.sendMessage(ChatColor.YELLOW + "Soul-Bound Lives: " + ChatColor.RED + targetSoulLives);
		sender.sendMessage(ChatColor.YELLOW + "Lives: " + ChatColor.RED + targetLives);
		return true;
	}

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
		return (args.length == 1) ? null : Collections.emptyList();
	}

	private void printUsage(final CommandSender sender, final String label) {
		if (label.equalsIgnoreCase("alpha")) {
			sender.sendMessage(ChatColor.RED + "/" + label + " revive <player> - Revives the targeted player.");
			return;
		}
		sender.sendMessage(ChatColor.RED + "Lives & Revive Help");
		sender.sendMessage(ChatColor.RED + "/" + label + " give <player> <amount> - Gives lives to a player.");
		sender.sendMessage(ChatColor.RED + "/" + label + " [target] - Shows amount of lives that a payer has.");
	}
}

package com.prevailpots.kitmap.faction.argument;

import com.customhcf.util.JavaUtils;
import com.customhcf.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.struct.Relation;
import com.prevailpots.kitmap.faction.type.PlayerFaction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FactionDepositArgument extends CommandArgument {
    private static final ImmutableList<String> COMPLETIONS;

    static {
        COMPLETIONS = ImmutableList.of("all");
    }

    private final HCF plugin;

    public FactionDepositArgument(final HCF plugin) {
        super("deposit", "Deposit money into the faction's team balance.", new String[]{"d"});
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <all|amount>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player) sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if(playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        final UUID uuid = player.getUniqueId();
        final int playerBalance = this.plugin.getEconomyManager().getBalance(uuid);
        Integer amount;
        if(args[1].equalsIgnoreCase("all")) {
            amount = playerBalance;
        } else if((amount = Ints.tryParse(args[1])) == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        if(amount <= 0) {
            sender.sendMessage(ChatColor.RED + "You cannot deposit an negative int amount of money! Yikes.");
            return true;
        }
        if(playerBalance < amount) {
            sender.sendMessage(ChatColor.RED + "You need at least " + '$' + JavaUtils.format((Number) amount) + " to do this, you only have " + '$' + JavaUtils.format((Number) playerBalance) + '.');
            return true;
        }
        this.plugin.getEconomyManager().subtractBalance(uuid, amount);
        playerFaction.setBalance(playerFaction.getBalance() + amount);
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " has deposited " + ChatColor.GREEN + '$' + JavaUtils.format((Number) amount) + ChatColor.YELLOW + " into the faction balance.");
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (List<String>) ((args.length == 2) ? FactionDepositArgument.COMPLETIONS : Collections.emptyList());
    }
}

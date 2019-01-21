package com.prevailpots.hcf.faction.argument;

import com.customhcf.util.BukkitUtils;
import com.customhcf.util.chat.ClickAction;
import com.customhcf.util.chat.Text;
import com.customhcf.util.command.CommandArgument;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import com.prevailpots.hcf.faction.FactionExecutor;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionHelpArgument extends CommandArgument {
    private static final int HELP_PER_PAGE = 10;
    private final FactionExecutor executor;
    private ImmutableMultimap<Integer, Text> pages;

    public FactionHelpArgument(final FactionExecutor executor) {
        super("help", "View help on how to use factions.");
        this.executor = executor;
        this.isPlayerOnly = true;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            this.showPage(sender, label, 1);
            return true;
        }
        final Integer page = Ints.tryParse(args[1]);
        if(page == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        this.showPage(sender, label, page);
        return true;
    }

    private void showPage(final CommandSender sender, final String label, final int pageNumber) {
        if(this.pages == null) {
            final boolean isPlayer = sender instanceof Player;
            int val = 1;
            int count = 0;
            final Multimap<Integer, Text> pages = ArrayListMultimap.create();
            for(final CommandArgument argument : this.executor.getArguments()) {
                if(argument.equals((Object) this)) {
                    continue;
                }
                final String permission = argument.getPermission();
                if(permission != null && !sender.hasPermission(permission)) {
                    continue;
                }
                if(argument.isPlayerOnly() && !isPlayer) {
                    continue;
                }
                ++count;
                pages.get(val).add(new Text(ChatColor.GREEN + "   /" + label + ' ' + argument.getName() + ChatColor.GRAY + " - " + ChatColor.GRAY + argument.getDescription()).setColor(ChatColor.GRAY).setClick(ClickAction.SUGGEST_COMMAND, "/"+label +" "+argument.getName()));
                if(count % HELP_PER_PAGE != 0) {
                    continue;
                }
                ++val;
            }
            this.pages = ImmutableMultimap.copyOf(pages);
        }
        final int totalPageCount = this.pages.size() / HELP_PER_PAGE + 1;
        if(pageNumber < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1.");
            return;
        }
        if(pageNumber > totalPageCount) {
            sender.sendMessage(ChatColor.RED + "There are only " + totalPageCount + " pages.");
            return;
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + " Faction Help " + ChatColor.GRAY + "[" + pageNumber + '/' + totalPageCount + ']');
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        for(final Text message : this.pages.get(pageNumber)) {
            message.send(sender);
        }
        sender.sendMessage(ChatColor.GRAY + " Use " + ChatColor.GREEN + '/' + label + ' ' + this.getName() + " <#>"  + ChatColor.GRAY + " to view other pages.");
        if(pageNumber == 1){
            sender.sendMessage(ChatColor.GRAY + "You may click a command '"+ ChatColor.ITALIC + "instantly" + ChatColor.GRAY + "' preform it.");
        }
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
}

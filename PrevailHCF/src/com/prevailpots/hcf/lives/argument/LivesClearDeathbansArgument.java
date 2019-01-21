package com.prevailpots.hcf.lives.argument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.customhcf.util.command.CommandArgument;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.user.FactionUser;

public class LivesClearDeathbansArgument extends CommandArgument {
    private final HCF plugin;

    public LivesClearDeathbansArgument(final HCF plugin) {
        super("cleardeathbans", "Clears the global deathbans");
        this.plugin = plugin;
        this.aliases = new String[]{"resetdeathbans"};
        this.permission = "command.stafflives." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(sender instanceof ConsoleCommandSender)  {
            for (final FactionUser user : this.plugin.getUserManager().getUsers().values()) {
                user.setDeathban(null);
            }
            Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "All death-bans have been cleared.");
            return true;
        }else{
            sender.sendMessage(ChatColor.RED + "Must be console");
            return false;
        }
    }
}

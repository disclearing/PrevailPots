package com.prevailpots.kitmap.kothgame.koth.argument;

import com.customhcf.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by TREHOME on 01/06/2016.
 */
public class KothShowArgument extends CommandArgument {

        public KothShowArgument() {
            super("show", "View the information on a koth");
        }

        public String getUsage(final String label) {
            return '/' + label + ' ' + this.getName();
        }

        public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args){
           if(args.length != 1){
               sender.sendMessage(ChatColor.RED + "Usage: /"+label+" "+getName()+" <kothName>");
               return true;
           }
            Bukkit.dispatchCommand(sender, "f who " +args[1]);
            return true;
        }
}

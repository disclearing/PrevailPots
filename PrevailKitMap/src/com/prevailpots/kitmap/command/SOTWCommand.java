package com.prevailpots.kitmap.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.prevailpots.kitmap.HCF;

import java.util.concurrent.TimeUnit;

/**
 * Created by HelpMe on 12/11/2015.
 */
public class SOTWCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("start")){
                HCF.getPlugin().getTimerManager().sotw.setRemaining(TimeUnit.HOURS.toMillis(2), true);
                HCF.getPlugin().getTimerManager().sotw.setPaused(false);
                cs.sendMessage(ChatColor.YELLOW + "SOTW started.");
            }else if(args[0].equalsIgnoreCase("end")) {
                HCF.getPlugin().getTimerManager().sotw.clearCooldown();
                cs.sendMessage(ChatColor.YELLOW + "SOTW stopped.");
            }else if(args[0].equalsIgnoreCase("pause")){
                HCF.getPlugin().getTimerManager().sotw.setPaused(true);
                cs.sendMessage(ChatColor.YELLOW + "SOTW paused.");
            }else{
                cs.sendMessage(ChatColor.RED +"Usage: /"+s+" start|end|pause");
            }
        }
        return false;
    }
}
